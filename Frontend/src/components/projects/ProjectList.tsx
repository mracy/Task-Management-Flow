import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Grid, ToggleButton, ToggleButtonGroup } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useGetProjectsQuery, useCreateProjectMutation, useDeleteProjectMutation } from '../../api/projectApi';
import { usePagination } from '../../hooks/usePagination';
import { useAuth } from '../../hooks/useAuth';
import SearchBar from '../common/SearchBar';
import ProjectCard from './ProjectCard';
import ProjectForm from './ProjectForm';
import ConfirmDialog from '../common/ConfirmDialog';
import { LoadingSkeleton } from '../common/LoadingSkeleton';
import { ErrorMessage } from '../common/ErrorBoundary';
import EmptyState from '../common/EmptyState';
import { ProjectFormData } from '../../utils/validators';
import toast from 'react-hot-toast';

export default function ProjectList() {
  const navigate = useNavigate();
  const { isManager } = useAuth();
  const { page, size, goToPage } = usePagination();
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ id: string; name: string } | null>(null);

  const { data, isLoading, error, refetch } = useGetProjectsQuery({ page, size, search, status: statusFilter || undefined });
  const [createProject, { isLoading: creating }] = useCreateProjectMutation();
  const [deleteProject, { isLoading: deleting }] = useDeleteProjectMutation();

  const handleSearch = useCallback((q: string) => { setSearch(q); goToPage(0); }, [goToPage]);
  const handleStatusFilter = (_: React.MouseEvent, val: string | null) => { setStatusFilter(val); goToPage(0); };

  const handleCreate = async (formData: ProjectFormData) => {
    try {
      await createProject(formData).unwrap();
      toast.success('Project created successfully');
      setFormOpen(false);
      refetch();
    } catch {
      toast.error('Failed to create project');
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteProject(deleteTarget.id).unwrap();
      toast.success('Project deleted');
      setDeleteTarget(null);
    } catch {
      toast.error('Failed to delete project');
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Typography variant="h4" fontWeight={700}>Projects</Typography>
        {isManager && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setFormOpen(true)}>
            New Project
          </Button>
        )}
      </Box>

      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap', alignItems: 'center' }}>
        <SearchBar onSearch={handleSearch} placeholder="Search projects..." />
        <ToggleButtonGroup value={statusFilter} exclusive onChange={handleStatusFilter} size="small">
          <ToggleButton value="">All</ToggleButton>
          <ToggleButton value="IN_PROGRESS">Active</ToggleButton>
          <ToggleButton value="COMPLETED">Completed</ToggleButton>
          <ToggleButton value="ON_HOLD">On Hold</ToggleButton>
        </ToggleButtonGroup>
      </Box>

      {isLoading ? <LoadingSkeleton rows={6} /> : error ? (
        <ErrorMessage title="Failed to load projects" onRetry={refetch} />
      ) : data && data.content.length > 0 ? (
        <>
          <Grid container spacing={3}>
            {data.content.map((project) => (
              <Grid item xs={12} sm={6} md={4} key={project.id}>
                <ProjectCard
                  project={project}
                  onClick={() => navigate(`/projects/${project.id}`)}
                  onEdit={() => { /* TODO: open edit dialog */ toast('Edit coming soon'); }}
                  onDelete={() => setDeleteTarget({ id: project.id, name: project.name })}
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
        <EmptyState title="No projects found" message="Create your first project to get started" action={isManager ? { label: 'New Project', onClick: () => setFormOpen(true) } : undefined} />
      )}

      <ProjectForm open={formOpen} onClose={() => setFormOpen(false)} onSubmit={handleCreate} loading={creating} />
      <ConfirmDialog open={!!deleteTarget} title="Delete Project" message={`Are you sure you want to delete "${deleteTarget?.name}"?`} confirmLabel="Delete" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)} loading={deleting} danger />
    </Box>
  );
}
