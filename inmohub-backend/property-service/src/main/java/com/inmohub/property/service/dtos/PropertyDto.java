package com.inmohub.property.service.dtos;

import com.inmohub.property.service.models.enums.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO de respuesta con la información completa de una propiedad")
public record PropertyDto(
        @Schema(description = "Identificador único de la propiedad")
        UUID id,
        String title,
        String description,
        BigDecimal price,
        Double areaM2,
        String address,

        @Schema(description = "Estado actual de la propiedad")
        PropertyStatus status,

        @Schema(description = "ID del propietario asociado")
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
