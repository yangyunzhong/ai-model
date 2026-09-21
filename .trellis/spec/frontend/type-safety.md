# 前端类型安全规范

## 类型组织

- 接口类型放在同目录 `model.d.ts`。
- API 文件 `index.ts` 只导入类型并封装请求。
- 通用接口类型复用 `#/api/common` 中的 `ID`、`IDS`、`PageQuery`、`PageResult`。
- 页面局部临时类型可放在组件内，但跨组件复用时要提升到 `model.d.ts` 或 `data.ts`。

## API 泛型

- 列表接口使用 `alovaInstance.get<PageResult<T>>(...)`。
- 详情接口使用 `alovaInstance.get<T>(...)`。
- 操作接口使用 `postWithMsg<void>`、`putWithMsg<void>`、`deleteWithMsg<void>`。
- 导出接口使用 `commonExport` 或明确 `Blob`。
- 需要原始响应时显式 `isTransformResponse: false`。

```ts
export function userList(params?: PageQuery) {
  return alovaInstance.get<PageResult<User>>(Api.userList, { params });
}
```

## 表单校验

- 表单 schema 放在 `data.ts` / `data.tsx`。
- 需要运行时校验时使用 `#/adapter/form` 暴露的 `z`。
- 不把后端必填规则只写在 UI 文案里，schema 要同步体现。

## any 使用

- 允许在 VxeGrid slot、第三方库回调或历史代码边界短暂使用 `Recordable<any>`。
- 新业务类型能明确时不要使用 `any`。
- 类型断言必须靠近边界，例如 `modalApi.getData() as { id?: number | string }`。

## 数字 ID

- 后端 ID 多为 Long/雪花 ID，前端类型使用项目通用 `ID`，不要假设一定是 number。
- URL 拼接和删除批量 ID 使用 `ID` / `IDS`。

## 禁止

- 禁止组件内重复声明与 `model.d.ts` 不一致的接口类型。
- 禁止让 API 返回 `any` 后在页面里猜字段。
- 禁止为了消除类型报错随意 `as unknown as Xxx`。
