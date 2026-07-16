import { Grid, Card, CardContent, Typography, Box } from '@mui/material';
import FolderOpenIcon from '@mui/icons-material/FolderOpen';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PendingIcon from '@mui/icons-material/Pending';
import WarningIcon from '@mui/icons-material/Warning';
import type { DashboardStats } from '../../types';

interface Props {
  stats: DashboardStats;
}

const cards = [
  { key: 'totalProjects', label: 'Total Projects', icon: FolderOpenIcon, color: '#2563EB' },
  { key: 'completedTasks', label: 'Completed Tasks', icon: CheckCircleIcon, color: '#059669' },
  { key: 'pendingTasks', label: 'Pending Tasks', icon: PendingIcon, color: '#D97706' },
  { key: 'overdueTasks', label: 'Overdue Tasks', icon: WarningIcon, color: '#DC2626' },
] as const;

export default function StatsCards({ stats }: Props) {
  return (
    <Grid container spacing={3}>
      {cards.map(({ key, label, icon: Icon, color }) => (
        <Grid item xs={12} sm={6} md={3} key={key}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="h4" fontWeight={700}>{stats[key]}</Typography>
                  <Typography variant="body2" color="text.secondary">{label}</Typography>
                </Box>
                <Box
                  sx={{
                    width: 48, height: 48, borderRadius: 2,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    bgcolor: color + '15',
                  }}
                >
                  <Icon sx={{ color, fontSize: 28 }} />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}
