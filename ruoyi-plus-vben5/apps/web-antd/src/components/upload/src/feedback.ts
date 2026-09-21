import type { UploadFeedbackAdapter } from './props';

import { $t } from '@vben/locales';

export function createUploadFeedback(): UploadFeedbackAdapter {
  return {
    confirmRemove(file) {
      return new Promise<boolean>((resolve) => {
        window.modal.confirm({
          title: $t('pages.common.tip'),
          content: $t('component.upload.confirmDelete', [file.name]),
          okButtonProps: { danger: true },
          centered: true,
          onOk() {
            resolve(true);
          },
          onCancel() {
            resolve(false);
          },
        });
      });
    },
    showMaxSizeError(maxSize) {
      window.message.error($t('component.upload.maxSize', [maxSize]));
    },
    showUploadSuccess() {
      window.message.success($t('component.upload.uploadSuccess'));
    },
  };
}
