# -*- coding: utf-8 -*-
"""自动扶梯健康评估模型（测试脚本）

平台约定：
- 入口函数 execute(params) -> dict，params 由平台透传（通常含 deviceId，可为空表示全量）
- 通过平台 SDK platform_db 只读查询平台数据库（ai-model 库）
- 结果 dict 必须包含 devices 列表，每台设备输出：
  deviceId / healthScore / healthLevel
  dimBody / dimCondition / dimFault / dimMaintain / dimEnv（五维分，0~100）
  以及模型注册时配置的输出参数同名字段：remainingLifeDays / riskLevel / maintainAdvice

数据来源：
- metro_device_realtime  设备实时监测数据（振动/电流/速度/电机温度/减速箱温度/功率）
- metro_device_threshold 设备阈值配置（item_name + item_value 键值对）
- metro_device           设备主数据（running_status 运行状态）

算法：加权评分法 —— 各项实测值与阈值比较得到偏差率，映射为五维得分后加权求和。
"""

import platform_db

# 阈值表 item_name 与默认阈值（单位与阈值表保持一致）
DEFAULT_THRESHOLDS = {
    "振动值上限": 4.5,        # mm/s
    "电机温度上限": 85.0,     # ℃
    "减速箱温度上限": 95.0,   # ℃
    "工作电流上限": 16.0,     # A
    "运行功率上限": 13.0,     # kW
}

# 运行状态分（running_status: 1-正常 2-故障 3-维护 4-下架）
RUNNING_STATUS_SCORE = {1: 96.0, 2: 45.0, 3: 70.0, 4: 30.0}

# 五维权重（本体 / 工况 / 故障 / 维保 / 环境）
WEIGHTS = {
    "dimBody": 0.28,
    "dimCondition": 0.24,
    "dimFault": 0.22,
    "dimMaintain": 0.16,
    "dimEnv": 0.10,
}


def execute(params):
    params = params or {}
    device_id = params.get("deviceId") or params.get("device_id")

    devices = _list_devices(device_id)
    if not devices:
        return {
            "success": False,
            "message": "未找到可评估的设备（realtime 表无监测数据）",
            "devices": [],
        }

    results = []
    for dev in devices:
        results.append(_evaluate_device(dev))
    return {"success": True, "message": "自动扶梯健康评估完成", "devices": results}


def _list_devices(device_id):
    """圈定待评估设备：仅自动扶梯（device_type=1），且须有实时监测数据。"""
    if device_id:
        rows = platform_db.query(
            "metro_device",
            columns=["id"],
            where={"id": device_id, "device_type": 1},
        )
        return [r["id"] for r in rows]
    rows = platform_db.query(
        "metro_device",
        columns=["id"],
        where={"device_type": 1},
        order_by="id",
    )
    escalators = [r["id"] for r in rows]
    monitored = platform_db.query(
        "metro_device_realtime",
        columns=["device_id"],
    )
    devices = []
    for row in monitored:
        did = row.get("device_id")
        if did in escalators and did not in devices:
            devices.append(did)
    return devices


def _load_thresholds(device_id):
    """读取设备阈值配置，缺失项回退默认值。"""
    thresholds = dict(DEFAULT_THRESHOLDS)
    rows = platform_db.query(
        "metro_device_threshold",
        columns=["item_name", "item_value"],
        where={"fk_device": device_id},
    )
    for row in rows:
        name = row.get("item_name")
        try:
            thresholds[name] = float(row.get("item_value"))
        except (TypeError, ValueError):
            continue
    return thresholds


def _load_latest_metrics(device_id, count=6):
    """读取最近 count 条监测记录并取均值（忽略 NULL 字段）。"""
    rows = platform_db.query(
        "metro_device_realtime",
        where={"device_id": device_id},
        order_by="monitor_time",
        limit=count,
    )
    if not rows:
        return None, None
    # order_by 仅支持单列，这里升序后取末尾 count 条即为最新数据
    rows = rows[-count:]

    metrics = {"real_vibration": [], "working_current": [], "real_speed": [],
               "real_motor_temp": [], "real_gearbox_temp": [], "real_power": []}
    latest = rows[-1]
    for row in rows:
        for key in metrics:
            value = row.get(key)
            if value is not None:
                metrics[key].append(float(value))
    averages = {key: (sum(values) / len(values) if values else None)
                for key, values in metrics.items()}
    return latest, averages


