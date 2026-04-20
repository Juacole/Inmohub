package com.inmohub.property.service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para la creación de una nueva propiedad inmobiliaria")
public record PropertyCreateDto(
        @Schema(description = "Título del anuncio", example = "Chalet de lujo con vistas al mar")
        @NotBlank(message = "El título es obligatorio")
        String title,

        @Schema(description = "Descripción detallada del inmueble", example = "Magnífica propiedad de 3 plantas con piscina...")
        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @Schema(description = "Precio de venta en euros", example = "450000.00")
        @NotNull @Positive
        BigDecimal price,

        @Schema(description = "Superficie en metros cuadrados", example = "250.5")
        @NotNull @Positive
        Double areaM2,

        @Schema(description = "Dirección física del inmueble", example = "Calle Mayor 123, Madrid")
        @NotBlank
        String address,

        @NotBlank String city,

        @NotBlank String state,

        @NotBlank String country,

        List<PropertyFeatureDto> features
) {
}
