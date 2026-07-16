import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Box,
} from '@mui/material';
import { projectSchema, ProjectFormData } from '../../utils/validators';
import { PROJECT_STATUSES } from '../../utils/constants';
import type { Project } from '../../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: ProjectFormData) => void;
  project?: Project | null;
  loading?: boolean;
}

export default function ProjectForm({ open, onClose, onSubmit, project, loading }: Props) {
  const isEdit = !!project;
  const {
    register, handleSubmit, reset,
    formState: { errors },
  } = useForm<ProjectFormData>({
    resolver: zodResolver(projectSchema),
    defaultValues: {
      name: project?.name || '',
      description: project?.description || '',
      status: project?.status || 'PLANNING',
    },
  });

  const handleClose = () => {
    reset();
    onClose();
  };

  const handleFormSubmit = (data: ProjectFormData) => {
    onSubmit(data);
    reset();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Project' : 'Create Project'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(handleFormSubmit)}>
        <DialogContent>
          <TextField fullWidth label="Project Name" margin="normal" {...register('name')}
            error={!!errors.name} helperText={errors.name?.message} autoFocus />
          <TextField fullWidth label="Description" margin="normal" multiline rows={3}
            {...register('description')} error={!!errors.description} helperText={errors.description?.message} />
          <TextField select fullWidth label="Status" margin="normal" {...register('status')}
            error={!!errors.status} helperText={errors.status?.message}>
            {PROJECT_STATUSES.map((s) => <MenuItem key={s} value={s}>{s.replace(/_/g, ' ')}</MenuItem>)}
          </TextField>
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
