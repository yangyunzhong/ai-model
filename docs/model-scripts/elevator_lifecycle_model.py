# -*- coding: utf-8 -*-
"""垂直电梯剩余寿命预测模型（测试脚本）

平台约定：
- 入口函数 execute(params) -> dict，params 由平台透传（通常含 deviceId，可为空表示全量）
- 通过平台 SDK platform_db 只读查询平台数据库（ai-model 库）
- 结果 dict 必须包含 devices 列表，每台设备输出：
  deviceId / healthScore / healthLevel（平台落库约定，取最新健康分）
  dimBody / dimCondition / dimFault / dimMaintain / dimEnv（五维分，取自最新历史）
  以及模型注册时配置的输出参数同名字段：
  remainingLifeDays / predictedDate / degradeLevel / annualDegradeRate / maintainAdvice

数据来源：
- metro_device_health_history 健康度日历史（total_score 序列 → 劣化速率）
- metro_device_realtime       实时监测（当前状态修正）
- metro_device_threshold      阈值配置（当前超限修正）

算法：剩余寿命预测 —— 与健康评估/风险预测模型不同，基于历史健康分序列做线性回归：
剩余寿命 = (当前分 - 报废线60分) / 每日劣化速率，再按当前运行状态修正。
"""

import platform_db

# 健康分报废线（低于该分数视为需大修/更换）
SCRAP_SCORE = 60.0

# 寿命预测上限（天）：健康分趋势向好或长期稳定时给出上限值
MAX_LIFE_DAYS = 3650

# 阈值表默认值（当前超限修正用）
DEFAULT_THRESHOLDS = {
    "振动值上限": 2.2,
    "电机温度上限": 75.0,
    "减速箱温度上限": 80.0,
    "工作电流上限": 12.0,
    "运行功率上限": 14.0,
}

ITEM_COLUMN = {
    "振动值上限": "real_vibration",
    "电机温度上限": "real_motor_temp",
    "减速箱温度上限": "real_gearbox_temp",
    "工作电流上限": "working_current",
    "运行功率上限": "real_power",
}


def execute(params):
    params = params or {}
    device_id = params.get("deviceId") or params.get("device_id")

    devices = _list_devices(device_id)
    if not devices:
        return {
            "success": False,
            "message": "未找到可评估的垂直电梯（device_type=2 且有健康历史）",
            "devices": [],
        }

    results = [_predict_device(dev) for dev in devices]
    return {"success": True, "message": "垂直电梯剩余寿命预测完成", "devices": results}


def _list_devices(device_id):
    """仅垂直电梯（device_type=2），且须有健康历史记录。"""
    if device_id:
        rows = platform_db.query(
            "metro_device",
            columns=["id"],
            where={"id": device_id, "device_type": 2},
        )
        return [r["id"] for r in rows]
    elevators = platform_db.query(
        "metro_device",
        columns=["id"],
        where={"device_type": 2},
        order_by="id",
    )
    valid = {r["id"] for r in elevators}
    history = platform_db.query("metro_device_health_history", columns=["device_id"])
    devices = []
    for row in history:
        did = row.get("device_id")
        if did in valid and did not in devices:
            devices.append(did)
    return devices


def _load_history_scores(device_id, days=30):
    """读取最近 days 条健康历史总分（record_date 升序）。"""
    rows = platform_db.query(
        "metro_device_health_history",
        columns=["record_date", "total_score", "s1_score", "s2_score",
                 "s3_score", "s4_score", "s5_score"],
        where={"device_id": device_id},
        order_by="record_date",
        limit=days,
    )
    return rows


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


def _slope_per_day(rows):
    """健康分对日期序号的线性回归斜率（分/天）。"""
    n = len(rows)
    if n < 2:
        return 0.0
    xs = list(range(n))
    mean_x = sum(xs) / n
    mean_y = sum(float(r.get("total_score") or 0) for r in rows) / n
    numerator = sum((x - mean_x) * (float(r.get("total_score") or 0) - mean_y)
                    for x, r in zip(xs, rows))
    denominator = sum((x - mean_x) ** 2 for x in xs)
    return numerator / denominator if denominator else 0.0


