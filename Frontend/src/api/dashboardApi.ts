import { apiSlice } from './apiSlice';
import type { DashboardResponse } from '../types';

export const dashboardApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getDashboard: builder.query<DashboardResponse, void>({
      query: () => '/dashboard',
      providesTags: [{ type: 'Dashboard', id: 'STATS' }],
    }),
  }),
});

export const { useGetDashboardQuery } = dashboardApi;
