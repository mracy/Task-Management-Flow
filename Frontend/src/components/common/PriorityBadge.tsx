import { Chip, ChipProps } from '@mui/material';
import { PRIORITY_COLORS } from '../../utils/constants';

interface Props {
  priority: string;
  size?: ChipProps['size'];
}

export default function PriorityBadge({ priority, size = 'small' }: Props) {
  const color = PRIORITY_COLORS[priority] || '#64748B';

  return (
    <Chip
      label={priority}
      size={size}
      sx={{
        backgroundColor: color + '18',
        color: color,
        fontWeight: 600,
        fontSize: '0.75rem',
      }}
    />
  );
}