def _predict_device(device_id):
    history = _load_history_scores(device_id)
    if not history:
        return _empty_result(device_id, "无健康历史记录")

    latest = history[-1]
    current_score = float(latest.get("total_score") or 0)
    slope = _slope_per_day(history)

    # ---- 劣化速率（取负斜率的绝对值；趋势向好时速率为 0）----
    degrade_per_day = max(-slope, 0.0)

    # ---- 当前状态修正：故障状态下寿命折减 ----
    correction_days = 0.0
    realtime_rows = platform_db.query(
        "metro_device_realtime",
        where={"device_id": device_id},
        order_by="monitor_time",
        limit=6,
    )
    running_status = 1
    breach = False
    if realtime_rows:
        latest_rt = realtime_rows[-1]
        running_status = int(latest_rt.get("running_status") or 1)
        thresholds = _load_thresholds(device_id)
        for item_name, column in ITEM_COLUMN.items():
            limit = thresholds.get(item_name)
            value = latest_rt.get(column)
            if limit and value is not None and float(value) > limit:
                breach = True
                break

    if running_status == 2:
        correction_days = -180.0   # 故障运行：寿命折减约半年
    elif running_status == 3:
        correction_days = -60.0
    elif breach:
        correction_days = -90.0    # 当前有指标超限：折减一个季度

    # ---- 剩余寿命 ----
    if current_score <= SCRAP_SCORE:
        remaining_days = 0
    elif degrade_per_day <= 0.0001:
        remaining_days = MAX_LIFE_DAYS if slope >= 0 else int((current_score - SCRAP_SCORE) / 0.01)
    else:
        remaining_days = int((current_score - SCRAP_SCORE) / degrade_per_day)
    remaining_days = max(0, min(remaining_days + int(correction_days), MAX_LIFE_DAYS))

    # ---- 预测日期 ----
    from datetime import datetime, timedelta
    predicted_date = (datetime.now() + timedelta(days=remaining_days)).strftime("%Y-%m-%d")

    # ---- 劣化等级（年化劣化分数）----
    annual_rate = round(degrade_per_day * 365.0, 1)
    if annual_rate <= 2.0:
        degrade_level = "稳定"
    elif annual_rate <= 8.0:
        degrade_level = "缓慢劣化"
    else:
        degrade_level = "快速劣化"

    advice = _advice(degrade_level, remaining_days, running_status, breach)

    return {
        "deviceId": device_id,
        "healthScore": current_score,
        "healthLevel": _health_level(current_score),
        "dimBody": latest.get("s1_score"),
        "dimCondition": latest.get("s2_score"),
        "dimFault": latest.get("s3_score"),
        "dimMaintain": latest.get("s4_score"),
        "dimEnv": latest.get("s5_score"),
        "remainingLifeDays": remaining_days,
        "predictedDate": predicted_date,
        "degradeLevel": degrade_level,
        "annualDegradeRate": annual_rate,
        "maintainAdvice": advice,
    }


def _advice(degrade_level, remaining_days, running_status, breach):
    if running_status == 2:
        return "设备当前处于故障状态，恢复运行后重新评估寿命；建议优先完成检修"
    if degrade_level == "快速劣化":
        return f"健康分下降过快，预计 {remaining_days} 天后触及报废线，建议立即安排深度维保并排查劣化根因"
    if degrade_level == "缓慢劣化":
        return f"呈缓慢劣化趋势，预计 {remaining_days} 天后触及报废线，建议加强季度维保质量"
    if breach:
        return "当前存在指标超限，趋势修正后寿命有所折减，建议关注超限指标"
    return f"健康状态稳定，预计 {remaining_days} 天内无需大修，按计划维保即可"


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
        "remainingLifeDays": None, "predictedDate": None,
        "degradeLevel": None, "annualDegradeRate": None,
        "maintainAdvice": reason,
    }
