export interface DocumentListTreeNode {
  children?: DocumentListTreeNode[];
  key: string;
  title: string;
}

export interface DocumentListRecord {
  createdAt: string;
  createdUser: string;
  id: number;
  name: string;
  size: string;
  treeKey: string;
  updatedAt: string;
  updatedUser: string;
  uploadTime: string;
  uploader: string;
}

export const documentListTreeData: DocumentListTreeNode[] = [
  {
    children: [
      {
        key: 'meeting-minutes',
        title: '2、会议纪要',
      },
      {
        key: 'non-stop-event',
        title: '3、非停事件',
      },
      {
        key: 'summary-plan',
        title: '4、总结与计划',
      },
    ],
    key: 'standard-file',
    title: '1、标准文件',
  },
  {
    children: [
      {
        key: 'safety-study',
        title: '6、安全学习安排',
      },
      {
        key: 'document-management',
        title: '7、文档管理',
      },
      {
        key: 'document-list',
        title: '8、文档列表',
      },
    ],
    key: 'division',
    title: '5、分部',
  },
];

export const documentListRecords: DocumentListRecord[] = [
  {
    createdAt: '2026-05-03 09:20',
    createdUser: '张明远',
    id: 1,
    name: '会议纪要-2026Q1.pdf',
    size: '2.4MB',
    treeKey: 'meeting-minutes',
    updatedAt: '2026-05-04 14:02',
    updatedUser: '周敬然',
    uploadTime: '2026-05-03 09:20',
    uploader: '张明远',
  },
  {
    createdAt: '2026-05-04 10:12',
    createdUser: '李林杰',
    id: 2,
    name: '非停事件处置记录.docx',
    size: '1.8MB',
    treeKey: 'non-stop-event',
    updatedAt: '2026-05-04 16:40',
    updatedUser: '李林杰',
    uploadTime: '2026-05-04 10:12',
    uploader: '李林杰',
  },
  {
    createdAt: '2026-05-05 11:05',
    createdUser: '王栋',
    id: 3,
    name: '年度总结与计划.xlsx',
    size: '920KB',
    treeKey: 'summary-plan',
    updatedAt: '2026-05-05 18:20',
    updatedUser: '王栋',
    uploadTime: '2026-05-05 11:05',
    uploader: '王栋',
  },
  {
    createdAt: '2026-05-06 09:18',
    createdUser: '宋腾飞',
    id: 4,
    name: '安全学习安排表.pdf',
    size: '3.1MB',
    treeKey: 'safety-study',
    updatedAt: '2026-05-06 13:32',
    updatedUser: '宋腾飞',
    uploadTime: '2026-05-06 09:18',
    uploader: '宋腾飞',
  },
  {
    createdAt: '2026-05-06 15:10',
    createdUser: '周敬然',
    id: 5,
    name: '文档管理制度.docx',
    size: '1.2MB',
    treeKey: 'document-management',
    updatedAt: '2026-05-07 08:42',
    updatedUser: '周敬然',
    uploadTime: '2026-05-06 15:10',
    uploader: '周敬然',
  },
  {
    createdAt: '2026-05-07 08:40',
    createdUser: '李西彤',
    id: 6,
    name: '文档列表总览.pdf',
    size: '4.6MB',
    treeKey: 'document-list',
    updatedAt: '2026-05-07 09:05',
    updatedUser: '李西彤',
    uploadTime: '2026-05-07 08:40',
    uploader: '李西彤',
  },
];

export function collectTreeDescendantKeys(
  nodes: DocumentListTreeNode[],
  key: string,
): string[] {
  const matchedNode = findTreeNode(nodes, key);
  if (!matchedNode) {
    return [];
  }

  const keys: string[] = [];
  collectLeafKeys(matchedNode, keys);
  return keys.length > 0 ? keys : [matchedNode.key];
}

export function filterDocumentTreeNodes(
  nodes: DocumentListTreeNode[],
  keyword: string,
): DocumentListTreeNode[] {
  const nextKeyword = keyword.trim().toLowerCase();
  if (!nextKeyword) {
    return nodes;
  }

  return nodes
    .map((node) => {
      const matchedChildren = node.children
        ? filterDocumentTreeNodes(node.children, nextKeyword)
        : [];
      const isMatched = node.title.toLowerCase().includes(nextKeyword);

      if (!isMatched && matchedChildren.length === 0) {
        return null;
      }

      return {
        ...node,
        children: matchedChildren,
      };
    })
    .filter(Boolean) as DocumentListTreeNode[];
}

function collectLeafKeys(node: DocumentListTreeNode, keys: string[]) {
  if (!node.children || node.children.length === 0) {
    keys.push(node.key);
    return;
  }

  node.children.forEach((child) => collectLeafKeys(child, keys));
}

function findTreeNode(
  nodes: DocumentListTreeNode[],
  key: string,
): DocumentListTreeNode | null {
  for (const node of nodes) {
    if (node.key === key) {
      return node;
    }

    if (node.children?.length) {
      const matched = findTreeNode(node.children, key);
      if (matched) {
        return matched;
      }
    }
  }

  return null;
}
