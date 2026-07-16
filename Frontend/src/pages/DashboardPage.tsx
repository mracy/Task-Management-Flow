import { Box, Typography } from '@mui/material';
import { useGetDashboardQuery } from '../api/dashboardApi';
import { StatsSkeleton } from '../components/common/LoadingSkeleton';
import { ErrorMessage } from '../components/common/ErrorBoundary';
import StatsCards from '../components/dashboard/StatsCards';
import TaskStatusChart from '../components/dashboard/TaskStatusChart';
import PriorityChart from '../components/dashboard/PriorityChart';
import RecentActivity from '../components/dashboard/RecentActivity';

export default function DashboardPage() {
  const { data, isLoading, error, refetch } = useGetDashboardQuery();

  if (isLoading) return <StatsSkeleton />;
  if (error) return <ErrorMessage title="Failed to load dashboard" message="Please try again" onRetry={refetch} />;
  if (!data) return null;

  return (
    <Box>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 3 }}>Dashboard</Typography>
      <StatsCards stats={data} />
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 3, mt: 3 }}>
        <TaskStatusChart data={data.tasksByStatus} />
        <PriorityChart data={data.tasksByPriority} />
      </Box>
      <RecentActivity activities={data.recentActivities} />
    </Box>
  );
}
