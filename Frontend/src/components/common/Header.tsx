import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Badge,
  Menu,
  MenuItem,
  Avatar,
  Box,
  Tooltip,
  Divider,
  ListItemIcon,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import NotificationsIcon from '@mui/icons-material/Notifications';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';
import { useAuth } from '../../hooks/useAuth';
import { useTheme as useAppTheme } from '../../hooks/useTheme';
import { useGetUnreadCountQuery } from '../../api/notificationApi';
import { useLogoutMutation } from '../../api/authApi';
import { useAppDispatch } from '../../app/store';
import { logout } from '../../store/authSlice';
import UserAvatar from './Avatar';
import toast from 'react-hot-toast';

interface Props {
  onDrawerToggle: () => void;
}

export default function Header({ onDrawerToggle }: Props) {
  const { user } = useAuth();
  const { isDark, toggleTheme } = useAppTheme();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [logoutApi] = useLogoutMutation();
  const { data: unreadData } = useGetUnreadCountQuery();

  const handleProfileOpen = (e: React.MouseEvent<HTMLElement>) => setAnchorEl(e.currentTarget);
  const handleProfileClose = () => setAnchorEl(null);

  const handleLogout = async () => {
    try {
      await logoutApi().unwrap();
    } catch {
      // silent - even if server call fails, clear local state
    }
    dispatch(logout());
    toast.success('Logged out successfully');
    navigate('/login');
    handleProfileClose();
  };

  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={{
        bgcolor: 'background.paper',
        color: 'text.primary',
        borderBottom: '1px solid',
        borderColor: 'divider',
      }}
    >
      <Toolbar>
        {isMobile && (
          <IconButton edge="start" onClick={onDrawerToggle} sx={{ mr: 1 }}>
            <MenuIcon />
          </IconButton>
        )}

        <Box sx={{ flexGrow: 1 }} />

        <Tooltip title={isDark ? 'Light mode' : 'Dark mode'}>
          <IconButton onClick={toggleTheme}>
            {isDark ? <LightModeIcon /> : <DarkModeIcon />}
          </IconButton>
        </Tooltip>

        <Tooltip title="Notifications">
          <IconButton onClick={() => navigate('/notifications')}>
            <Badge badgeContent={unreadData || 0} color="error">
              <NotificationsIcon />
            </Badge>
          </IconButton>
        </Tooltip>

        <Tooltip title="Account">
          <IconButton onClick={handleProfileOpen} sx={{ ml: 1 }}>
            {user ? (
              <UserAvatar firstName={user.firstName} lastName={user.lastName} src={user.avatarUrl} />
            ) : (
              <AccountCircleIcon />
            )}
          </IconButton>
        </Tooltip>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleProfileClose}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        >
          {user && (
            <Box sx={{ px: 2, py: 1 }}>
              <Typography variant="subtitle2">{user.firstName} {user.lastName}</Typography>
              <Typography variant="caption" color="text.secondary">{user.email}</Typography>
            </Box>
          )}
          <Divider />
          <MenuItem onClick={() => { navigate('/settings'); handleProfileClose(); }}>
            <ListItemIcon><AccountCircleIcon fontSize="small" /></ListItemIcon>
            Profile
          </MenuItem>
          <MenuItem onClick={handleLogout}>
            <ListItemIcon><LogoutIcon fontSize="small" /></ListItemIcon>
            Logout
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
