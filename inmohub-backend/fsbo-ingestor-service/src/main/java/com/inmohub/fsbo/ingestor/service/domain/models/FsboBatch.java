package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;

import java.time.LocalDateTime;
import java.util.*;

public class FsboBatch {
    private final UUID id;
    private final LocalDateTime uploadedAt;
    private final List<FsboRecord> records;

    private FsboBatch(UUID id, LocalDateTime uploadedAt, List<FsboRecord> records) {
        this.id = Objects.requireNonNull(id, "El ID del batch es obligatorio.");
        this.uploadedAt = Objects.requireNonNull(uploadedAt, "La fecha de carga es obligatoria.");

        if (records == null || records.isEmpty()) {
            throw new DomainException("Un lote debe contener al menos un registro.");
        }
        this.records = new ArrayList<>(records);
    }

    public static FsboBatch create(UUID batchId, LocalDateTime uploadedAt, List<FsboRecord> records) {
        return new FsboBatch(batchId, uploadedAt, records);
    }

    public List<FsboRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public int totalRecords() {
        return records.size();
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}