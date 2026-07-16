import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Chip, Divider, Grid, TextField, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useState } from 'react';
import { useGetTaskByIdQuery, useDeleteTaskMutation, useChangeTaskStatusMutation } from '../api/taskApi';
import { useGetCommentsQuery } from '../api/commentApi';
import { useAuth } from '../hooks/useAuth';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { ErrorMessage } from '../components/common/ErrorBoundary';
import StatusBadge from '../components/common/StatusBadge';
import PriorityBadge from '../components/common/PriorityBadge';
import UserAvatar from '../components/common/Avatar';
import CommentList from '../components/comments/CommentList';
import CommentForm from '../components/comments/CommentForm';
import ConfirmDialog from '../components/common/ConfirmDialog';
import { formatDate } from '../utils/helpers';
import { TASK_STATUSES } from '../utils/constants';
import toast from 'react-hot-toast';

export default function TaskDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isManager } = useAuth();

  const { data: task, isLoading, error, refetch } = useGetTaskByIdQuery(id!);
  const { data: comments } = useGetCommentsQuery(id!);
  const [deleteTask, { isLoading: deleting }] = useDeleteTaskMutation();
  const [changeStatus] = useChangeTaskStatusMutation();
  const [deleteConfirm, setDeleteConfirm] = useState(false);

  const handleDelete = async () => {
    try {
      await deleteTask(id!).unwrap();
      toast.success('Task deleted');
      navigate('/tasks');
    } catch {
      toast.error('Failed to delete task');
    }
  };

  const handleStatusChange = async (status: string) => {
    try {
      await changeStatus({ id: id!, data: { status: status as never } }).unwrap();
      toast.success('Status updated');
    } catch {
      toast.error('Failed to update status');
    }
  };

  if (isLoading) return <LoadingSkeleton rows={4} />;
  if (error) return <ErrorMessage title="Failed to load task" onRetry={refetch} />;
  if (!task) return null;

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 2 }}>
        Back
      </Button>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" fontWeight={700}>{task.title}</Typography>
          <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
            <PriorityBadge priority={task.priority} />
            <StatusBadge status={task.status} />
          </Box>
        </Box>
        {isManager && (
          <Button variant="outlined" color="error" onClick={() => setDeleteConfirm(true)}>
            Delete Task
          </Button>
        )}
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Typography variant="h6" fontWeight={600} gutterBottom>Description</Typography>
          <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', color: 'text.secondary', mb: 3 }}>
            {task.description}
          </Typography>

          <Divider sx={{ my: 3 }} />

          <Typography variant="h6" fontWeight={600} gutterBottom>
            Comments ({comments?.content?.length || 0})
          </Typography>
          <CommentList comments={comments?.content || []} taskId={id!} />
          <CommentForm taskId={id!} />
        </Grid>

        <Grid item xs={12} md={4}>
          <Box sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>Details</Typography>

            {isManager && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="caption" color="text.secondary">Status</Typography>
                <FormControl fullWidth size="small" sx={{ mt: 0.5 }}>
                  <Select value={task.status} onChange={(e) => handleStatusChange(e.target.value)}>
                    {TASK_STATUSES.map((s) => <MenuItem key={s} value={s}>{s.replace(/_/g, ' ')}</MenuItem>)}
                  </Select>
                </FormControl>
              </Box>
            )}

            <Box sx={{ mb: 2 }}>
              <Typography variant="caption" color="text.secondary">Due Date</Typography>
              <Typography variant="body2">{formatDate(task.dueDate)}</Typography>
            </Box>

            <Box sx={{ mb: 2 }}>
              <Typography variant="caption" color="text.secondary">Project</Typography>
              <Typography variant="body2" sx={{ cursor: 'pointer', color: 'primary.main' }} onClick={() => navigate(`/projects/${task.projectId}`)}>
                {task.projectName}
              </Typography>
            </Box>

            <Box sx={{ mb: 2 }}>
              <Typography variant="caption" color="text.secondary">Reporter</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                <UserAvatar firstName={task.reporter.firstName} lastName={task.reporter.lastName} sx={{ width: 24, height: 24 }} />
                <Typography variant="body2">{task.reporter.firstName} {task.reporter.lastName}</Typography>
              </Box>
            </Box>

            {task.assignee && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="caption" color="text.secondary">Assignee</Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                  <UserAvatar firstName={task.assignee.firstName} lastName={task.assignee.lastName} sx={{ width: 24, height: 24 }} />
                  <Typography variant="body2">{task.assignee.firstName} {task.assignee.lastName}</Typography>
                </Box>
              </Box>
            )}

            <Box>
              <Typography variant="caption" color="text.secondary">Created</Typography>
              <Typography variant="body2">{formatDate(task.createdAt)}</Typography>
            </Box>
          </Box>
        </Grid>
      </Grid>

      <ConfirmDialog open={deleteConfirm} title="Delete Task" message={`Delete "${task.title}"?`} confirmLabel="Delete" onConfirm={handleDelete} onCancel={() => setDeleteConfirm(false)} loading={deleting} danger />
    </Box>
  );
}
