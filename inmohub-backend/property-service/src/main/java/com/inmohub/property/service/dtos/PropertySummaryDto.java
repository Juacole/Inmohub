package com.inmohub.property.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO ligero para el catálogo de propiedades optimizado para el frontend")
public final class PropertySummaryDto {
    @Schema(description = "Identificador único de la propiedad (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Título del anuncio de la propiedad", example = "Chalet de lujo con vistas al mar")
    private String title;
    @Schema(description = "Precio de venta en euros", example = "450000.00")
    private BigDecimal price;
    @Schema(description = "Ciudad donde se encuentra el inmueble", example = "Madrid")
    private String city;
    @Schema(description = "Estado actual de la propiedad: AVAILABLE, SOLD, RENTED, OFF_MARKET")
    private String status;
    @Schema(description = "URL de la fotografía principal del inmueble", example = "https://firebasestorage.googleapis.com/...")
    private String primaryPhotoUrl;

    public PropertySummaryDto() {}

    public PropertySummaryDto(UUID id, String title, BigDecimal price, String city, String status, String primaryPhotoUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.city = city;
        this.status = status;
        this.primaryPhotoUrl = primaryPhotoUrl;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public String getCity() { return city; }
    public String getStatus() { return status; }
    public String getPrimaryPhotoUrl() { return primaryPhotoUrl; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCity(String city) { this.city = city; }
    public void setStatus(String status) { this.status = status; }
    public void setPrimaryPhotoUrl(String primaryPhotoUrl) { this.primaryPhotoUrl = primaryPhotoUrl; }
}