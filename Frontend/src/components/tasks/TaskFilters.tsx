import { Box, TextField, MenuItem } from '@mui/material';
import { PRIORITIES, TASK_STATUSES } from '../../utils/constants';
import type { TaskFilters } from '../../types';
import SearchBar from '../common/SearchBar';

interface Props {
  filters: TaskFilters;
  onChange: (filters: Partial<TaskFilters>) => void;
}

export default function TaskFiltersBar({ filters, onChange }: Props) {
  return (
    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
      <SearchBar
        onSearch={(q) => onChange({ search: q })}
        placeholder="Search tasks..."
      />
      <TextField
        select size="small" label="Status" value={filters.status || ''}
        onChange={(e) => onChange({ status: (e.target.value || undefined) as TaskFilters['status'] })}
        sx={{ minWidth: 140 }}
      >
        <MenuItem value="">All Statuses</MenuItem>
        {TASK_STATUSES.map((s) => <MenuItem key={s} value={s}>{s.replace(/_/g, ' ')}</MenuItem>)}
      </TextField>
      <TextField
        select size="small" label="Priority" value={filters.priority || ''}
        onChange={(e) => onChange({ priority: (e.target.value || undefined) as TaskFilters['priority'] })}
        sx={{ minWidth: 140 }}
      >
        <MenuItem value="">All Priorities</MenuItem>
        {PRIORITIES.map((p) => <MenuItem key={p} value={p}>{p}</MenuItem>)}
      </TextField>
    </Box>
  );
}
