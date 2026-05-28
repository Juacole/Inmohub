package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link FsboBatch}.
 * Verifica la creacion de lotes con propiedades, filtrado de registros validos/invalidos
 * y que la lista de propiedades devuelta sea inmutable.
 */
@DisplayName("FsboBatch (Modelo de Dominio)")
class FsboBatchTest {

    private final UUID ownerId = UUID.randomUUID();
    private final OwnerDetails owner = new OwnerDetails(ownerId, "Pepe Montana", "pepe@test.com", "600123456");
    private final LocalDateTime uploadedAt = LocalDateTime.now();

    private PropertyRecord crearRegistroValido(String titulo) {
        return PropertyRecord.create(titulo, "Desc", new BigDecimal("100"),
                100.0, "Calle " + titulo, "Madrid", "Madrid", "España", Map.of());
    }

    @Test
    @DisplayName("Debe crear un FsboBatch con propiedades validas")
    void crearBatchExitoso() {
        List<PropertyRecord> records = List.of(
                crearRegistroValido("Chalet 1"),
                crearRegistroValido("Chalet 2")
        );

        FsboBatch batch = FsboBatch.create(owner, uploadedAt, records);

        assertEquals(owner, batch.getOwnerDetails());
        assertEquals(uploadedAt, batch.getUploadedAt());
        assertEquals(2, batch.totalRecords());
        assertEquals(2, batch.getValidProperties().size());
        assertEquals(2, batch.getProperties().size());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si la lista de propiedades es null")
    void listaNullLanzaExcepcion() {
        assertThrows(DomainException.class, () -> {
            FsboBatch.create(owner, uploadedAt, null);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepcion si la lista de propiedades esta vacia")
    void listaVaciaLanzaExcepcion() {
        assertThrows(DomainException.class, () -> {
            FsboBatch.create(owner, uploadedAt, Collections.emptyList());
        });
    }

    @Test
    @DisplayName("Debe filtrar solo propiedades validas en getValidProperties")
    void filtrarPropiedadesValidas() {
        PropertyRecord valida = crearRegistroValido("Valida");
        PropertyRecord invalida = PropertyRecord.createInvalid("Error de formato");

        FsboBatch batch = FsboBatch.create(owner, uploadedAt, List.of(valida, invalida));

        assertEquals(2, batch.totalRecords());
        assertEquals(1, batch.getValidProperties().size());
        assertEquals(2, batch.getProperties().size());
    }

    @Test
    @DisplayName("getProperties debe retornar lista inmutable")
    void propiedadesInmutables() {
        FsboBatch batch = FsboBatch.create(owner, uploadedAt,
                List.of(crearRegistroValido("Chalet")));

        List<PropertyRecord> properties = batch.getProperties();

        assertThrows(UnsupportedOperationException.class, () -> properties.add(crearRegistroValido("Nuevo")));
    }
}