def _deviation_score(value, limit, headroom=1.0):
    """按实测值与上限的比值映射为 0~100 分：比值越低分越高。"""
    if value is None or not limit:
        return None
    ratio = float(value) / float(limit)
    if ratio <= 0.6:
        return 100.0
    if ratio >= 1.2:
        return 40.0
    # 0.6~1.2 线性衰减：100 -> 40
    return round(100.0 - (ratio - 0.6) / 0.6 * 60.0, 1)


def _evaluate_device(device_id):
    thresholds = _load_thresholds(device_id)
    latest, avg = _load_latest_metrics(device_id)

    if latest is None:
        return _empty_result(device_id, "无监测数据")

    running_status = latest.get("running_status")

    # 本体维度：振动值与上限的偏差（最核心指标）
    dim_body = _deviation_score(avg.get("real_vibration"), thresholds["振动值上限"])
    if dim_body is None:
        dim_body = 75.0

    # 工况维度：电流、功率、速度相对额定/上限的表现
    current_score = _deviation_score(avg.get("working_current"), thresholds["工作电流上限"])
    power_score = _deviation_score(avg.get("real_power"), thresholds["运行功率上限"])
    dim_condition = _mean_ignore_none([current_score, power_score]) or 75.0

    # 故障维度：以运行状态为主，监测状态异常适度扣分
    dim_fault = RUNNING_STATUS_SCORE.get(int(running_status) if running_status else 1, 75.0)
    if latest.get("monitor_status") and str(latest["monitor_status"]) in ("报警", "异常"):
        dim_fault = round(dim_fault - 10.0, 1)

    # 维保维度：电机/减速箱温度裕度（温度越高散热与维保表现越差）
    motor_score = _deviation_score(avg.get("real_motor_temp"), thresholds["电机温度上限"])
    gearbox_score = _deviation_score(avg.get("real_gearbox_temp"), thresholds["减速箱温度上限"])
    dim_maintain = _mean_ignore_none([motor_score, gearbox_score]) or 80.0

    # 环境维度：简化为综合劣化度（各项均值分的离散程度，波动大则环境/工况不稳）
    all_scores = [s for s in [dim_body, current_score, power_score, motor_score, gearbox_score] if s is not None]
    if len(all_scores) >= 2:
        spread = max(all_scores) - min(all_scores)
        dim_env = round(95.0 - spread * 0.8, 1)
    else:
        dim_env = 85.0
    dim_env = max(dim_env, 40.0)

    health_score = round(
        dim_body * WEIGHTS["dimBody"] + dim_condition * WEIGHTS["dimCondition"]
        + dim_fault * WEIGHTS["dimFault"] + dim_maintain * WEIGHTS["dimMaintain"]
        + dim_env * WEIGHTS["dimEnv"], 1)

    result = {
        "deviceId": device_id,
        "healthScore": health_score,
        "healthLevel": _health_level(health_score),
        "dimBody": round(dim_body, 1),
        "dimCondition": round(dim_condition, 1),
        "dimFault": round(dim_fault, 1),
        "dimMaintain": round(dim_maintain, 1),
        "dimEnv": round(dim_env, 1),
        "remainingLifeDays": int(health_score / 100.0 * 3650.0),
        "riskLevel": _risk_level(health_score),
        "maintainAdvice": _advice(dim_body, dim_condition, dim_fault, dim_maintain),
    }
    return result


def _health_level(score):
    if score >= 80:
        return "healthy"
    if score >= 60:
        return "subhealthy"
    return "danger"


def _risk_level(score):
    if score >= 80:
        return "低"
    if score >= 60:
        return "中"
    return "高"


def _advice(dim_body, dim_condition, dim_fault, dim_maintain):
    dims = {"振动/本体": dim_body, "电流/工况": dim_condition,
            "运行状态": dim_fault, "温度/维保": dim_maintain}
    weakest = min(dims, key=lambda k: dims[k])
    advice_map = {
        "振动/本体": "振动值接近上限，建议检查梯级链张紧度与导轨润滑",
        "电流/工况": "工作电流/功率偏高，建议检查电机负载与传动效率",
        "运行状态": "设备处于故障/维护状态，建议尽快完成检修并复归运行",
        "温度/维保": "电机或减速箱温度裕度不足，建议检查散热与润滑油状况",
    }
    return advice_map[weakest]


def _mean_ignore_none(values):
    valid = [v for v in values if v is not None]
    return round(sum(valid) / len(valid), 1) if valid else None


def _empty_result(device_id, reason):
    return {
        "deviceId": device_id,
        "healthScore": None,
        "healthLevel": None,
        "dimBody": None, "dimCondition": None, "dimFault": None,
        "dimMaintain": None, "dimEnv": None,
        "remainingLifeDays": None,
        "riskLevel": None,
        "maintainAdvice": reason,
    }
