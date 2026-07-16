export type NotificationType = 'TASK_ASSIGNED' | 'TASK_UPDATED' | 'PROJECT_CREATED' | 'COMMENT_ADDED';

export interface Notification {
  id: string;
  type: NotificationType;
  message: string;
  read: boolean;
  createdAt: string;
  entityId?: string;
  entityType?: string;
}
