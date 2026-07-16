import { useState } from 'react';
import {
  Card, CardContent, Typography, Box, Chip, IconButton, Menu, MenuItem,
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import type { Task } from '../../types';
import StatusBadge from '../common/StatusBadge';
import PriorityBadge from '../common/PriorityBadge';
import UserAvatar from '../common/Avatar';
import { formatDate, isOverdue } from '../../utils/helpers';

interface Props {
  task: Task;
  onClick: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export default function TaskCard({ task, onClick, onEdit, onDelete }: Props) {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const overdue = isOverdue(task.dueDate) && task.status !== 'DONE';

  return (
    <Card
      sx={{ cursor: 'pointer', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-2px)' } }}
      onClick={onClick}
    >
      <CardContent sx={{ pb: '12px !important' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
          <Typography variant="subtitle1" fontWeight={600} sx={{ flex: 1, mr: 1 }}>
            {task.title}
          </Typography>
          <IconButton size="small" onClick={(e) => { e.stopPropagation(); setAnchorEl(e.currentTarget); }}>
            <MoreVertIcon fontSize="small" />
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
            <MenuItem onClick={() => { onEdit(); setAnchorEl(null); }}>Edit</MenuItem>
            <MenuItem onClick={() => { onDelete(); setAnchorEl(null); }} sx={{ color: 'error.main' }}>Delete</MenuItem>
          </Menu>
        </Box>

        <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5, minHeight: 32 }}>
          {task.description?.substring(0, 80) || 'No description'}
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, mb: 1, flexWrap: 'wrap' }}>
          <PriorityBadge priority={task.priority} />
          <StatusBadge status={task.status} />
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: overdue ? 'error.main' : 'text.secondary' }}>
            <CalendarTodayIcon fontSize="small" />
            <Typography variant="caption" sx={{ color: overdue ? 'error.main' : 'inherit', fontWeight: overdue ? 600 : 400 }}>
              {formatDate(task.dueDate)}
            </Typography>
          </Box>
          {task.assignee && (
            <UserAvatar
              firstName={task.assignee.firstName}
              lastName={task.assignee.lastName}
              src={task.assignee.avatarUrl}
              sx={{ width: 28, height: 28, fontSize: '0.7rem' }}
            />
          )}
        </Box>
      </CardContent>
    </Card>
  );
}
