import { useState } from 'react';
import {
  Card, CardContent, Typography, Box, Chip, IconButton, Menu, MenuItem,
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import PeopleIcon from '@mui/icons-material/People';
import type { ProjectSummary } from '../../types';
import StatusBadge from '../common/StatusBadge';
import { formatDate } from '../../utils/helpers';

interface Props {
  project: ProjectSummary;
  onClick: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export default function ProjectCard({ project, onClick, onEdit, onDelete }: Props) {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  return (
    <Card
      sx={{ cursor: 'pointer', height: '100%', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-2px)' } }}
      onClick={onClick}
    >
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
          <Typography variant="h6" fontWeight={600} noWrap sx={{ flex: 1, mr: 1 }}>
            {project.name}
          </Typography>
          <IconButton size="small" onClick={(e) => { e.stopPropagation(); setAnchorEl(e.currentTarget); }}>
            <MoreVertIcon />
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
            <MenuItem onClick={() => { onEdit(); setAnchorEl(null); }}>Edit</MenuItem>
            <MenuItem onClick={() => { onDelete(); setAnchorEl(null); }} sx={{ color: 'error.main' }}>Delete</MenuItem>
          </Menu>
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
          {project.description?.substring(0, 100) || 'No description'}
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <StatusBadge status={project.status} />
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
            <PeopleIcon fontSize="small" />
            <Typography variant="caption">{project.memberCount}</Typography>
          </Box>
        </Box>
        <Typography variant="caption" color="text.disabled" sx={{ mt: 1, display: 'block' }}>
          Created {formatDate(project.createdAt)}
        </Typography>
      </CardContent>
    </Card>
  );
}
