import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Grid, MenuItem, TextField } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useGetTasksQuery, useDeleteTaskMutation } from '../../api/taskApi';
import { useGetProjectsQuery } from '../../api/projectApi';
import { usePagination } from '../../hooks/usePagination';
import { useAuth } from '../../hooks/useAuth';
import TaskCard from './TaskCard';
import TaskFiltersBar from './TaskFilters';
import TaskForm from './TaskForm';
import ConfirmDialog from '../common/ConfirmDialog';
import { LoadingSkeleton } from '../common/LoadingSkeleton';
import { ErrorMessage } from '../common/ErrorBoundary';
import EmptyState from '../common/EmptyState';
import { TaskFormData } from '../../utils/validators';
import { useCreateTaskMutation } from '../../api/taskApi';
import type { TaskFilters } from '../../types';
import toast from 'react-hot-toast';

export default function TaskList({ projectId }: { projectId?: string } = {}) {
  const navigate = useNavigate();
  const { isManager } = useAuth();
  const { page, size, goToPage } = usePagination();
  const [filters, setFilters] = useState<TaskFilters>({ projectId, page, size });
  const [formOpen, setFormOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ id: string; title: string } | null>(null);

  const { data, isLoading, error, refetch } = useGetTasksQuery({ ...filters, page, size });
  const { data: projectsData } = useGetProjectsQuery({ page: 0, size: 100 });
  const [createTask, { isLoading: creating }] = useCreateTaskMutation();
  const [deleteTask, { isLoading: deleting }] = useDeleteTaskMutation();

  const handleFilterChange = useCallback((partial: Partial<TaskFilters>) => {
    setFilters((prev) => ({ ...prev, ...partial }));
    goToPage(0);
  }, [goToPage]);

  const handleCreate = async (formData: TaskFormData) => {
    try {
      await createTask(formData).unwrap();
      toast.success('Task created successfully');
      setFormOpen(false);
      refetch();
    } catch {
      toast.error('Failed to create task');
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteTask(deleteTarget.id).unwrap();
      toast.success('Task deleted');
      setDeleteTarget(null);
    } catch {
      toast.error('Failed to delete task');
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Typography variant="h4" fontWeight={700}>Tasks</Typography>
        {isManager && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setFormOpen(true)}>
            New Task
          </Button>
        )}
      </Box>

      <TaskFiltersBar filters={filters} onChange={handleFilterChange} />

      <Box sx={{ mt: 3 }}>
        {isLoading ? <LoadingSkeleton rows={6} /> : error ? (
          <ErrorMessage title="Failed to load tasks" onRetry={refetch} />
        ) : data && data.content.length > 0 ? (
          <>
            <Grid container spacing={2}>
              {data.content.map((task) => (
                <Grid item xs={12} sm={6} md={4} key={task.id}>
                  <TaskCard
                    task={task}
                    onClick={() => navigate(`/tasks/${task.id}`)}
                    onEdit={() => { toast('Edit coming soon'); }}
                    onDelete={() => setDeleteTarget({ id: task.id, title: task.title })}
                  />
                </Grid>
              ))}
            </Grid>
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
              <Button disabled={page === 0} onClick={() => goToPage(page - 1)}>Previous</Button>
              <Typography sx={{ mx: 2, alignSelf: 'center' }}>Page {page + 1} of {data.totalPages}</Typography>
              <Button disabled={page >= data.totalPages - 1} onClick={() => goToPage(page + 1)}>Next</Button>
            </Box>
          </>
        ) : (
          <EmptyState title="No tasks found" message="Create your first task" action={isManager ? { label: 'New Task', onClick: () => setFormOpen(true) } : undefined} />
        )}
      </Box>

      <TaskForm
        open={formOpen} onClose={() => setFormOpen(false)} onSubmit={handleCreate} loading={creating}
        projects={projectsData?.content?.map((p) => ({ id: p.id, name: p.name })) || []}
      />
      <ConfirmDialog open={!!deleteTarget} title="Delete Task" message={`Are you sure you want to delete "${deleteTarget?.title}"?`} confirmLabel="Delete" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)} loading={deleting} danger />
    </Box>
  );
}
