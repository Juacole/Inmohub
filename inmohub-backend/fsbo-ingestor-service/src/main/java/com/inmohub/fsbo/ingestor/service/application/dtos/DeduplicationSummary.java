package com.inmohub.fsbo.ingestor.service.application.dtos;


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