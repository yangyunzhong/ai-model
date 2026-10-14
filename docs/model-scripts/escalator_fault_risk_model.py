# -*- coding: utf-8 -*-
"""自动扶梯故障风险预测模型（测试脚本）

平台约定：
- 入口函数 execute(params) -> dict，params 由平台透传（通常含 deviceId，可为空表示全量）
- 通过平台 SDK platform_db 只读查询平台数据库（ai-model 库）
- 结果 dict 必须包含 devices 列表，每台设备输出：
  deviceId / healthScore / healthLevel（平台落库约定，healthScore=100-风险分）
  dimBody / dimCondition / dimFault / dimMaintain / dimEnv（五维分，此处为风险维度）
  以及模型注册时配置的输出参数同名字段：
  faultRiskScore / faultProbability / riskLevel / riskFactors / maintainAdvice

数据来源：
- metro_device_realtime  实时监测（超限次数 / 波动率 / 趋势）
- metro_device_threshold 阈值配置
- metro_device_summary   运行汇总（历史故障次数 / 重复故障 / 维保超期）

算法：故障风险预测 —— 与健康评估模型不同，聚焦"未来出故障的可能性"：
风险 = 超限严重度(0.30) + 波动失稳(0.25) + 劣化趋势(0.20) + 历史故障(0.25)。
"""

import platform_db

# 阈值表 item_name 与默认阈值（单位与阈值表保持一致）
DEFAULT_THRESHOLDS = {
    "振动值上限": 4.5,
    "电机温度上限": 85.0,
    "减速箱温度上限": 95.0,
    "工作电流上限": 16.0,
    "运行功率上限": 13.0,
}

# 指标名 -> realtime 列名
ITEM_COLUMN = {
    "振动值上限": "real_vibration",
    "电机温度上限": "real_motor_temp",
    "减速箱温度上限": "real_gearbox_temp",
    "工作电流上限": "working_current",
    "运行功率上限": "real_power",
}

RUNNING_STATUS_FAULT = {2, 4}  # 故障 / 下架直接视为高风险状态


def execute(params):
    params = params or {}
    device_id = params.get("deviceId") or params.get("device_id")

    devices = _list_devices(device_id)
    if not devices:
        return {
            "success": False,
            "message": "未找到可评估的自动扶梯（device_type=1 且有监测数据）",
            "devices": [],
        }

    results = [ _predict_device(dev) for dev in devices ]
    return {"success": True, "message": "自动扶梯故障风险预测完成", "devices": results}


def _list_devices(device_id):
    """仅自动扶梯（device_type=1），且须有实时监测数据。"""
    if device_id:
        rows = platform_db.query(
            "metro_device",
            columns=["id"],
            where={"id": device_id, "device_type": 1},
        )
        return [r["id"] for r in rows]
    escalators = platform_db.query(
        "metro_device",
        columns=["id"],
        where={"device_type": 1},
        order_by="id",
    )
    valid = {r["id"] for r in escalators}
    monitored = platform_db.query("metro_device_realtime", columns=["device_id"])
    devices = []
    for row in monitored:
        did = row.get("device_id")
        if did in valid and did not in devices:
            devices.append(did)
    return devices


def _load_thresholds(device_id):
    thresholds = dict(DEFAULT_THRESHOLDS)
    rows = platform_db.query(
        "metro_device_threshold",
        columns=["item_name", "item_value"],
        where={"fk_device": device_id},
    )
    for row in rows:
        try:
            thresholds[row.get("item_name")] = float(row.get("item_value"))
        except (TypeError, ValueError):
            continue
    return thresholds


def _load_series(device_id, count=10):
    """最近 count 条监测记录（按时间升序取末尾）。"""
    rows = platform_db.query(
        "metro_device_realtime",
        where={"device_id": device_id},
        order_by="monitor_time",
        limit=count,
    )
    return rows[-count:] if rows else []


def _slope(values):
    n = len(values)
    if n < 2:
        return 0.0
    mean_x = (n - 1) / 2.0
    mean_y = sum(values) / n
    numerator = sum((i - mean_x) * (y - mean_y) for i, y in enumerate(values))
    denominator = sum((i - mean_x) ** 2 for i in range(n))
    return numerator / denominator if denominator else 0.0


