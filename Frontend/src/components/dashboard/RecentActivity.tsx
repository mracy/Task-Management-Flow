import {
  Card, CardContent, Typography, List, ListItem, ListItemText, ListItemAvatar, Divider, Box,
} from '@mui/material';
import HistoryIcon from '@mui/icons-material/History';
import type { RecentActivity } from '../../types';
import { timeAgo } from '../../utils/helpers';

interface Props {
  activities: RecentActivity[];
}

export default function RecentActivity({ activities }: Props) {
  return (
    <Card sx={{ mt: 3 }}>
      <CardContent>
        <Typography variant="h6" fontWeight={600} gutterBottom>Recent Activities</Typography>
        {activities.length === 0 ? (
          <Typography color="text.secondary" sx={{ py: 3, textAlign: 'center' }}>No recent activities</Typography>
        ) : (
          <List disablePadding>
            {activities.map((activity, idx) => (
              <Box key={idx}>
                {idx > 0 && <Divider />}
                <ListItem sx={{ py: 1.5 }}>
                  <ListItemAvatar>
                    <Box
                      sx={{
                        width: 40, height: 40, borderRadius: '50%',
                        bgcolor: 'action.hover', display: 'flex', alignItems: 'center', justifyContent: 'center',
                      }}
                    >
                      <HistoryIcon fontSize="small" color="action" />
                    </Box>
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Typography variant="body2">
                        <strong>{activity.userName}</strong> {activity.action}
                      </Typography>
                    }
                    secondary={activity.description || timeAgo(activity.timestamp)}
                  />
                </ListItem>
              </Box>
            ))}
          </List>
        )}
      </CardContent>
    </Card>
  );
}
