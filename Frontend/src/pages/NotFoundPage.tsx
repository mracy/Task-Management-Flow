import { Box, Typography } from '@mui/material';
import SearchOffIcon from '@mui/icons-material/SearchOff';
import { Link as RouterLink } from 'react-router-dom';
import { Button } from '@mui/material';

export default function NotFoundPage() {
  return (
    <Box sx={{ textAlign: 'center', mt: 10 }}>
      <SearchOffIcon sx={{ fontSize: 80, color: 'text.disabled', mb: 2 }} />
      <Typography variant="h4" fontWeight={700} gutterBottom>404 - Page Not Found</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        The page you're looking for doesn't exist.
      </Typography>
      <Button component={RouterLink} to="/dashboard" variant="contained">
        Go to Dashboard
      </Button>
    </Box>
  );
}
