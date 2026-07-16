import { useState } from 'react';
import {
  List, ListItem, ListItemAvatar, ListItemText, Typography, Box, IconButton, Divider,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import { useUpdateCommentMutation, useDeleteCommentMutation } from '../../api/commentApi';
import { useAuth } from '../../hooks/useAuth';
import UserAvatar from '../common/Avatar';
import ConfirmDialog from '../common/ConfirmDialog';
import { formatDateTime } from '../../utils/helpers';
import toast from 'react-hot-toast';
import type { Comment } from '../../types';
import { TextField } from '@mui/material';

interface Props {
  comments: Comment[];
  taskId: string;
}

export default function CommentList({ comments, taskId }: Props) {
  const { user } = useAuth();
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editContent, setEditContent] = useState('');
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);
  const [updateComment, { isLoading: updating }] = useUpdateCommentMutation();
  const [deleteComment, { isLoading: deleting }] = useDeleteCommentMutation();

  const startEdit = (comment: Comment) => {
    setEditingId(comment.id);
    setEditContent(comment.content);
  };

  const handleSave = async (comment: Comment) => {
    try {
      await updateComment({ taskId, commentId: comment.id, data: { content: editContent } }).unwrap();
      toast.success('Comment updated');
      setEditingId(null);
    } catch {
      toast.error('Failed to update comment');
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteComment({ taskId, commentId: deleteTarget }).unwrap();
      toast.success('Comment deleted');
      setDeleteTarget(null);
    } catch {
      toast.error('Failed to delete comment');
    }
  };

  return (
    <>
      <List disablePadding>
        {comments.map((comment, idx) => {
          const isAuthor = user?.id === comment.author.id;
          return (
            <Box key={comment.id}>
              {idx > 0 && <Divider />}
              <ListItem
                alignItems="flex-start"
                secondaryAction={
                  isAuthor && editingId !== comment.id && (
                    <Box>
                      <IconButton size="small" onClick={() => startEdit(comment)}><EditIcon fontSize="small" /></IconButton>
                      <IconButton size="small" onClick={() => setDeleteTarget(comment.id)}><DeleteIcon fontSize="small" /></IconButton>
                    </Box>
                  )
                }
              >
                <ListItemAvatar>
                  <UserAvatar firstName={comment.author.firstName} lastName={comment.author.lastName} />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                      <Typography variant="subtitle2">{comment.author.firstName} {comment.author.lastName}</Typography>
                      <Typography variant="caption" color="text.disabled">{formatDateTime(comment.createdAt)}</Typography>
                    </Box>
                  }
                  secondary={
                    editingId === comment.id ? (
                      <Box sx={{ mt: 1 }}>
                        <TextField fullWidth multiline size="small" value={editContent} onChange={(e) => setEditContent(e.target.value)} />
                        <Box sx={{ mt: 1 }}>
                          <IconButton size="small" onClick={() => handleSave(comment)} disabled={updating}><SaveIcon fontSize="small" /></IconButton>
                          <IconButton size="small" onClick={() => setEditingId(null)}><CancelIcon fontSize="small" /></IconButton>
                        </Box>
                      </Box>
                    ) : (
                      <Typography variant="body2" sx={{ mt: 0.5, whiteSpace: 'pre-wrap' }}>{comment.content}</Typography>
                    )
                  }
                />
              </ListItem>
            </Box>
          );
        })}
      </List>
      <ConfirmDialog open={!!deleteTarget} title="Delete Comment" message="Are you sure you want to delete this comment?" confirmLabel="Delete" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)} loading={deleting} danger />
    </>
  );
}
