import base64, importlib.util, json, os, sys, traceback

def load_module(path):
    spec = importlib.util.spec_from_file_location("user_script", path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod

def _decode_arg(index):
    if len(sys.argv) <= index:
        return {}
    try:
        return json.loads(base64.b64decode(sys.argv[index]).decode("utf-8"))
    except Exception:
        return {}

def main():
    script_path = sys.argv[1]                 # /app/scripts/script.py
    entry = sys.argv[2]                       # execute
    params = json.loads(base64.b64decode(sys.argv[3]).decode("utf-8"))
    # argv[4]：模型的输入/输出参数元数据（供平台 DB SDK 按入参字段读取表数据）
    param_meta = _decode_arg(4)
    os.environ["MODEL_INPUT_PARAMS"] = json.dumps(
        param_meta.get("inputParams") or [], ensure_ascii=False)
    os.environ["MODEL_OUTPUT_PARAMS"] = json.dumps(
        param_meta.get("outputParams") or [], ensure_ascii=False)
    try:
        mod = load_module(script_path)
        fn = getattr(mod, entry)
        result = fn(params)                    # 约定：execute(params) -> dict
        print("__RESULT__" + json.dumps(result, ensure_ascii=False, default=str))
        sys.exit(0)
    except Exception as e:
        print(traceback.format_exc(), file=sys.stderr)
        print("__RESULT__" + json.dumps(
            {"success": False, "message": str(e)}, ensure_ascii=False))
        sys.exit(1)

if __name__ == "__main__":
    main()