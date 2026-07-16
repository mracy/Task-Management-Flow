export * from './auth';
export * from './project';
export * from './task';
export * from './comment';
export * from './notification';
export * from './dashboard';

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
