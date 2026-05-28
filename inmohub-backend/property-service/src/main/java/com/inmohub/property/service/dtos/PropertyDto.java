package com.inmohub.property.service.dtos;

import com.inmohub.property.service.models.enums.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta con la informacion completa de una propiedad inmobiliaria.
 * Incluye metadatos, estado, fotos, caracteristicas y fechas de auditoria.
 */
@Schema(description = "DTO de respuesta con la información completa de una propiedad")
public record PropertyDto(
        @Schema(description = "Identificador único de la propiedad (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        @Schema(description = "Título del anuncio de la propiedad", example = "Chalet de lujo con vistas al mar")
        String title,
        @Schema(description = "Descripción detallada del inmueble")
        String description,
        @Schema(description = "Precio de venta en euros", example = "450000.00")
        BigDecimal price,
        @Schema(description = "Superficie total en metros cuadrados", example = "250.5")
        Double areaM2,
        @Schema(description = "Dirección física completa del inmueble", example = "Calle Mayor 123, Madrid")
        String address,
        @Schema(description = "Ciudad donde se encuentra el inmueble", example = "Madrid")
        String city,
        @Schema(description = "Estado actual de la propiedad: AVAILABLE (disponible), SOLD (vendido), RENTED (alquilado), OFF_MARKET (fuera del mercado)")
        PropertyStatus status,
        @Schema(description = "ID del propietario asociado (UUID)", example = "660e8400-e29b-41d4-a716-446655440000")
        UUID ownerId,
        @Schema(description = "Lista de fotografías del inmueble")
        List<PropertyPhotoDto> photos,
        @Schema(description = "Lista de características adicionales (habitaciones, baños, piscina, etc.)")
        List<PropertyFeatureDto> features,
        @Schema(description = "Fecha y hora de creación del registro", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,
        @Schema(description = "Fecha y hora de última modificación", example = "2024-01-20T14:45:00")
        LocalDateTime updatedAt
) {
}
