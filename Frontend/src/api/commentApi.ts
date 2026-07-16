import { apiSlice } from './apiSlice';
import type { Comment, CreateCommentRequest, UpdateCommentRequest, PaginatedResponse } from '../types';

export const commentApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getComments: builder.query<PaginatedResponse<Comment>, string>({
      query: (taskId) => `/tasks/${taskId}/comments`,
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((c) => ({ type: 'Comment' as const, id: c.id })),
              { type: 'Comment', id: 'LIST' },
            ]
          : [{ type: 'Comment', id: 'LIST' }],
    }),
    createComment: builder.mutation<Comment, { taskId: string; data: CreateCommentRequest }>({
      query: ({ taskId, data }) => ({
        url: `/tasks/${taskId}/comments`,
        method: 'POST',
        body: data,
      }),
      invalidatesTags: [{ type: 'Comment', id: 'LIST' }],
    }),
    updateComment: builder.mutation<
      Comment,
      { taskId: string; commentId: string; data: UpdateCommentRequest }
    >({
      query: ({ taskId, commentId, data }) => ({
        url: `/tasks/${taskId}/comments/${commentId}`,
        method: 'PUT',
        body: data,
      }),
      invalidatesTags: (_result, _error, { commentId }) => [
        { type: 'Comment', id: commentId },
      ],
    }),
    deleteComment: builder.mutation<void, { taskId: string; commentId: string }>({
      query: ({ taskId, commentId }) => ({
        url: `/tasks/${taskId}/comments/${commentId}`,
        method: 'DELETE',
      }),
      invalidatesTags: (_result, _error, { commentId }) => [
        { type: 'Comment', id: commentId },
        { type: 'Comment', id: 'LIST' },
      ],
    }),
  }),
});

export const {
  useGetCommentsQuery,
  useCreateCommentMutation,
  useUpdateCommentMutation,
  useDeleteCommentMutation,
} = commentApi;
