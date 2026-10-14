# -*- coding: utf-8 -*-
"""垂直电梯健康评估模型（测试脚本）

平台约定：
- 入口函数 execute(params) -> dict，params 由平台透传（通常含 deviceId，可为空表示全量）
- 通过平台 SDK platform_db 只读查询平台数据库（ai-model 库）
- 结果 dict 必须包含 devices 列表，每台设备输出：
  deviceId / healthScore / healthLevel
  dimBody / dimCondition / dimFault / dimMaintain / dimEnv（五维分，0~100）
  以及模型注册时配置的输出参数同名字段：remainingLifeDays / riskLevel / maintainAdvice

数据来源：
- metro_device_realtime       设备实时监测数据（电流/速度/电机温度/功率）
- metro_device_threshold      设备阈值配置（item_name + item_value 键值对）
- metro_device_health_history 设备历史健康记录（维保维度参考最近 7 天 s4 得分）

算法：与扶梯脚本差异化 —— 采用趋势斜率法：
- 读最近 6 条监测数据，对电机温度、振动做线性趋势分析，温升/振升趋势扣分；
- 电流波动率（标准差/均值）反映工况稳定性；
- 维保维度参考历史健康记录中维保检修维度（s4）均值。
"""

import platform_db

# 阈值表 item_name 与默认阈值（单位与阈值表保持一致）
DEFAULT_THRESHOLDS = {
    "振动值上限": 2.2,        # mm/s（电梯标准比扶梯严）
    "电机温度上限": 75.0,     # ℃
    "减速箱温度上限": 80.0,   # ℃
    "工作电流上限": 12.0,     # A
    "运行功率上限": 14.0,     # kW
}

# 运行状态分（running_status: 1-正常 2-故障 3-维护 4-下架）
RUNNING_STATUS_SCORE = {1: 96.0, 2: 40.0, 3: 68.0, 4: 25.0}

