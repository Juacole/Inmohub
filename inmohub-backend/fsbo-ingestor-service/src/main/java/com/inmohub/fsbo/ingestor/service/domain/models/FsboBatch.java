package com.inmohub.fsbo.ingestor.service.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FsboBatch {
    private final UUID id;
    private final LocalDateTime uploadedAt;
    private final List<FsboRecord> records;

    private FsboBatch(UUID batchId, LocalDateTime uploadedAt, List<FsboRecord> records) {
        this.id = batchId;
        this.uploadedAt = uploadedAt;
        this.records = records != null ? records : new ArrayList<>();
    }

    public static FsboBatch create(UUID batchId, LocalDateTime uploadedAt, List<FsboRecord> records) {
        return new FsboBatch(batchId, uploadedAt, records);
    }
    public List<FsboRecord> getRecords() {
        return records;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}