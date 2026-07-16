import { useAppSelector } from '../app/store';

export function useAuth() {
  const { user, accessToken, isAuthenticated, loading } = useAppSelector((state) => state.auth);

  return {
    user,
    accessToken,
    isAuthenticated,
    loading,
    isAdmin: user?.role === 'ADMIN',
    isManager: user?.role === 'MANAGER' || user?.role === 'ADMIN',
    isEmployee: user?.role === 'EMPLOYEE',
  };
}
