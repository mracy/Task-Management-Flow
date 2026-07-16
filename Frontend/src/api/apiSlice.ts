import {
  createApi,
  fetchBaseQuery,
  BaseQueryFn,
  FetchArgs,
  FetchBaseQueryError,
} from '@reduxjs/toolkit/query/react';
import type { RootState } from '../app/store';
import { logout, updateAccessToken } from '../store/authSlice';
import toast from 'react-hot-toast';

const baseQuery = fetchBaseQuery({
  baseUrl: '/api/v1',
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).auth.accessToken;
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  },
});

const baseQueryWithReauth: BaseQueryFn<
  string | FetchArgs,
  unknown,
  FetchBaseQueryError
> = async (args, api, extraOptions) => {
  let result = await baseQuery(args, api, extraOptions);

  if (result.error && result.error.status === 401) {
    const refreshToken = (api.getState() as RootState).auth.refreshToken;
    if (refreshToken) {
      const refreshResult = await baseQuery(
        {
          url: '/auth/refresh',
          method: 'POST',
          body: { refreshToken },
        },
        api,
        extraOptions
      );

      if (refreshResult.data) {
        const data = refreshResult.data as { accessToken: string };
        api.dispatch(updateAccessToken(data.accessToken));
        result = await baseQuery(args, api, extraOptions);
      } else {
        api.dispatch(logout());
        toast.error('Session expired. Please login again.');
      }
    } else {
      api.dispatch(logout());
    }
  }

  if (result.error) {
    const errData = result.error as { data?: { message?: string }; status: number | string };
    const message = errData.data?.message || 'An unexpected error occurred';
    if (errData.status === 403) {
      toast.error('You do not have permission to perform this action.');
    } else if (errData.status === 404) {
      toast.error('Resource not found.');
    } else if (typeof errData.status === 'number' && errData.status >= 500) {
      toast.error('Server error. Please try again later.');
    } else {
      toast.error(message);
    }
  }

  return result;
};

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['User', 'Project', 'Task', 'Comment', 'Notification', 'Dashboard'],
  endpoints: () => ({}),
});
