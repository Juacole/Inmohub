package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import com.inmohub.fsbo.ingestor.service.domain.valueobjects.Email;

import java.util.UUID;

public class FsboRecord {
    private final UUID id;
    private final String ownerName;
    private final Email ownerEmail;
    private final String ownerPhone;
    private final String propertyTitle;
    private RecordStatus status;
    private String errorMessage;

    private FsboRecord(
            UUID id,
            String ownerName,
            Email ownerEmail,
            String ownerPhone,
            String propertyTitle,
            RecordStatus status
    ) {
        this.id = id;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.ownerPhone = ownerPhone;
        this.propertyTitle = propertyTitle;
        this.status = status;
    }

    public static FsboRecord create(String name, Email email, String phone, String title) {
        return new FsboRecord(UUID.randomUUID(), name, email, phone, title, RecordStatus.PENDING);
    }

    public void markAsDuplicated(String reason) {
        this.status = RecordStatus.DUPLICATED;
        this.errorMessage = reason;
    }

    public void markAsProcessed() {
        this.status = RecordStatus.PROCESSED;
    }

    public UUID getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Email getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}