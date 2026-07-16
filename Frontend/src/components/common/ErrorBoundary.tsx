import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

interface Props {
  title?: string;
  message?: string;
  onRetry?: () => void;
}

export class ErrorBoundary extends React.Component<
  { children: React.ReactNode; fallback?: React.ReactNode },
  { hasError: boolean; error: Error | null }
> {
  constructor(props: { children: React.ReactNode; fallback?: React.ReactNode }) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback;
      return (
        <Box sx={{ p: 4, textAlign: 'center' }}>
          <ErrorOutlineIcon sx={{ fontSize: 64, color: 'error.main', mb: 2 }} />
          <Typography variant="h5" gutterBottom>Something went wrong</Typography>
          <Typography color="text.secondary" sx={{ mb: 2 }}>
            {this.state.error?.message || 'An unexpected error occurred'}
          </Typography>
          <Button variant="contained" onClick={() => this.setState({ hasError: false, error: null })}>
            Try Again
          </Button>
        </Box>
      );
    }
    return this.props.children;
  }
}

export function ErrorMessage({ title, message, onRetry }: Props) {
  return (
    <Box sx={{ p: 4, textAlign: 'center' }}>
      <ErrorOutlineIcon sx={{ fontSize: 48, color: 'error.main', mb: 2 }} />
      {title && <Typography variant="h6" gutterBottom>{title}</Typography>}
      {message && <Typography color="text.secondary" sx={{ mb: 2 }}>{message}</Typography>}
      {onRetry && (
        <Button variant="contained" onClick={onRetry} sx={{ mt: 1 }}>
          Try Again
        </Button>
      )}
    </Box>
  );
}
