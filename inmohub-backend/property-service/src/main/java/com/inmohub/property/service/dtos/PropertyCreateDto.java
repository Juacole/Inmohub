package com.inmohub.property.service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la creacion de una nueva propiedad inmobiliaria.
 * Agrupa los datos basicos del inmueble y sus caracteristicas opcionales enviados por el cliente.
 */
@Schema(description = "DTO para la creación de una nueva propiedad inmobiliaria")
public record PropertyCreateDto(
        @Schema(
                description = "Título del anuncio visible para los compradores/inquilinos. " +
                        "Debe ser descriptivo y conciso (máximo 100 caracteres).",
                example = "Chalet de lujo con vistas al mar",
                maxLength = 100
        )
        @NotBlank(message = "El título es obligatorio")
        String title,

        @Schema(
                description = "Descripción detallada del inmueble. " +
                        "Incluye información sobre distribución, acabados, orientación, etc.",
                example = "Magnífica propiedad de 3 plantas con piscina, jardín de 500m2, garaje para 2 coches. Excelente orientación sur."
        )
        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @Schema(
                description = "Precio de venta en euros. Debe ser un valor positivo mayor a 0.",
                example = "450000.00"
        )
        @NotNull @Positive
        BigDecimal price,

        @Schema(
                description = "Superficie construida en metros cuadrados. No puede ser negativo.",
                example = "250.5"
        )
        @NotNull @Positive
        Double areaM2,

        @Schema(
                description = "Dirección física completa del inmueble (calle, número, piso, puerta).",
                example = "Calle Mayor 123, Bajo B, Madrid"
        )
        @NotBlank
        String address,

        @Schema(description = "Ciudad donde se encuentra el inmueble", example = "Madrid")
        @NotBlank String city,

        @Schema(description = "Estado o provincia geográfica", example = "Comunidad de Madrid")
        @NotBlank String state,

        @Schema(description = "País donde se encuentra el inmueble", example = "España")
        @NotBlank String country,

        @Schema(
                description = "Lista de características adicionales del inmueble. " +
                        "Ejemplos: habitaciones (3-4), baños (2), piscina (sí/no), jardín, garaje, trastero, calefacción, aire acondicionado.",
                example = "[{\"featureName\":\"Habitaciones\",\"featureValue\":\"4\"},{\"featureName\":\"Baños\",\"featureValue\":\"3\"},{\"featureName\":\"Piscina\",\"featureValue\":\"Sí\"}]"
        )
        List<PropertyFeatureDto> features
) {
}
