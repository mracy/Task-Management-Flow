import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { TextField, Button, Box } from '@mui/material';
import { commentSchema, CommentFormData } from '../../utils/validators';
import { useCreateCommentMutation } from '../../api/commentApi';
import toast from 'react-hot-toast';

interface Props {
  taskId: string;
}

export default function CommentForm({ taskId }: Props) {
  const [createComment, { isLoading }] = useCreateCommentMutation();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
  });

  const onSubmit = async (data: CommentFormData) => {
    try {
      await createComment({ taskId, data }).unwrap();
      toast.success('Comment added');
      reset();
    } catch {
      toast.error('Failed to add comment');
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 2 }}>
      <TextField
        fullWidth multiline rows={2} label="Add a comment..."
        {...register('content')} error={!!errors.content} helperText={errors.content?.message}
      />
      <Button type="submit" variant="contained" size="small" disabled={isLoading} sx={{ mt: 1 }}>
        {isLoading ? 'Posting...' : 'Post Comment'}
      </Button>
    </Box>
  );
}
