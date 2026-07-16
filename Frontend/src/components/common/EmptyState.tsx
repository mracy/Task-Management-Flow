import { Box, Typography, Button } from '@mui/material';
import InboxIcon from '@mui/icons-material/Inbox';

interface Props {
  title?: string;
  message?: string;
  icon?: React.ReactNode;
  action?: { label: string; onClick: () => void };
}

export default function EmptyState({ title = 'No data', message, icon, action }: Props) {
  return (
    <Box sx={{ p: 6, textAlign: 'center' }}>
      {icon || <InboxIcon sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }} />}
      <Typography variant="h6" color="text.secondary" gutterBottom>
        {title}
      </Typography>
      {message && (
        <Typography variant="body2" color="text.disabled" sx={{ mb: 3 }}>
          {message}
        </Typography>
      )}
      {action && (
        <Button variant="contained" onClick={action.onClick}>
          {action.label}
        </Button>
      )}
    </Box>
  );
}
