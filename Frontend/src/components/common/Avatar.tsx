import { Avatar as MuiAvatar, AvatarProps } from '@mui/material';
import { getInitials } from '../../utils/helpers';

interface Props extends AvatarProps {
  firstName: string;
  lastName: string;
  src?: string;
}

const AVATAR_COLORS = [
  '#2563EB', '#7C3AED', '#059669', '#D97706', '#DC2626',
  '#0891B2', '#4F46E5', '#9333EA', '#C2410C', '#16A34A',
];

function getColor(name: string): string {
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length];
}

export default function UserAvatar({ firstName, lastName, src, sx, ...props }: Props) {
  const name = `${firstName} ${lastName}`;
  const initials = getInitials(firstName, lastName);
  const bgColor = getColor(name);

  return (
    <MuiAvatar
      src={src}
      sx={{
        bgcolor: bgColor,
        color: '#fff',
        fontWeight: 600,
        fontSize: '0.875rem',
        width: 36,
        height: 36,
        ...sx,
      }}
      {...props}
    >
      {initials}
    </MuiAvatar>
  );
}