def _predict_device(device_id):
    thresholds = _load_thresholds(device_id)
    series = _load_series(device_id)
    if not series:
        return _empty_result(device_id, "无监测数据")

    latest = series[-1]
    running_status = latest.get("running_status")

    # ---- 因子 1：超限严重度（各指标超阈值比例 + 最大超出幅度）----
    breach_ratio_total = 0.0
    max_over_extent = 0.0
    breach_items = []
    checked = 0
    for item_name, column in ITEM_COLUMN.items():
        limit = thresholds.get(item_name)
        values = [float(r[column]) for r in series if r.get(column) is not None]
        if not limit or not values:
            continue
        checked += 1
        breaches = [v for v in values if v > limit]
        if breaches:
            breach_ratio_total += len(breaches) / len(values)
            extent = max(breaches) / limit - 1.0
            if extent > max_over_extent:
                max_over_extent = extent
            if len(breaches) > 0:
                breach_items.append(item_name)
    breach_score = min(100.0,
                       (breach_ratio_total / max(checked, 1)) * 100.0 * 1.2 + max_over_extent * 60.0)

    # ---- 因子 2：波动失稳（各指标变异系数 CV 均值）----
    cvs = []
    for column in ITEM_COLUMN.values():
        values = [float(r[column]) for r in series if r.get(column) is not None]
        if len(values) >= 3 and sum(values) > 0:
            mean_v = sum(values) / len(values)
            variance = sum((v - mean_v) ** 2 for v in values) / len(values)
            cvs.append((variance ** 0.5) / mean_v)
    volatility_score = min(100.0, (sum(cvs) / len(cvs)) * 100.0 * 4.0) if cvs else 10.0

    # ---- 因子 3：劣化趋势（振动斜率相对阈值的增长速度）----
    vib_values = [float(r["real_vibration"]) for r in series if r.get("real_vibration") is not None]
    slope = _slope(vib_values) if vib_values else 0.0
    trend_score = min(100.0, max(slope, 0.0) / thresholds["振动值上限"] * 100.0 * 10.0)

    # ---- 因子 4：历史故障（汇总表故障次数 / 重复故障 / 维保超期）----
    history_score = 10.0
    history_factors = []
    summary_rows = platform_db.query(
        "metro_device_summary",
        where={"device_id": device_id},
    )
    if summary_rows:
        s = summary_rows[-1]
        fault_count = int(s.get("fault_count") or 0)
        repeat = int(s.get("repeat_fault") or 0)
        overdue = int(s.get("overdue_days") or 0)
        history_score = min(100.0, fault_count * 18.0 + repeat * 25.0 + overdue * 4.0 + 10.0)
        if fault_count > 0:
            history_factors.append(f"近一年故障 {fault_count} 次")
        if repeat:
            history_factors.append("存在重复故障")
        if overdue > 0:
            history_factors.append(f"维保超期 {overdue} 天")

    # ---- 综合风险分 ----
    fault_risk = round(
        breach_score * 0.30 + volatility_score * 0.25
        + trend_score * 0.20 + history_score * 0.25, 1)
    fault_risk = min(fault_risk, 100.0)
    if int(running_status or 1) in RUNNING_STATUS_FAULT:
        fault_risk = max(fault_risk, 75.0)

    # ---- 输出 ----
    probability_map = [
        (30.0, 5.0),    # 低风险：30 天内故障概率约 5%
        (60.0, 20.0),   # 中风险：约 20%
    ]
    if fault_risk < probability_map[0][0]:
        probability = probability_map[0][1]
        risk_level = "低"
    elif fault_risk < probability_map[1][0]:
        probability = probability_map[1][1]
        risk_level = "中"
    else:
        probability = 45.0
        risk_level = "高"

    risk_factors = []
    if breach_items:
        risk_factors.append("超阈值指标: " + "、".join(breach_items))
    if volatility_score >= 40.0:
        risk_factors.append("运行参数波动异常")
    if trend_score >= 40.0:
        risk_factors.append("振动呈上升趋势")
    risk_factors.extend(history_factors)
    if not risk_factors:
        risk_factors.append("各项指标平稳")

    health_score = round(100.0 - fault_risk, 1)

    return {
        "deviceId": device_id,
        "healthScore": health_score,
        "healthLevel": _health_level(health_score),
        "dimBody": round(100.0 - breach_score, 1),
        "dimCondition": round(100.0 - volatility_score, 1),
        "dimFault": round(100.0 - trend_score, 1),
        "dimMaintain": round(100.0 - history_score, 1),
        "dimEnv": round(max(60.0, 100.0 - fault_risk * 0.5), 1),
        "faultRiskScore": fault_risk,
        "faultProbability": probability,
        "riskLevel": risk_level,
        "riskFactors": "；".join(risk_factors),
        "maintainAdvice": _advice(risk_level, breach_items, trend_score),
    }


def _advice(risk_level, breach_items, trend_score):
    if risk_level == "高":
        base = "故障风险高，建议 3 日内安排专项检查并降低载客负荷"
    elif risk_level == "中":
        base = "故障风险中等，建议纳入本周巡检重点跟踪"
    else:
        base = "运行风险低，按既定周期维保即可"
    if "振动值上限" in breach_items or trend_score >= 40.0:
        base += "；重点检查梯级链与导轨系统"
    return base


def _health_level(score):
    if score >= 80:
        return "healthy"
    if score >= 60:
        return "subhealthy"
    return "danger"


def _empty_result(device_id, reason):
    return {
        "deviceId": device_id,
        "healthScore": None, "healthLevel": None,
        "dimBody": None, "dimCondition": None, "dimFault": None,
        "dimMaintain": None, "dimEnv": None,
        "faultRiskScore": None, "faultProbability": None,
        "riskLevel": None, "riskFactors": reason, "maintainAdvice": None,
    }
