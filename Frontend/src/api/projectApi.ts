import { apiSlice } from './apiSlice';
import type { Project, ProjectSummary, CreateProjectRequest, UpdateProjectRequest, PaginatedResponse, AddMemberRequest } from '../types';

export const projectApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getProjects: builder.query<
      PaginatedResponse<ProjectSummary>,
      { page?: number; size?: number; search?: string; status?: string; sort?: string; direction?: string }
    >({
      query: ({ page = 0, size = 10, search, status, sort, direction }) => {
        const params = new URLSearchParams({ page: String(page), size: String(size) });
        if (search) params.set('search', search);
        if (status) params.set('status', status);
        if (sort) params.set('sort', sort);
        if (direction) params.set('direction', direction);
        return `/projects?${params.toString()}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((p) => ({ type: 'Project' as const, id: p.id })),
              { type: 'Project', id: 'LIST' },
            ]
          : [{ type: 'Project', id: 'LIST' }],
    }),
    getProjectById: builder.query<Project, string>({
      query: (id) => `/projects/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Project', id }],
    }),
    createProject: builder.mutation<Project, CreateProjectRequest>({
      query: (body) => ({
        url: '/projects',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'Project', id: 'LIST' }],
    }),
    updateProject: builder.mutation<Project, { id: string; data: UpdateProjectRequest }>({
      query: ({ id, data }) => ({
        url: `/projects/${id}`,
        method: 'PUT',
        body: data,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Project', id },
        { type: 'Project', id: 'LIST' },
      ],
    }),
    deleteProject: builder.mutation<void, string>({
      query: (id) => ({
        url: `/projects/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (_result, _error, id) => [
        { type: 'Project', id },
        { type: 'Project', id: 'LIST' },
      ],
    }),
    addMember: builder.mutation<Project, { projectId: string; data: AddMemberRequest }>({
      query: ({ projectId, data }) => ({
        url: `/projects/${projectId}/members`,
        method: 'POST',
        body: data,
      }),
      invalidatesTags: (_result, _error, { projectId }) => [{ type: 'Project', id: projectId }],
    }),
    removeMember: builder.mutation<Project, { projectId: string; userId: string }>({
      query: ({ projectId, userId }) => ({
        url: `/projects/${projectId}/members/${userId}`,
        method: 'DELETE',
      }),
      invalidatesTags: (_result, _error, { projectId }) => [{ type: 'Project', id: projectId }],
    }),
  }),
});

export const {
  useGetProjectsQuery,
  useGetProjectByIdQuery,
  useCreateProjectMutation,
  useUpdateProjectMutation,
  useDeleteProjectMutation,
  useAddMemberMutation,
  useRemoveMemberMutation,
} = projectApi;
