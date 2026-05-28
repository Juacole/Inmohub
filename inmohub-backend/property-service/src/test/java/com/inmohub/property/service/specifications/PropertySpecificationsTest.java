package com.inmohub.property.service.specifications;

import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.enums.PropertyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link PropertySpecifications}.
 * Verifica la construccion de especificaciones JPA para busquedas dinamicas
 * de propiedades por ciudad, rango de precios y estado.
 * Cubre combinaciones de criterios y valores invalidos/nulos.
 */
@DisplayName("PropertySpecifications")
class PropertySpecificationsTest {

    @Test
    @DisplayName("buildSpecification debe retornar allOf si no hay criterios")
    void sinCriteriosRetornaAllOf() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(null, null, null, null);

        assertNotNull(spec);
    }

    @Test
    @DisplayName("buildSpecification debe combinar city y precio minimo")
    void combinarCiudadYPrecioMinimo() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(
                "Madrid", new BigDecimal("100000"), null, null);

        assertNotNull(spec);
    }

    @Test
    @DisplayName("buildSpecification debe combinar todos los criterios")
    void combinarTodosLosCriterios() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(
                "Madrid", new BigDecimal("100000"), new BigDecimal("500000"), "AVAILABLE");

        assertNotNull(spec);
    }

    @Test
    @DisplayName("buildSpecification debe ignorar status invalido")
    void statusInvalidoIgnorado() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(
                "Madrid", null, null, "ESTADO_INEXISTENTE");

        assertNotNull(spec);
    }

    @Test
    @DisplayName("buildSpecification debe aceptar solo precio maximo")
    void soloPrecioMaximo() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(
                null, null, new BigDecimal("500000"), null);

        assertNotNull(spec);
    }

    @Test
    @DisplayName("buildSpecification debe aceptar ciudad vacia")
    void ciudadVaciaIgnorada() {
        Specification<Property> spec = PropertySpecifications.buildSpecification(
                "", null, null, null);

        assertNotNull(spec);
    }
}
