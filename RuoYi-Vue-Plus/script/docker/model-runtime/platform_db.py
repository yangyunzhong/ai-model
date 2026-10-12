"""平台受控数据库访问 SDK。

用户脚本通过 `import platform_db` 使用本模块，按表名只读查询平台数据库，
并按注册模型窗口中配置的「输入参数」从表中读取同名字段数据。

约定：
- 连接信息来自环境变量（由平台在创建容器时注入）：
  MODEL_DB_HOST / MODEL_DB_PORT / MODEL_DB_NAME / MODEL_DB_USER / MODEL_DB_PASSWORD
- 模型的输入/输出参数元数据来自环境变量：
  MODEL_INPUT_PARAMS  / MODEL_OUTPUT_PARAMS  （JSON 数组字符串）

只读约束：仅提供 SELECT 查询，所有表名/列名做标识符白名单校验，
查询值使用参数化占位符，避免 SQL 注入。
"""

import json
import os
import re

import pymysql
import pymysql.cursors

__all__ = [
    "connection_info",
    "connect",
    "query",
    "load_inputs",
    "input_params",
    "output_params",
]

# 合法标识符：字母或下划线开头，后接字母/数字/下划线
_IDENT_RE = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")


def _env(name, default=None):
    value = os.environ.get(name)
    if value is None or value == "":
        return default
    return value


def connection_info():
    """返回数据库连接信息字典（供调试打印，不含密码）。"""
    return {
        "host": _env("MODEL_DB_HOST", "host.docker.internal"),
        "port": int(_env("MODEL_DB_PORT", "3306")),
        "database": _env("MODEL_DB_NAME", "ai-model"),
        "user": _env("MODEL_DB_USER", "model_reader"),
    }


def connect():
    """建立数据库连接（DictCursor，utf8mb4）。"""
    return pymysql.connect(
        host=_env("MODEL_DB_HOST", "host.docker.internal"),
        port=int(_env("MODEL_DB_PORT", "3306")),
        user=_env("MODEL_DB_USER", "model_reader"),
        password=_env("MODEL_DB_PASSWORD", ""),
        database=_env("MODEL_DB_NAME", "ai-model"),
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
        connect_timeout=10,
    )


def _check_identifier(value, label):
    if not value or not _IDENT_RE.match(value):
        raise ValueError(f"非法的{label}：{value!r}（仅允许字母、数字、下划线，且不能以数字开头）")
    return value


def _parse_param_meta(env_name):
    raw = _env(env_name)
    if not raw:
        return []
    try:
        data = json.loads(raw)
    except (ValueError, TypeError):
        return []
    if not isinstance(data, list):
        return []
    return [item for item in data if isinstance(item, dict) and item.get("paramKey")]


def input_params():
    """返回模型的输入参数元数据列表（每项含 paramKey/paramName/paramType/defaultValue）。"""
    return _parse_param_meta("MODEL_INPUT_PARAMS")


def output_params():
    """返回模型的输出参数元数据列表（每项含 paramKey/paramName/paramType）。"""
    return _parse_param_meta("MODEL_OUTPUT_PARAMS")


def _build_where(where):
    if not where:
        return "", []
    if not isinstance(where, dict):
        raise ValueError("where 必须是 dict，例如 {'line_id': 'L1'}")
    clauses = []
    values = []
    for column, value in where.items():
        _check_identifier(column, "列名")
        clauses.append(f"`{column}` = %s")
        values.append(value)
    return " AND ".join(clauses), values


def query(table, columns=None, where=None, order_by=None, limit=None):
    """按表名只读查询，返回 list[dict]。

    :param table: 表名（脚本内自行指定）
    :param columns: 需要查询的列名列表，None 表示 SELECT *
    :param where: 等值过滤条件 dict，例如 {"line_id": "L1"}
    :param order_by: 排序列名，可选
    :param limit: 返回条数上限，可选
    """
    _check_identifier(table, "表名")

    if columns:
        select_cols = ", ".join(f"`{_check_identifier(c, '列名')}`" for c in columns)
    else:
        select_cols = "*"

    sql = f"SELECT {select_cols} FROM `{table}`"
    params = []

    where_sql, where_values = _build_where(where)
    if where_sql:
        sql += " WHERE " + where_sql
        params.extend(where_values)

    if order_by:
        sql += f" ORDER BY `{_check_identifier(order_by, '排序列名')}`"

    if limit is not None:
        sql += " LIMIT %s"
        params.append(int(limit))

    conn = connect()
    try:
        with conn.cursor() as cursor:
            cursor.execute(sql, params)
            return list(cursor.fetchall())
    finally:
        conn.close()


def _cast(value, param_type):
    """按参数类型将表字段值转换为脚本计算所需的类型。"""
    if value is None:
        return None
    if param_type == "数值":
        if isinstance(value, (int, float)):
            return value
        text = str(value).strip()
        try:
            if "." in text:
                return float(text)
            return int(text)
        except ValueError:
            return value
    if param_type == "布尔":
        if isinstance(value, bool):
            return value
        return str(value).strip().lower() in ("1", "true", "yes", "y", "是")
    # 文本等其它类型原样返回
    return value


def load_inputs(table, where=None, order_by=None, req_index=0):
    """按模型「输入参数」读取该表对应字段的数据用于计算。

    规则：
    - 以模型配置的输入参数（paramKey）为准；
    - 表中存在同名字段则取表值（经 paramType 转型）；
    - 表中没有该字段或取值为 NULL 时，回退到输入参数的 defaultValue。

    :param table: 表名（脚本内自行指定）
    :param where: 等值过滤条件 dict，用于定位目标数据行
    :param order_by: 排序列名，可选
    :param req_index: 命中多行时取第几行（从 0 开始）
    :return: 以 paramKey 为 key 的入参字典
    """
    metas = input_params()

    # 未配置输入参数时，直接返回顺序行的全部字段，便于脚本灵活使用
    if not metas:
        rows = query(table, where=where, order_by=order_by)
        if not rows:
            return {}
        return dict(rows[req_index] if req_index < len(rows) else rows[0])

    keys = [m.get("paramKey") for m in metas]
    rows = query(table, columns=None, where=where, order_by=order_by)
    row = rows[req_index] if rows and req_index < len(rows) else {}

    result = {}
    for meta in metas:
        key = meta.get("paramKey")
        param_type = meta.get("paramType")
        default_value = meta.get("defaultValue")
        if key in row and row.get(key) is not None:
            result[key] = _cast(row.get(key), param_type)
        else:
            result[key] = _cast(default_value, param_type) if default_value is not None else None
    return result
