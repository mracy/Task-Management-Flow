import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Link,
  Alert,
  InputAdornment,
  IconButton,
} from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import LoginIcon from '@mui/icons-material/Login';
import { loginSchema, LoginFormData } from '../../utils/validators';
import { useLoginMutation } from '../../api/authApi';
import { useAppDispatch } from '../../app/store';
import { setCredentials } from '../../store/authSlice';
import toast from 'react-hot-toast';

export default function LoginForm() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [login, { isLoading, error }] = useLoginMutation();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      const result = await login(data).unwrap();
      dispatch(setCredentials(result));
      toast.success(`Welcome back, ${result.user.firstName}!`);
      navigate('/dashboard');
    } catch {
      // Error handled by RTK Query middleware
    }
  };

  return (
    <Card sx={{ maxWidth: 440, width: '100%', mx: 'auto' }}>
      <CardContent sx={{ p: 4 }}>
        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <LoginIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
          <Typography variant="h5" fontWeight={700}>Sign In</Typography>
          <Typography variant="body2" color="text.secondary">Welcome back to TaskFlow</Typography>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>Invalid email or password</Alert>}

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <TextField
            fullWidth label="Email" type="email" margin="normal"
            {...register('email')} error={!!errors.email} helperText={errors.email?.message} autoFocus
          />
          <TextField
            fullWidth label="Password" margin="normal"
            type={showPassword ? 'text' : 'password'}
            {...register('password')} error={!!errors.password} helperText={errors.password?.message}
            InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
          />
          <Button fullWidth type="submit" variant="contained" size="large" disabled={isLoading} sx={{ mt: 3, py: 1.5 }}>
            {isLoading ? 'Signing in...' : 'Sign In'}
          </Button>
        </Box>

        <Typography variant="body2" textAlign="center" sx={{ mt: 3 }}>
          Don't have an account?{' '}
          <Link component={RouterLink} to="/register" fontWeight={600}>Sign Up</Link>
        </Typography>
      </CardContent>
    </Card>
  );
}
