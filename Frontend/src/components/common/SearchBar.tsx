import { useState, useCallback } from 'react';
import { TextField, InputAdornment } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

interface Props {
  onSearch: (query: string) => void;
  placeholder?: string;
  debounceMs?: number;
}

export default function SearchBar({ onSearch, placeholder = 'Search...', debounceMs = 300 }: Props) {
  const [value, setValue] = useState('');

  const debouncedSearch = useCallback(
    (() => {
      let timer: ReturnType<typeof setTimeout>;
      return (query: string) => {
        clearTimeout(timer);
        timer = setTimeout(() => onSearch(query), debounceMs);
      };
    })(),
    [onSearch, debounceMs]
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setValue(val);
    debouncedSearch(val);
  };

  return (
    <TextField
      fullWidth
      placeholder={placeholder}
      value={value}
      onChange={handleChange}
      size="small"
      InputProps={{
        startAdornment: (
          <InputAdornment position="start">
            <SearchIcon fontSize="small" />
          </InputAdornment>
        ),
      }}
      sx={{ maxWidth: 400 }}
    />
  );
}
