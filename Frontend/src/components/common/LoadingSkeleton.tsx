import { Box, Skeleton, Card, CardContent, Grid } from '@mui/material';

export function LoadingSkeleton({ rows = 3 }: { rows?: number }) {
  return (
    <Box>
      {Array.from({ length: rows }).map((_, i) => (
        <Skeleton key={i} variant="rounded" height={60} sx={{ mb: 1 }} />
      ))}
    </Box>
  );
}

export function CardSkeleton() {
  return (
    <Card>
      <CardContent>
        <Skeleton variant="text" width="60%" height={32} />
        <Skeleton variant="text" width="100%" height={20} />
        <Skeleton variant="text" width="80%" height={20} />
        <Skeleton variant="text" width="40%" height={20} sx={{ mt: 2 }} />
      </CardContent>
    </Card>
  );
}

export function StatsSkeleton() {
  return (
    <Grid container spacing={3}>
      {Array.from({ length: 4 }).map((_, i) => (
        <Grid item xs={12} sm={6} md={3} key={i}>
          <Card>
            <CardContent>
              <Skeleton variant="text" width="50%" height={40} />
              <Skeleton variant="text" width="70%" height={24} />
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}
