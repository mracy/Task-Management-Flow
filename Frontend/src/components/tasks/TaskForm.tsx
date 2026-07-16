import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Box, Grid,
} from '@mui/material';
import { taskSchema, TaskFormData } from '../../utils/validators';
import { PRIORITIES, TASK_STATUSES } from '../../utils/constants';
import type { Task } from '../../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: TaskFormData) => void;
  task?: Task | null;
  loading?: boolean;
  projects?: { id: string; name: string }[];
}

export default function TaskForm({ open, onClose, onSubmit, task, loading, projects = [] }: Props) {
  const isEdit = !!task;
  const {
    register, handleSubmit, reset,
    formState: { errors },
  } = useForm<TaskFormData>({
    resolver: zodResolver(taskSchema),
    defaultValues: {
      title: task?.title || '',
      description: task?.description || '',
      priority: task?.priority || 'MEDIUM',
      status: task?.status || 'TODO',
      dueDate: task?.dueDate ? task.dueDate.substring(0, 10) : '',
      projectId: task?.projectId || '',
    },
  });

  const handleClose = () => { reset(); onClose(); };
  const handleFormSubmit = (data: TaskFormData) => { onSubmit(data); reset(); };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Task' : 'Create Task'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(handleFormSubmit)}>
        <DialogContent>
          <TextField fullWidth label="Title" margin="normal" {...register('title')}
            error={!!errors.title} helperText={errors.title?.message} autoFocus />
          <TextField fullWidth label="Description" margin="normal" multiline rows={3}
            {...register('description')} error={!!errors.description} helperText={errors.description?.message} />
          <Grid container spacing={2} sx={{ mt: 0 }}>
            <Grid item xs={12} sm={6}>
              <TextField select fullWidth label="Priority" margin="normal" {...register('priority')}
                error={!!errors.priority} helperText={errors.priority?.message}>
                {PRIORITIES.map((p) => <MenuItem key={p} value={p}>{p}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField select fullWidth label="Status" margin="normal" {...register('status')}
                error={!!errors.status} helperText={errors.status?.message}>
                {TASK_STATUSES.map((s) => <MenuItem key={s} value={s}>{s.replace(/_/g, ' ')}</MenuItem>)}
              </TextField>
            </Grid>
          </Grid>
          <TextField fullWidth label="Due Date" type="date" margin="normal"
            InputLabelProps={{ shrink: true }}
            {...register('dueDate')} error={!!errors.dueDate} helperText={errors.dueDate?.message} />
          {!isEdit && projects.length > 0 && (
            <TextField select fullWidth label="Project" margin="normal" {...register('projectId')}
              error={!!errors.projectId} helperText={errors.projectId?.message}>
              {projects.map((p) => <MenuItem key={p.id} value={p.id}>{p.name}</MenuItem>)}
            </TextField>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button type="submit" variant="contained" disabled={loading}>
            {loading ? 'Saving...' : isEdit ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}
