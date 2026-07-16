package com.taskmanagement.util;

import com.taskmanagement.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
