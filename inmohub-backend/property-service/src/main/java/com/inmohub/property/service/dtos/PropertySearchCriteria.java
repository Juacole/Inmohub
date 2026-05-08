package com.inmohub.property.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Criterios de búsqueda dinámica para filtrar propiedades")
public record PropertySearchCriteria(
        @Schema(description = "Ciudad donde se encuentra el inmueble", example = "Madrid")
        String city,

        @Schema(description = "Precio mínimo de la propiedad", example = "100000.00")
        BigDecimal minPrice,

        @Schema(description = "Precio máximo de la propiedad", example = "500000.00")
        BigDecimal maxPrice,

        @Schema(description = "Estado de la propiedad: AVAILABLE, SOLD, RENTED, OFF_MARKET", example = "AVAILABLE")
        String status
) {
}
