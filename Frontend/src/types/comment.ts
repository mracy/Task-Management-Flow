import { UserSummary } from './auth';

export interface Comment {
  id: string;
  content: string;
  author: UserSummary;
  taskId: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
}

export interface UpdateCommentRequest {
  content: string;
}
