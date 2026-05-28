package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link PropertyRecord}.
 * Cubre la creacion de registros validos e invalidos, validaciones de negocio
 * (precio negativo, area <= 0), transiciones de estado (PENDING -> ERROR/PROCESSED)
 * y valores por defecto (pais desconocido, descripcion vacia).
 */
@DisplayName("PropertyRecord (Modelo de Dominio)")
class PropertyRecordTest {

    private final Map<String, String> features = Map.of("Habitaciones", "3", "Baños", "2");

    @Nested
    @DisplayName("Creacion de PropertyRecord")
    class Creacion {

        @Test
        @DisplayName("Debe crear un PropertyRecord valido con estado PENDING")
        void crearRegistroValido() {
            PropertyRecord record = PropertyRecord.create(
                    "Chalet en Madrid", "Descripcion amplia", new BigDecimal("450000"),
                    250.5, "Calle Mayor 123", "Madrid", "Comunidad de Madrid", "España", features);

            assertNotNull(record.getId());
            assertEquals("Chalet en Madrid", record.getTitle());
            assertEquals(RecordStatus.PENDING, record.getStatus());
            assertTrue(record.isValid());
            assertEquals(2, record.getFeatures().size());
        }

        @Test
        @DisplayName("Debe lanzar excepcion si el titulo es null")
        void tituloNuloLanzaExcepcion() {
            assertThrows(DomainException.class, () -> {
                PropertyRecord.create(null, "Desc", new BigDecimal("100"),
                        100.0, "Calle", "Madrid", "Madrid", "España", Map.of());
            });
        }

        @Test
        @DisplayName("Debe lanzar excepcion si el precio es negativo")
        void precioNegativoLanzaExcepcion() {
            assertThrows(DomainException.class, () -> {
                PropertyRecord.create("Titulo", "Desc", new BigDecimal("-1"),
                        100.0, "Calle", "Madrid", "Madrid", "España", Map.of());
            });
        }

        @Test
        @DisplayName("Debe lanzar excepcion si el area es menor o igual a cero")
        void areaCeroLanzaExcepcion() {
            assertThrows(DomainException.class, () -> {
                PropertyRecord.create("Titulo", "Desc", new BigDecimal("100"),
                        0.0, "Calle", "Madrid", "Madrid", "España", Map.of());
            });
        }

        @Test
        @DisplayName("Debe permitir features vacias")
        void crearSinFeatures() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            assertTrue(record.getFeatures().isEmpty());
        }

        @Test
        @DisplayName("Debe crear un registro invalido con mensaje de error")
        void crearRegistroInvalido() {
            PropertyRecord record = PropertyRecord.createInvalid("Fila con formato incorrecto");

            assertEquals(RecordStatus.ERROR, record.getStatus());
            assertEquals("Fila con formato incorrecto", record.getErrorMessage());
            assertFalse(record.isValid());
        }
    }

    @Nested
    @DisplayName("Cambio de estado")
    class CambioEstado {

        @Test
        @DisplayName("markAsError debe cambiar estado a ERROR y guardar el mensaje")
        void marcarComoError() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            record.markAsError("Inmueble duplicado en archivo o sistema.");

            assertEquals(RecordStatus.ERROR, record.getStatus());
            assertEquals("Inmueble duplicado en archivo o sistema.", record.getErrorMessage());
            assertFalse(record.isValid());
        }

        @Test
        @DisplayName("markAsError debe lanzar excepcion si la razon es null")
        void marcaErrorSinRazonLanzaExcepcion() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            assertThrows(DomainException.class, () -> record.markAsError(null));
        }

        @Test
        @DisplayName("markAsProcessed debe cambiar estado a PROCESSED")
        void marcarComoProcesado() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            record.markAsProcessed();

            assertEquals(RecordStatus.PROCESSED, record.getStatus());
            assertNull(record.getErrorMessage());
        }

        @Test
        @DisplayName("canBeProcessed debe ser true solo para PENDING")
        void soloPendientesPuedenSerProcesados() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            assertTrue(record.canBeProcessed());

            record.markAsProcessed();
            assertFalse(record.canBeProcessed());

            PropertyRecord invalidRecord = PropertyRecord.createInvalid("Error");
            assertFalse(invalidRecord.canBeProcessed());
        }
    }

    @Nested
    @DisplayName("Valores por defecto")
    class ValoresPorDefecto {

        @Test
        @DisplayName("Debe asignar Espana como pais por defecto si es null")
        void paisPorDefecto() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", "Desc", new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", null, Map.of());

            assertEquals("Desconocido", record.getCountry());
        }

        @Test
        @DisplayName("Debe asignar cadena vacia a descripcion si es null")
        void descripcionNula() {
            PropertyRecord record = PropertyRecord.create(
                    "Titulo", null, new BigDecimal("100"),
                    100.0, "Calle", "Madrid", "Madrid", "España", Map.of());

            assertEquals("", record.getDescription());
        }
    }
}
