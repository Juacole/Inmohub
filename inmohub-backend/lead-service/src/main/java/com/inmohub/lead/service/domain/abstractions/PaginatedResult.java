package com.inmohub.lead.service.domain.abstractions;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public PaginatedResult {
        if (page < 0) throw new DomainException("La página no puede ser negativa.");
        if (size < 1) throw new DomainException("El tamaño debe ser mayor a 0.");
    }

    public static <T> PaginatedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int calculatedTotalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean isLast = page >= calculatedTotalPages - 1 && calculatedTotalPages > 0;
        return new PaginatedResult<>(content, page, size, totalElements, calculatedTotalPages, isLast);
    }
}