package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Modelo de dominio que representa una propiedad inmobiliaria dentro de un archivo FSBO.
 * Incluye validaciones integradas y transicion de estados.
 */
public class PropertyRecord {
    private final UUID id;
    private final String title;
    private final String description;
    private final BigDecimal price;
    private final Double areaM2;
    private final String address;
    private final String city;
    private final String state;
    private final String country;
    private final Map<String, String> features;
    private RecordStatus status;
    private String errorMessage;

    private PropertyRecord(
            UUID id,
            String title,
            String description,
            BigDecimal price,
            Double areaM2,
            String address,
            String city,
            String state,
            String country,
            Map<String, String> features
    ) {
        this(id, title, description, price, areaM2, address, city, state, country, features, RecordStatus.PENDING, null);
    }

    private PropertyRecord(
            UUID id,
            String title,
            String description,
            BigDecimal price,
            Double areaM2,
            String address,
            String city,
            String state,
            String country,
            Map<String, String> features,
            String errorMessage
    ) {
        this(id, title, description, price, areaM2, address, city, state, country, features, RecordStatus.ERROR, errorMessage);
    }

    private PropertyRecord(
            UUID id,
            String title,
            String description,
            BigDecimal price,
            Double areaM2,
            String address,
            String city,
            String state,
            String country,
            Map<String, String> features,
            RecordStatus status,
            String errorMessage
    ) {
        if (status != RecordStatus.ERROR) {
            if (id == null) throw new DomainException("El ID no puede ser nulo.");
            if (title == null || title.isBlank()) throw new DomainException("El título es obligatorio.");
            if (address == null || address.isBlank()) throw new DomainException("La dirección es obligatoria.");
            if (city == null || city.isBlank()) throw new DomainException("La ciudad es obligatoria.");

            if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainException("El precio no puede ser negativo.");
            }
            if (areaM2 != null && areaM2 <= 0) {
                throw new DomainException("El área debe ser mayor a cero.");
            }
        }

        this.id = id;
        this.title = (title != null) ? title : "";
        this.description = (description == null) ? "" : description;
        this.price = price;
        this.areaM2 = areaM2;
        this.address = (address != null) ? address : "";
        this.city = (city != null) ? city : "";
        this.state = state;
        this.country = (country == null) ? "Desconocido" : country;

        this.features = (features != null) ? Map.copyOf(features) : Map.of();
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static PropertyRecord createInvalid(String errorMessage) {
        return new PropertyRecord(
                null, null, null, null, null, null, null, null, null,
                Map.of(), errorMessage
        );
    }

    public static PropertyRecord create(
            String title,
            String description,
            BigDecimal price,
            Double area,
            String address,
            String city,
            String state,
            String country,
            Map<String, String> features
    ) {
        return new PropertyRecord(
                UUID.randomUUID(),
                title,
                description,
                price,
                area,
                address,
                city,
                state,
                country,
                features
        );
    }

    public void markAsError(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Se requiere una razón para marcar el error.");
        }
        this.status = RecordStatus.ERROR;
        this.errorMessage = reason;
    }

    public void markAsProcessed() {
        this.status = RecordStatus.PROCESSED;
        this.errorMessage = null;
    }

    public boolean isValid() {
        return this.status == RecordStatus.PENDING;
    }

    public boolean canBeProcessed() {
        return this.status == RecordStatus.PENDING;
    }

    public Map<String, String> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Double getAreaM2() {
        return areaM2;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}