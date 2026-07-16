import { Box, Typography } from '@mui/material';
import LoginForm from '../components/auth/LoginForm';
import TaskIcon from '@mui/icons-material/Task';

export default function LoginPage() {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: 'background.default',
        p: 2,
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 4, gap: 1 }}>
        <TaskIcon sx={{ fontSize: 36, color: 'primary.main' }} />
        <Typography variant="h4" fontWeight={700}>TaskFlow</Typography>
      </Box>
      <LoginForm />
    </Box>
  );
}
