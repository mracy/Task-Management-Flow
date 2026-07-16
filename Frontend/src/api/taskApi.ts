import { apiSlice } from './apiSlice';
import type {
  Task,
  CreateTaskRequest,
  UpdateTaskRequest,
  ChangeTaskStatusRequest,
  TaskFilters,
  PaginatedResponse,
} from '../types';

export const taskApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getTasks: builder.query<PaginatedResponse<Task>, TaskFilters>({
      query: (filters) => {
        const params = new URLSearchParams();
        if (filters.page !== undefined) params.set('page', String(filters.page));
        if (filters.size !== undefined) params.set('size', String(filters.size));
        if (filters.status) params.set('status', filters.status);
        if (filters.priority) params.set('priority', filters.priority);
        if (filters.assigneeId) params.set('assigneeId', filters.assigneeId);
        if (filters.projectId) params.set('projectId', filters.projectId);
        if (filters.search) params.set('search', filters.search);
        if (filters.sort) params.set('sort', filters.sort);
        if (filters.direction) params.set('direction', filters.direction);
        return `/tasks?${params.toString()}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((t) => ({ type: 'Task' as const, id: t.id })),
              { type: 'Task', id: 'LIST' },
            ]
          : [{ type: 'Task', id: 'LIST' }],
    }),
    getTaskById: builder.query<Task, string>({
      query: (id) => `/tasks/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Task', id }],
    }),
    createTask: builder.mutation<Task, CreateTaskRequest>({
      query: (body) => ({
        url: '/tasks',
        method: 'POST',
        body,
      }),
      invalidatesTags: [
        { type: 'Task', id: 'LIST' },
        { type: 'Dashboard', id: 'STATS' },
      ],
    }),
    updateTask: builder.mutation<Task, { id: string; data: UpdateTaskRequest }>({
      query: ({ id, data }) => ({
        url: `/tasks/${id}`,
        method: 'PUT',
        body: data,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Task', id },
        { type: 'Task', id: 'LIST' },
        { type: 'Dashboard', id: 'STATS' },
      ],
    }),
    deleteTask: builder.mutation<void, string>({
      query: (id) => ({
        url: `/tasks/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (_result, _error, id) => [
        { type: 'Task', id },
        { type: 'Task', id: 'LIST' },
        { type: 'Dashboard', id: 'STATS' },
      ],
    }),
    changeTaskStatus: builder.mutation<Task, { id: string; data: ChangeTaskStatusRequest }>({
      query: ({ id, data }) => ({
        url: `/tasks/${id}/status`,
        method: 'PATCH',
        body: data,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Task', id },
        { type: 'Task', id: 'LIST' },
        { type: 'Dashboard', id: 'STATS' },
      ],
    }),
    assignTask: builder.mutation<Task, { id: string; assigneeId: string }>({
      query: ({ id, assigneeId }) => ({
        url: `/tasks/${id}/assign`,
        method: 'PATCH',
        body: { assigneeId },
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Task', id },
        { type: 'Task', id: 'LIST' },
      ],
    }),
  }),
});

export const {
  useGetTasksQuery,
  useGetTaskByIdQuery,
  useCreateTaskMutation,
  useUpdateTaskMutation,
  useDeleteTaskMutation,
  useChangeTaskStatusMutation,
  useAssignTaskMutation,
} = taskApi;
