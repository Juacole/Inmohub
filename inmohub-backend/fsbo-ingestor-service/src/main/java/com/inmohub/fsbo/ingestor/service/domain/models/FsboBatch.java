package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Agregado que representa un lote de propiedades cargado por un propietario.
 * Contiene los detalles del propietario y la lista de registros de propiedad.
 */
public class FsboBatch {
    private final OwnerDetails ownerDetails;
    private final LocalDateTime uploadedAt;
    private final List<PropertyRecord> properties;

    private FsboBatch(OwnerDetails ownerDetails, LocalDateTime uploadedAt, List<PropertyRecord> properties) {
        this.ownerDetails = Objects.requireNonNull(ownerDetails, "El ID del batch es obligatorio.");
        this.uploadedAt = Objects.requireNonNull(uploadedAt, "La fecha de carga es obligatoria.");

        if (properties == null || properties.isEmpty()) {
            throw new DomainException("Un lote debe contener al menos un registro.");
        }
        this.properties = new ArrayList<>(properties);
    }

    public static FsboBatch create(OwnerDetails ownerDetails, LocalDateTime uploadedAt, List<PropertyRecord> records) {
        return new FsboBatch(ownerDetails, uploadedAt, records);
    }

    public List<PropertyRecord> getValidProperties() {
        return properties.stream().filter(PropertyRecord::isValid).toList();
    }

    public List<PropertyRecord> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public int totalRecords() {
        return properties.size();
    }

    public OwnerDetails getOwnerDetails() {
        return ownerDetails;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}