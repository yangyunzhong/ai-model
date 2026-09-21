import type { UploadChangeParam, UploadFile } from 'antdv-next';

export type UploadType = 'file' | 'image';

export interface UploadResult {
  fileName: string;
  ossId: string;
  url: string;
}

export interface OssFile {
  fileName?: string;
  originalName: string;
  ossId: string;
  url: string;
}

export interface UploadProgressEvent {
  loaded: number;
  total?: number;
}

export interface UploadApiOptions {
  onUploadProgress?: (event: UploadProgressEvent) => void;
  otherData?: Record<string, any>;
}

export type UploadRequest<T = UploadResult> = Promise<T> & {
  abort: () => void;
  onUpload: (handler: (event: UploadProgressEvent) => void) => void;
};

export type UploadApi<T = UploadResult> = (
  file: Blob | File,
  options?: UploadApiOptions,
) => UploadRequest<T>;

export type UploadInfoApi<T = OssFile> = (ossIds: string[]) => Promise<T[]>;

export interface UploadFeedbackAdapter {
  confirmRemove?: (file: UploadFile) => boolean | Promise<boolean>;
  showMaxSizeError?: (maxSize: number) => void;
  showUploadSuccess?: () => void;
}

export interface CropperUploadParams {
  file: Blob;
  filename: string;
  name: string;
}

export interface CropperUploadResult {
  url: string;
}

export type CropperUploadApi<T = CropperUploadResult> = (
  params: CropperUploadParams,
) => Promise<T>;

/**
 * 自定义返回文件名/缩略图使用 泛型控制返回是否必填
 * type 为不同的接口返回值 需要自行if判断
 */
export type CustomGetter<T extends string | undefined> = (
  cb:
    | { response: OssFile; type: 'info' }
    | { response: UploadResult; type: 'upload' },
) => T extends undefined ? string | undefined : string;

export interface BaseUploadProps {
  /**
   * 是否在组件Unmounted时取消上传
   * @default true
   */
  abortOnUnmounted?: boolean;
  /**
   * 同antdv参数
   */
  accept?: string;
  /**
   * 你可能使用的是application/pdf这种mime类型, 但是这样用户可能看不懂, 在这里自定义逻辑
   * @default 原始accept
   */
  acceptFormat?: ((accept: string) => string) | string;
  /**
   * 上传接口
   */
  api?: UploadApi;
  /**
   * 自定义文件名 需要区分两个接口的返回值
   */
  customFilename?: CustomGetter<string>;
  /**
   * 自定义缩略图 需要区分两个接口的返回值
   */
  customThumbUrl?: CustomGetter<undefined>;
  /**
   * 附带的请求参数
   */
  data?: any;
  /**
   * 是否支持上传文件夹
   * @default false
   */
  directory?: boolean;
  /**
   * 是否禁用
   * @default false
   */
  disabled?: boolean;
  /**
   * 是否支持拖拽上传
   * @default false
   */
  enableDragUpload?: boolean;
  /**
   * 上传提示、删除确认等反馈能力。
   * 应用侧默认注入全局 message/modal，平台层只依赖该抽象能力。
   */
  feedback?: UploadFeedbackAdapter;
  /**
   * 是否显示文案 请上传不超过...
   * @default true
   */
  helpMessage?: boolean;
  /**
   * 通过已绑定的 ossId 查询文件信息。
   * 应用侧默认注入业务接口，平台层只依赖该抽象能力。
   */
  infoApi?: UploadInfoApi;
  /**
   * 当ossId查询不到文件信息时  比如被删除了
   * 是否保留列表对应的ossId 默认不保留
   * @default false
   */
  keepMissingId?: boolean;
  /**
   * 最大上传图片数量
   * maxCount为1时 会被绑定为string而非string[]
   * @default 1
   */
  maxCount?: number;
  /**
   * 文件最大 单位M
   * @default 5
   */
  maxSize?: number;
  /**
   * 是否支持多选文件，ie10+ 支持。开启后按住 ctrl 可选择多个文件。
   * @default false
   */
  multiple?: boolean;
  /**
   * 自定义文件/图片预览逻辑 比如: 你可以改为下载
   * 图片上传默认为预览
   * 文件上传默认为window.open
   * @param file file
   */
  preview?: (file: UploadFile) => Promise<void> | void;
  /**
   * 删除文件前是否需要确认
   * @default false
   */
  removeConfirm?: boolean;
  /**
   * 文件上传失败 是否从展示列表中删除
   * @default true
   */
  removeOnError?: boolean;
  /**
   * 上传成功 是否展示提示信息
   * @default true
   */
  showSuccessMsg?: boolean;
}

export interface UploadEmits {
  (e: 'success', file: Blob | File, response: UploadResult): void;
  (e: 'remove', file: UploadFile): void;
  (e: 'change', info: UploadChangeParam): void;
}

/**
 * 默认支持上传的图片文件类型。
 */
export const defaultImageAcceptExts = [
  '.jpg',
  '.jpeg',
  '.png',
  '.gif',
  '.webp',
];

/**
 * 默认支持上传的文件类型。
 */
export const defaultFileAcceptExts = ['.xlsx', '.csv', '.docx', '.pdf'];

/**
 * 文件(非图片)的默认预览逻辑。
 */
export function defaultFilePreview(file: UploadFile) {
  if (file?.url) {
    window.open(file.url);
  }
}
