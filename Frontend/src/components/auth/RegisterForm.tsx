import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Box, Card, CardContent, TextField, Button, Typography, Link,
  Alert, InputAdornment, IconButton, Grid,
} from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import { registerSchema, RegisterFormData } from '../../utils/validators';
import { useRegisterMutation } from '../../api/authApi';
import { useAppDispatch } from '../../app/store';
import { setCredentials } from '../../store/authSlice';
import toast from 'react-hot-toast';

export default function RegisterForm() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [registerUser, { isLoading, error }] = useRegisterMutation();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      const { confirmPassword: _, ...payload } = data;
      const result = await registerUser(payload).unwrap();
      dispatch(setCredentials(result));
      toast.success('Account created successfully!');
      navigate('/dashboard');
    } catch {
      // Error handled by RTK Query middleware
    }
  };

  return (
    <Card sx={{ maxWidth: 540, width: '100%', mx: 'auto' }}>
      <CardContent sx={{ p: 4 }}>
        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <PersonAddIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
          <Typography variant="h5" fontWeight={700}>Create Account</Typography>
          <Typography variant="body2" color="text.secondary">Join TaskFlow today</Typography>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>Registration failed. Email may already be in use.</Alert>}

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth label="First Name" margin="normal"
                {...register('firstName')} error={!!errors.firstName} helperText={errors.firstName?.message}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth label="Last Name" margin="normal"
                {...register('lastName')} error={!!errors.lastName} helperText={errors.lastName?.message}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth label="Email" type="email" margin="normal"
                {...register('email')} error={!!errors.email} helperText={errors.email?.message}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
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
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth label="Confirm Password" margin="normal"
                type={showPassword ? 'text' : 'password'}
                {...register('confirmPassword')} error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
              />
            </Grid>
          </Grid>

          <Button fullWidth type="submit" variant="contained" size="large" disabled={isLoading} sx={{ mt: 3, py: 1.5 }}>
            {isLoading ? 'Creating Account...' : 'Create Account'}
          </Button>
        </Box>

        <Typography variant="body2" textAlign="center" sx={{ mt: 3 }}>
          Already have an account?{' '}
          <Link component={RouterLink} to="/login" fontWeight={600}>Sign In</Link>
        </Typography>
      </CardContent>
    </Card>
  );
}
