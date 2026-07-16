export const API_BASE_URL = '/api/v1';
export const TASK_STATUSES = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED'] as const;
export const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as const;
export const PROJECT_STATUSES = ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'ARCHIVED'] as const;
export const ROLES = ['ADMIN', 'MANAGER', 'EMPLOYEE'] as const;

export const STATUS_COLORS: Record<string, string> = {
  TODO: '#64748B',
  IN_PROGRESS: '#2563EB',
  IN_REVIEW: '#D97706',
  DONE: '#059669',
  CANCELLED: '#DC2626',
  PLANNING: '#64748B',
  ON_HOLD: '#D97706',
  COMPLETED: '#059669',
  ARCHIVED: '#9CA3AF',
};

export const PRIORITY_COLORS: Record<string, string> = {
  LOW: '#059669',
  MEDIUM: '#D97706',
  HIGH: '#EA580C',
  CRITICAL: '#DC2626',
};

export const NOTIFICATION_ICONS: Record<string, string> = {
  TASK_ASSIGNED: 'Assignment',
  TASK_UPDATED: 'Update',
  PROJECT_CREATED: 'FolderOpen',
  COMMENT_ADDED: 'Comment',
};
