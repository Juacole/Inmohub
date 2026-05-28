package com.inmohub.property.service.dtos;

import com.inmohub.property.service.models.enums.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la actualizacion parcial (PATCH) de una propiedad inmobiliaria.
 * Todos los campos son opcionales; solo los valores no nulos modifican el inmueble.
 */
@Schema(description = "DTO para la actualización parcial de una propiedad inmobiliaria. Solo los campos proporcionados serán modificados.")
public record PropertyPatchDto(
        @Schema(description = "Título del anuncio", example = "Chalet renovado")
        String title,

        @Schema(description = "Descripción detallada del inmueble", example = "Propiedad completamente reformada con acabados de lujo.")
        String description,

        @Schema(description = "Precio de venta en euros", example = "480000.00")
        BigDecimal price,

        @Schema(description = "Superficie construida en metros cuadrados", example = "250.5")
        Double areaM2,

        @Schema(description = "Dirección física completa del inmueble", example = "Calle Mayor 123, Madrid")
        String address,

        @Schema(description = "Ciudad donde se encuentra el inmueble", example = "Madrid")
        String city,

        @Schema(description = "Estado o provincia geográfica", example = "Comunidad de Madrid")
        String state,

        @Schema(description = "País donde se encuentra el inmueble", example = "España")
        String country,

        @Schema(description = "Estado actual de la propiedad", example = "AVAILABLE")
        PropertyStatus status,

        @Schema(description = "Lista de características adicionales del inmueble")
        List<PropertyFeatureDto> features
) {}
