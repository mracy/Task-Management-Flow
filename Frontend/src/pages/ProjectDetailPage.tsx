import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Chip, Divider, Grid, List, ListItem, ListItemAvatar, ListItemText } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EditIcon from '@mui/icons-material/Edit';
import { useGetProjectByIdQuery } from '../api/projectApi';
import { useGetTasksQuery } from '../api/taskApi';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { ErrorMessage } from '../components/common/ErrorBoundary';
import StatusBadge from '../components/common/StatusBadge';
import UserAvatar from '../components/common/Avatar';
import TaskCard from '../components/tasks/TaskCard';
import { formatDate } from '../utils/helpers';
import { useAuth } from '../hooks/useAuth';
import type { Project } from '../types';
import type { Task } from '../types';

export default function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isManager } = useAuth();

  const { data: project, isLoading: projectLoading, error: projectError, refetch: refetchProject } = useGetProjectByIdQuery(id!);
  const { data: tasksData, isLoading: tasksLoading } = useGetTasksQuery({ projectId: id, size: 50 });

  if (projectLoading) return <LoadingSkeleton rows={4} />;
  if (projectError) return <ErrorMessage title="Failed to load project" onRetry={refetchProject} />;
  if (!project) return null;

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/projects')} sx={{ mb: 2 }}>
        Back to Projects
      </Button>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" fontWeight={700}>{project.name}</Typography>
          <Box sx={{ display: 'flex', gap: 1, mt: 1, alignItems: 'center' }}>
            <StatusBadge status={project.status} />
            <Typography variant="body2" color="text.secondary">Created {formatDate(project.createdAt)}</Typography>
          </Box>
        </Box>
        {isManager && (
          <Button variant="outlined" startIcon={<EditIcon />}>Edit Project</Button>
        )}
      </Box>

      <Typography variant="body1" sx={{ mb: 3, color: 'text.secondary' }}>
        {project.description}
      </Typography>

      <Divider sx={{ my: 3 }} />

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Typography variant="h6" fontWeight={600} gutterBottom>Tasks ({tasksData?.content?.length || 0})</Typography>
          {tasksLoading ? <LoadingSkeleton rows={3} /> : (
            <Grid container spacing={2}>
              {tasksData?.content?.map((task: Task) => (
                <Grid item xs={12} sm={6} key={task.id}>
                  <TaskCard task={task} onClick={() => navigate(`/tasks/${task.id}`)} onEdit={() => {}} onDelete={() => {}} />
                </Grid>
              ))}
            </Grid>
          )}
        </Grid>

        <Grid item xs={12} md={4}>
          <Typography variant="h6" fontWeight={600} gutterBottom>Team ({project.members?.length || 0})</Typography>
          <List disablePadding>
            {project.members?.map((member: Project['members'][number]) => (
              <ListItem key={member.id} sx={{ px: 0 }}>
                <ListItemAvatar>
                  <UserAvatar firstName={member.firstName} lastName={member.lastName} />
                </ListItemAvatar>
                <ListItemText
                  primary={`${member.firstName} ${member.lastName}`}
                  secondary={member.email}
                />
              </ListItem>
            ))}
          </List>
        </Grid>
      </Grid>
    </Box>
  );
}
