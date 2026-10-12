# -*- coding: utf-8 -*-
"""
模型平台测试脚本（用于「模型管理 - 注册模型」上传）

平台契约：
1. 入口函数名固定为 execute，脚本必须实现 `def execute(params):`，参数 params 为 dict。
2. params 的 key 与注册窗口里「输入参数 - 参数标识」一一对应。
   例如窗口配置参数标识 window_days（数值），脚本用 params.get("window_days") 取值。
3. 返回值必须是 dict，且包含 success / message / devices 三个字段（字段名硬编码，不可改名）：
   {
     "success": True/False,
     "message": "执行说明",
     "devices": [
       {
         "deviceId":     "设备ID（必填，用于匹配平台设备清单）",
         "healthScore":  72,            # 健康总分 0-100
         "healthLevel":  "subhealthy",  # healthy/subhealthy/danger
         "dimBody":      96,            # 维度分：本体
         "dimCondition": 62,            # 维度分：状态
         "dimFault":     60,            # 维度分：故障
         "dimMaintain":  71,            # 维度分：维护
         "dimEnv":       73,            # 维度分：环境
         # 以下为可选，平台会优先用自身设备清单覆盖
         "deviceName": "...", "lineId": "...", "stationId": "...", "deviceType": "..."
       }
     ]
   }
4. runner.py 会把返回值以单行 `__RESULT__{json}` 输出到 stdout，其余 stdout 视为日志。
"""

import hashlib

# 入口函数名（平台硬编码调用 execute）
ENTRY_FUNCTION = "execute"

# 健康等级阈值
LEVEL_HEALTHY = 80
LEVEL_SUBHEALTHY = 60

# 五个健康维度（与 model_result 表字段名一致）
DIMENSIONS = ("dimBody", "dimCondition", "dimFault", "dimMaintain", "dimEnv")

# 未传入设备时的默认示例设备
DEFAULT_DEVICES = [
    {"deviceId": "DEV-001", "deviceName": "1号线-站台门-01", "lineId": "L1", "stationId": "ST-001", "deviceType": "站台门"},
    {"deviceId": "DEV-002", "deviceName": "1号线-扶梯-02", "lineId": "L1", "stationId": "ST-002", "deviceType": "扶梯"},
    {"deviceId": "DEV-003", "deviceName": "2号线-风机-03", "lineId": "L2", "stationId": "ST-003", "deviceType": "风机"},
]


def _score(seed, key):
    """按 seed + 维度名生成 60-100 的确定性得分，便于结果复现。"""
    digest = hashlib.md5(("%s|%s" % (seed, key)).encode("utf-8")).hexdigest()
    return 60 + int(digest[:8], 16) % 41


def _level(score):
    """按总分换算健康等级。"""
    if score >= LEVEL_HEALTHY:
        return "healthy"
    if score >= LEVEL_SUBHEALTHY:
        return "subhealthy"
    return "danger"


def _resolve_devices(params):
    """解析待评估设备：优先 params.devices，其次 params.deviceIds，最后用默认设备。"""
    devices = params.get("devices")
    if isinstance(devices, list) and devices:
        return devices
    device_ids = params.get("deviceIds")
    if isinstance(device_ids, list) and device_ids:
        return [{"deviceId": str(i)} for i in device_ids]
    return DEFAULT_DEVICES


def execute(params):
    """平台入口函数：接收 params，返回约定结构的结果。"""
    params = params if isinstance(params, dict) else {}
    devices = _resolve_devices(params)

    print("【脚本日志】模型开始执行，待评估设备数：%d" % len(devices))

    results = []
    for device in devices:
        if not isinstance(device, dict):
            continue
        device_id = str(device.get("deviceId") or "")
        if not device_id:
            continue

        seed = "modelTest|%s" % device_id
        dims = {dim: _score(seed, dim) for dim in DIMENSIONS}
        health_score = int(round(sum(dims.values()) / float(len(DIMENSIONS))))
        health_level = _level(health_score)

        result = {
            "deviceId": device_id,
            "healthScore": health_score,
            "healthLevel": health_level,
        }
        result.update(dims)
        # 携带设备冗余信息（平台若匹配到设备清单会覆盖）
        for field in ("deviceName", "lineId", "stationId", "deviceType"):
            if device.get(field):
                result[field] = device[field]
        results.append(result)

        print("【脚本日志】设备 %s => 健康分 %d（%s）" % (device_id, health_score, health_level))

    success = len(results) > 0
    message = "测试脚本执行完成，共评估 %d 台设备" % len(results) if success else "未解析到可评估的设备"
    print("【脚本日志】" + message)

    return {"success": success, "message": message, "devices": results}
