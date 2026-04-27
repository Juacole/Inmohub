package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import com.inmohub.fsbo.ingestor.service.domain.valueobjects.Email;

import java.util.Objects;
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
        this.id = Objects.requireNonNull(id, "El ID del registro es obligatorio.");
        this.ownerEmail = Objects.requireNonNull(ownerEmail, "El email del dueño es obligatorio.");
        this.status = Objects.requireNonNull(status, "El estado inicial es obligatorio.");

        if (ownerName == null || ownerName.isBlank())
            throw new DomainException("El nombre del dueño no puede estar vacío.");

        if (propertyTitle == null || propertyTitle.isBlank())
            throw new DomainException("El título de la propiedad es obligatorio.");

        this.ownerName = ownerName;
        this.ownerPhone = (ownerPhone == null) ? "" : ownerPhone;
        this.propertyTitle = propertyTitle;
    }

    public static FsboRecord create(String name, Email email, String phone, String title) {
        return new FsboRecord(UUID.randomUUID(), name, email, phone, title, RecordStatus.PENDING);
    }

    public void markAsDuplicated(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Se requiere un motivo para marcar como duplicado.");
        }
        this.status = RecordStatus.DUPLICATED;
        this.errorMessage = reason;
    }

    public void markAsProcessed() {
        this.status = RecordStatus.PROCESSED;
        this.errorMessage = null;
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