# 五维权重（本体 / 工况 / 故障 / 维保 / 环境）
WEIGHTS = {
    "dimBody": 0.30,
    "dimCondition": 0.22,
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
    return {"success": True, "message": "垂直电梯健康评估完成", "devices": results}


def _list_devices(device_id):
    """圈定待评估设备：仅垂直电梯（device_type=2），且须有实时监测数据。"""
    if device_id:
        rows = platform_db.query(
            "metro_device",
            columns=["id"],
            where={"id": device_id, "device_type": 2},
        )
        return [r["id"] for r in rows]
    rows = platform_db.query(
        "metro_device",
        columns=["id"],
        where={"device_type": 2},
        order_by="id",
    )
    elevators = [r["id"] for r in rows]
    monitored = platform_db.query(
        "metro_device_realtime",
        columns=["device_id"],
    )
    devices = []
    for row in monitored:
        did = row.get("device_id")
        if did in elevators and did not in devices:
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


def _load_series(device_id, count=6):
    """读取最近 count 条监测记录（按时间升序），并返回均值与序列。"""
    rows = platform_db.query(
        "metro_device_realtime",
        where={"device_id": device_id},
        order_by="monitor_time",
        limit=count,
    )
    if not rows:
        return None, None
    rows = rows[-count:]

    series = {"real_vibration": [], "working_current": [], "real_speed": [],
              "real_motor_temp": [], "real_gearbox_temp": [], "real_power": []}
    latest = rows[-1]
    for row in rows:
        for key in series:
            value = row.get(key)
            if value is not None:
                series[key].append(float(value))
    averages = {key: (sum(values) / len(values) if values else None)
                for key, values in series.items()}
    return latest, (series, averages)


def _slope(values):
    """一阶线性趋势斜率（单位：每条记录的变化量）。"""
    n = len(values)
    if n < 2:
        return 0.0
    mean_x = (n - 1) / 2.0
    mean_y = sum(values) / n
    numerator = sum((i - mean_x) * (y - mean_y) for i, y in enumerate(values))
    denominator = sum((i - mean_x) ** 2 for i in range(n))
    return numerator / denominator if denominator else 0.0


def _deviation_score(value, limit):
    """按实测均值与上限的比值映射为 0~100 分。"""
    if value is None or not limit:
        return None
    ratio = float(value) / float(limit)
    if ratio <= 0.6:
        return 100.0
    if ratio >= 1.2:
        return 40.0
    return round(100.0 - (ratio - 0.6) / 0.6 * 60.0, 1)


def _trend_penalty(values, limit):
    """趋势扣分：斜率为正（持续上升）且接近上限时扣分，返回 0~30。"""
    if not values or not limit:
        return 0.0
    slope = _slope(values)
    if slope <= 0:
        return 0.0
    headroom = max(0.0, (limit - values[-1]) / limit)  # 距上限的剩余空间比例
    if headroom <= 0:
        return 30.0
    penalty = slope / limit * 100.0 * 6.0 * (1.0 - headroom)
    return min(round(penalty, 1), 30.0)


def _evaluate_device(device_id):
    thresholds = _load_thresholds(device_id)
    latest, packed = _load_series(device_id)

    if latest is None:
        return _empty_result(device_id, "无监测数据")

    series, avg = packed
    running_status = latest.get("running_status")

    # 本体维度：振动均值分 - 温升趋势扣分（电梯对振动更敏感）
    dim_body = _deviation_score(avg.get("real_vibration"), thresholds["振动值上限"])
    if dim_body is None:
        dim_body = 75.0
    dim_body = round(dim_body - _trend_penalty(series["real_vibration"], thresholds["振动值上限"]), 1)

    # 工况维度：电流均值分 + 电流波动率（稳定性）
    dim_condition = _deviation_score(avg.get("working_current"), thresholds["工作电流上限"])
    if dim_condition is None:
        dim_condition = 75.0
    currents = series["working_current"]
    if len(currents) >= 3 and sum(currents) > 0:
        mean_i = sum(currents) / len(currents)
        variance = sum((v - mean_i) ** 2 for v in currents) / len(currents)
        cv = (variance ** 0.5) / mean_i
        dim_condition = round(dim_condition - cv * 100.0 * 0.3, 1)
    dim_condition = max(dim_condition, 40.0)

    # 故障维度：运行状态 + 监测状态
    dim_fault = RUNNING_STATUS_SCORE.get(int(running_status) if running_status else 1, 70.0)
    if latest.get("monitor_status") and str(latest["monitor_status"]) in ("报警", "异常"):
        dim_fault = round(dim_fault - 10.0, 1)

    # 维保维度：电机/减速箱温度表现 + 历史维保检修（s4）均值
    motor_score = _deviation_score(avg.get("real_motor_temp"), thresholds["电机温度上限"])
    gearbox_score = _deviation_score(avg.get("real_gearbox_temp"), thresholds["减速箱温度上限"])
    maintain_scores = [motor_score, gearbox_score]
    history_s4 = _load_history_maintain(device_id)
    if history_s4 is not None:
        maintain_scores.append(history_s4)
    dim_maintain = _mean_ignore_none(maintain_scores) or 80.0

    # 环境维度：减速箱/电机温度散差（散热环境稳定性）
    temps = [s for s in [motor_score, gearbox_score] if s is not None]
    if len(temps) >= 2:
        dim_env = max(round(95.0 - (max(temps) - min(temps)) * 0.6, 1), 40.0)
    else:
        dim_env = 85.0

    health_score = round(
        max(dim_body, 30) * WEIGHTS["dimBody"] + max(dim_condition, 30) * WEIGHTS["dimCondition"]
        + dim_fault * WEIGHTS["dimFault"] + max(dim_maintain, 30) * WEIGHTS["dimMaintain"]
        + dim_env * WEIGHTS["dimEnv"], 1)

    result = {
        "deviceId": device_id,
        "healthScore": health_score,
        "healthLevel": _health_level(health_score),
        "dimBody": round(max(dim_body, 30), 1),
        "dimCondition": round(dim_condition, 1),
        "dimFault": round(dim_fault, 1),
        "dimMaintain": round(dim_maintain, 1),
        "dimEnv": round(dim_env, 1),
        "remainingLifeDays": int(health_score / 100.0 * 7300.0),
        "riskLevel": _risk_level(health_score),
        "maintainAdvice": _advice(dim_body, dim_condition, dim_fault, dim_maintain),
    }
    return result


def _load_history_maintain(device_id, days=7):
    """读取最近 days 天健康历史中维保检修维度（s4）均值，无历史返回 None。"""
    rows = platform_db.query(
        "metro_device_health_history",
        columns=["s4_score"],
        where={"device_id": device_id},
        order_by="record_date",
        limit=days,
    )
    scores = [float(r["s4_score"]) for r in rows if r.get("s4_score") is not None]
    return round(sum(scores) / len(scores), 1) if scores else None


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
        "振动/本体": "振动存在上升趋势，建议检查导靴磨损与轿厢平衡系数",
        "电流/工况": "电流波动较大，建议检查门机系统与曳引机负载",
        "运行状态": "设备处于故障/维护状态，建议尽快完成检修并复归运行",
        "温度/维保": "电机温度裕度不足，建议检查机房通风与维保记录执行情况",
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
