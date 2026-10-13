# -*- coding: utf-8 -*-
"""
模型平台示例脚本：从数据库表读取设备数据，按固定维度计算健康结果。

平台契约
1. 入口函数固定为 execute(params)，params 为 dict，key = 注册窗口「输入参数 - 参数标识」。
2. 脚本内自行指定要读取的表名（变量 TABLE_NAME），平台为只读访问。
3. 返回值必须包含 success / message / devices；每个 device 固定输出以下 7 个结果字段：
   - deviceId：设备编号（必填，等于 model_device.id，用于匹配平台设备台账）
   - healthLevel：健康等级（healthy / subhealthy / danger）
   - healthScore：总分（0-100，等于五个维度得分之和）
   - dimBody：本体得分（满分 20）
   - dimCondition：工况得分（满分 25）
   - dimFault：故障得分（满分 25）
   - dimMaintain：维保得分（满分 20）
   - dimEnv：环境得分（满分 10）
"""

import hashlib

import platform_db

# 入口函数名（平台硬编码调用 execute）
ENTRY_FUNCTION = "execute"

# ============ 脚本内指定要读取的表名（本次读设备台账 model_device）============
TABLE_NAME = "model_device"

# 各维度满分，总分 100
DIM_MAX_SCORE = {
    "dimBody": 20,       # 本体
    "dimCondition": 25,  # 工况
    "dimFault": 25,      # 故障
    "dimMaintain": 20,   # 维保
    "dimEnv": 10,        # 环境
}

LEVEL_HEALTHY = 80
LEVEL_SUBHEALTHY = 60

# 表内暂无数据时的兜底示例设备，保证脚本可立即跑通
FALLBACK_DEVICES = [
    {"id": "DEV-001", "device_name": "1号线-站台门-01", "device_type": "站台门", "line_id": "L1"},
]


def _ratio(seed, key):
    """按 seed + 维度名生成 0-1 的确定性比例，便于复现。"""
    digest = hashlib.md5(("%s|%s" % (seed, key)).encode("utf-8")).hexdigest()
    return (int(digest[:8], 16) % 1001) / 1000.0


def _level(score):
    if score >= LEVEL_HEALTHY:
        return "healthy"
    if score >= LEVEL_SUBHEALTHY:
        return "subhealthy"
    return "danger"


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

    # 2) 读取输入参数（本脚本固定维度计算，输入参数仅用于日志/扩展）
    try:
        cfg = platform_db.load_inputs(TABLE_NAME, where={"status": "启用"})
    except Exception as exc:
        print("【脚本日志】读取输入参数失败：%s" % exc)
        cfg = {}

    print("【脚本日志】数据源=%s 行数=%d 输入参数=%s" % (source, len(rows), cfg))

    devices = []
    for row in rows:
        device_id = str(row.get("id") or "").strip()
        if not device_id:
            continue

        seed = "modelTestDb|%s" % device_id
        dims = {
            dim: int(round(_ratio(seed, dim) * full))
            for dim, full in DIM_MAX_SCORE.items()
        }
        health_score = sum(dims.values())
        health_level = _level(health_score)

        # 固定 7 个结果字段：healthLevel / healthScore / dimBody / dimCondition / dimFault / dimMaintain / dimEnv
        result = {
            "deviceId": device_id,
            "healthLevel": health_level,
            "healthScore": health_score,
        }
        result.update(dims)
        devices.append(result)

        print("【脚本日志】设备 %s => 总分 %d（%s）维度 %s"
              % (device_id, health_score, health_level, dims))

    success = len(devices) > 0
    message = ("共评估 %d 台设备（数据源：%s）" % (len(devices), source)) if success else "无可评估设备"
    return {"success": success, "message": message, "devices": devices}
