import { Box, Typography, List, ListItem, ListItemText, ListItemAvatar, Divider, Button, IconButton, Chip } from '@mui/material';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import { useGetNotificationsQuery, useMarkAsReadMutation, useMarkAllAsReadMutation } from '../../api/notificationApi';
import { LoadingSkeleton } from '../common/LoadingSkeleton';
import { ErrorMessage } from '../common/ErrorBoundary';
import EmptyState from '../common/EmptyState';
import { timeAgo } from '../../utils/helpers';
import toast from 'react-hot-toast';

const TYPE_COLORS: Record<string, string> = {
  TASK_ASSIGNED: '#2563EB',
  TASK_UPDATED: '#D97706',
  PROJECT_CREATED: '#059669',
  COMMENT_ADDED: '#7C3AED',
};

export default function NotificationList() {
  const { data: notifications, isLoading, error, refetch } = useGetNotificationsQuery();
  const [markAsRead] = useMarkAsReadMutation();
  const [markAllAsRead] = useMarkAllAsReadMutation();

  const handleMarkAllRead = async () => {
    try {
      await markAllAsRead().unwrap();
      toast.success('All notifications marked as read');
    } catch {
      toast.error('Failed to mark notifications');
    }
  };

  const handleMarkRead = async (id: string) => {
    try {
      await markAsRead(id).unwrap();
    } catch {
      // silent
    }
  };

  if (isLoading) return <LoadingSkeleton rows={5} />;
  if (error) return <ErrorMessage title="Failed to load notifications" onRetry={refetch} />;

  const unreadCount = notifications?.filter((n) => !n.read).length || 0;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight={700}>
          Notifications
          {unreadCount > 0 && <Chip label={unreadCount} color="primary" size="small" sx={{ ml: 1 }} />}
        </Typography>
        {unreadCount > 0 && (
          <Button startIcon={<MarkEmailReadIcon />} onClick={handleMarkAllRead}>Mark All Read</Button>
        )}
      </Box>

      {!notifications || notifications.length === 0 ? (
        <EmptyState title="No notifications" message="You're all caught up!" />
      ) : (
        <List disablePadding>
          {notifications.map((notification, idx) => (
            <Box key={notification.id}>
              {idx > 0 && <Divider />}
              <ListItem
                sx={{ bgcolor: notification.read ? 'transparent' : 'action.hover', py: 2 }}
                secondaryAction={
                  !notification.read && (
                    <IconButton size="small" onClick={() => handleMarkRead(notification.id)}>
                      <MarkEmailReadIcon fontSize="small" />
                    </IconButton>
                  )
                }
              >
                <ListItemAvatar>
                  <Box
                    sx={{
                      width: 40, height: 40, borderRadius: '50%', display: 'flex',
                      alignItems: 'center', justifyContent: 'center',
                      bgcolor: (TYPE_COLORS[notification.type] || '#64748B') + '20',
                    }}
                  >
                    <Typography sx={{ fontSize: 18 }}>
                      {notification.type === 'TASK_ASSIGNED' ? '📋' : notification.type === 'TASK_UPDATED' ? '✏️' : notification.type === 'PROJECT_CREATED' ? '📁' : '💬'}
                    </Typography>
                  </Box>
                </ListItemAvatar>
                <ListItemText
                  primary={<Typography variant="body1" sx={{ fontWeight: notification.read ? 400 : 600 }}>{notification.message}</Typography>}
                  secondary={timeAgo(notification.createdAt)}
                />
              </ListItem>
            </Box>
          ))}
        </List>
      )}
    </Box>
  );
}
