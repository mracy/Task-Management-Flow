import { Box, Typography } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { Link as RouterLink } from 'react-router-dom';
import { Button } from '@mui/material';

export default function UnauthorizedPage() {
  return (
    <Box sx={{ textAlign: 'center', mt: 10 }}>
      <ErrorOutlineIcon sx={{ fontSize: 80, color: 'warning.main', mb: 2 }} />
      <Typography variant="h4" fontWeight={700} gutterBottom>Access Denied</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        You don't have permission to access this page.
      </Typography>
      <Button component={RouterLink} to="/dashboard" variant="contained">
        Go to Dashboard
      </Button>
    </Box>
  );
}
