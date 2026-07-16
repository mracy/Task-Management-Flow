import { useCallback } from 'react';
import { useAppDispatch, useAppSelector } from '../app/store';
import { toggleTheme } from '../store/uiSlice';
import { lightTheme, darkTheme } from '../theme/lightTheme';

export function useTheme() {
  const dispatch = useAppDispatch();
  const { themeMode } = useAppSelector((state) => state.ui);
  const theme = themeMode === 'light' ? lightTheme : darkTheme;

  const handleToggleTheme = useCallback(() => {
    dispatch(toggleTheme());
  }, [dispatch]);

  return { theme, themeMode, toggleTheme: handleToggleTheme, isDark: themeMode === 'dark' };
}
