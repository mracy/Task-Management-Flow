import { useCallback, useState } from 'react';

interface UsePaginationOptions {
  initialPage?: number;
  initialSize?: number;
}

export function usePagination({ initialPage = 0, initialSize = 10 }: UsePaginationOptions = {}) {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);

  const goToPage = useCallback((newPage: number) => setPage(newPage), []);
  const nextPage = useCallback(() => setPage((p) => p + 1), []);
  const prevPage = useCallback(() => setPage((p) => Math.max(0, p - 1)), []);
  const resetPagination = useCallback(() => setPage(0), []);

  return { page, size, setPage, setSize, goToPage, nextPage, prevPage, resetPagination };
}
