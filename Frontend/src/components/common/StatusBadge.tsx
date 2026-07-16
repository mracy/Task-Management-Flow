import { Chip, ChipProps } from '@mui/material';
import { STATUS_COLORS } from '../../utils/constants';

interface Props {
  status: string;
  size?: ChipProps['size'];
}

export default function StatusBadge({ status, size = 'small' }: Props) {
  const color = STATUS_COLORS[status] || '#64748B';
  const label = status.replace(/_/g, ' ');

  return (
    <Chip
      label={label}
      size={size}
      sx={{
        backgroundColor: color + '18',
        color: color,
        fontWeight: 600,
        fontSize: '0.75rem',
        textTransform: 'capitalize',
      }}
    />
  );
}
