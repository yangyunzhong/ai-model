# -*- coding: utf-8 -*-
"""
模型平台示例脚本：从数据库表读取「输入参数」数据，计算后返回「输出参数」数据。

平台契约
1. 入口函数固定为 execute(params)，params 为 dict，key = 注册窗口「输入参数 - 参数标识」。
2. 脚本内自行指定要读取的表名（变量 TABLE_NAME），平台为只读访问。
3. platform_db.load_inputs(TABLE_NAME) 按「输入参数 paramKey」从表中读取同名字段：
   - 表中存在同名列且有值 -> 取表值（按 paramType 转型）；
   - 表中没有该列或值为 NULL -> 回退到注册窗口配置的「默认值」。
4. 返回值必须包含 success / message / devices；每个 device：
   - deviceId：设备编号（必填，等于 model_device.id，用于匹配平台设备台账）
   - healthScore / healthLevel / dimBody / dimCondition / dimFault / dimMaintain / dimEnv：平台内置字段，自动入对应列
   - remainingLifeDays / riskLevel / maintainAdvice：注册窗口「输出参数」paramKey，平台原样写入结果「输出参数」列
"""

import hashlib

import platform_db

# 入口函数名（平台硬编码调用 execute）
ENTRY_FUNCTION = "execute"

# ============ 脚本内指定要读取的表名（本次读设备台账 model_device）============
TABLE_NAME = "model_device"

LEVEL_HEALTHY = 80
LEVEL_SUBHEALTHY = 60
DIMENSIONS = ("dimBody", "dimCondition", "dimFault", "dimMaintain", "dimEnv")

# 表内暂无数据时的兜底示例设备，保证脚本可立即跑通
FALLBACK_DEVICES = [
    {"id": "DEV-001", "device_name": "1号线-站台门-01", "device_type": "站台门", "line_id": "L1"},
]


def _score(seed, key):
    """按 seed + 维度名生成 60-100 的确定性得分，便于复现。"""
    digest = hashlib.md5(("%s|%s" % (seed, key)).encode("utf-8")).hexdigest()
    return 60 + int(digest[:8], 16) % 41


def _level(score):
    if score >= LEVEL_HEALTHY:
        return "healthy"
    if score >= LEVEL_SUBHEALTHY:
        return "subhealthy"
    return "danger"


def _risk_level(score):
    if score >= 85:
        return "低风险"
    if score >= 70:
        return "中风险"
    return "高风险"


def _as_int(value, default):
    try:
        return int(float(value))
    except (TypeError, ValueError):
        return default


def _load_rows():
    """从脚本指定的表读取启用设备；表不可用或为空时回退内置示例设备。"""
    try:
        rows = platform_db.query(
            TABLE_NAME,
            columns=["id", "device_name", "device_type", "line_id", "status"],
            where={"status": "启用"},
            order_by="sort_order",
        )
    except Exception as exc:  # 连库失败不阻断执行，便于排查
        print("【脚本日志】读取数据库表 %s 失败：%s，使用内置示例设备" % (TABLE_NAME, exc))
        return list(FALLBACK_DEVICES), "内置示例（连库失败）"

    if not rows:
        print("【脚本日志】表 %s 暂无启用设备，使用内置示例设备兜底" % TABLE_NAME)
        return list(FALLBACK_DEVICES), "内置示例（表暂无数据）"
    return rows, TABLE_NAME


def execute(params):
    params = params if isinstance(params, dict) else {}

    # 1) 读取表数据（表名由脚本内 TABLE_NAME 指定）
    rows, source = _load_rows()

    # 2) 按输入参数从表中读取同名字段值，缺失则回退注册窗口默认值
    try:
        cfg = platform_db.load_inputs(TABLE_NAME, where={"status": "启用"})
    except Exception as exc:
        print("【脚本日志】读取输入参数失败：%s" % exc)
        cfg = {}

    window_days = _as_int(cfg.get("window_days"), 30)   # 表中无此列 -> 用默认值
    warn_score = _as_int(cfg.get("warn_score"), 60)     # 表中无此列 -> 用默认值
    line_id = cfg.get("line_id") or "L1"                # 表中有 line_id 列 -> 取真实值

    print("【脚本日志】数据源=%s 行数=%d 输入参数=%s" % (source, len(rows), cfg))

    devices = []
    for row in rows:
        device_id = str(row.get("id") or "").strip()
        if not device_id:
            continue

        seed = "modelTestDb|%s |%s" % (device_id, line_id)
        dims = {dim: _score(seed, dim) for dim in DIMENSIONS}
        health_score = int(round(sum(dims.values()) / float(len(DIMENSIONS))))
        health_level = _level(health_score)

        # 结合输入参数做业务计算
        remaining = max(0, int((health_score - warn_score) * window_days / 10.0))
        advice = "建议在 %d 天内安排巡检" % remaining if remaining > 0 else "建议立即安排检修"

        result = {
            "deviceId": device_id,
            "healthScore": health_score,
            "healthLevel": health_level,
            # 以下 key 与注册窗口「输出参数」paramKey 一一对应
            "remainingLifeDays": remaining,
            "riskLevel": _risk_level(health_score),
            "maintainAdvice": advice,
        }
        result.update(dims)
        # 冗余设备信息（平台匹配到台账时以台账为准）
        if row.get("device_name"):
            result["deviceName"] = row.get("device_name")
        if row.get("line_id"):
            result["lineId"] = row.get("line_id")
        if row.get("device_type"):
            result["deviceType"] = row.get("device_type")
        devices.append(result)

        print("【脚本日志】设备 %s => 健康分 %d（%s）剩余寿命 %d 天"
              % (device_id, health_score, health_level, remaining))

    success = len(devices) > 0
    message = ("共评估 %d 台设备（数据源：%s）" % (len(devices), source)) if success else "无可评估设备"
    return {"success": success, "message": message, "devices": devices}
