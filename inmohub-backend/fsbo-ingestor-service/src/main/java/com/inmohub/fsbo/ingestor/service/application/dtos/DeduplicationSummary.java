package com.inmohub.fsbo.ingestor.service.application.dtos;


/**
 * DTO con el resumen de deduplicacion: total de registros evaluados y duplicados encontrados.
 */
public record DeduplicationSummary(
        int totalEvaluated,
        int duplicatesFound
) {
    public boolean hasDuplicates() {
        return duplicatesFound > 0;
    }
    public boolean isAllDuplicated() {
        return totalEvaluated == duplicatesFound;
    }
}