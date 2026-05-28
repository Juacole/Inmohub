package com.inmohub.fsbo.ingestor.service.application.services;

import com.inmohub.fsbo.ingestor.service.application.dtos.DeduplicationSummary;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link DeduplicationService}.
 * Verifica la deteccion de duplicados dentro del mismo lote (misma direccion+ciudad)
 * y contra el sistema (consulta al repositorio). Cubre listas null/vacias,
 * duplicados totales y parciales.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeduplicationService")
class DeduplicationServiceTest {

    @Mock
    private IFsboRepository repository;

    @InjectMocks
    private DeduplicationService deduplicationService;

    private PropertyRecord crearRegistro(String direccion, String ciudad) {
        return PropertyRecord.create("Titulo", "Desc", new BigDecimal("100"),
                100.0, direccion, ciudad, "Madrid", "España", Map.of());
    }

    @Test
    @DisplayName("Debe retornar resumen vacio si la lista es null")
    void listaNullRetornaVacio() {
        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(null);

        assertEquals(0, summary.totalEvaluated());
        assertEquals(0, summary.duplicatesFound());
        assertFalse(summary.hasDuplicates());
    }

    @Test
    @DisplayName("Debe retornar resumen vacio si la lista esta vacia")
    void listaVaciaRetornaVacio() {
        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(List.of());

        assertEquals(0, summary.totalEvaluated());
        assertEquals(0, summary.duplicatesFound());
    }

    @Test
    @DisplayName("Debe detectar duplicados dentro del mismo lote")
    void detectarDuplicadosEnLote() {
        PropertyRecord record1 = crearRegistro("Calle Mayor 123", "Madrid");
        PropertyRecord record2 = crearRegistro("Calle Mayor 123", "Madrid");
        PropertyRecord record3 = crearRegistro("Calle Nueva 456", "Barcelona");

        when(repository.existsByAddressAndCity(anyString(), anyString())).thenReturn(false);

        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(
                List.of(record1, record2, record3));

        assertEquals(3, summary.totalEvaluated());
        assertEquals(1, summary.duplicatesFound());
        assertTrue(summary.hasDuplicates());
        assertFalse(summary.isAllDuplicated());

        assertEquals(RecordStatus.ERROR, record2.getStatus());

        assertTrue(record3.isValid());
    }

    @Test
    @DisplayName("Debe detectar duplicados contra el sistema")
    void detectarDuplicadosEnSistema() {
        PropertyRecord record = crearRegistro("Calle Mayor 123", "Madrid");
        PropertyRecord recordNuevo = crearRegistro("Calle Nueva 456", "Barcelona");

        when(repository.existsByAddressAndCity("Calle Mayor 123", "Madrid")).thenReturn(true);
        when(repository.existsByAddressAndCity("Calle Nueva 456", "Barcelona")).thenReturn(false);

        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(
                List.of(record, recordNuevo));

        assertEquals(2, summary.totalEvaluated());
        assertEquals(1, summary.duplicatesFound());
        assertEquals(RecordStatus.ERROR, record.getStatus());
        assertTrue(recordNuevo.isValid());
    }

    @Test
    @DisplayName("Debe marcar todos como duplicados si coinciden")
    void todosDuplicados() {
        PropertyRecord record = crearRegistro("Calle Mayor 123", "Madrid");

        when(repository.existsByAddressAndCity("Calle Mayor 123", "Madrid")).thenReturn(true);

        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(List.of(record));

        assertTrue(summary.isAllDuplicated());
    }

    @Test
    @DisplayName("No debe marcar duplicados propiedades ya en estado ERROR")
    void ignorarRegistrosYaEnError() {
        PropertyRecord recordError = crearRegistro("Calle 1", "Madrid");
        recordError.markAsError("Ya era invalido");
        PropertyRecord recordValido = crearRegistro("Calle 2", "Barcelona");

        when(repository.existsByAddressAndCity(anyString(), anyString())).thenReturn(false);

        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(
                List.of(recordError, recordValido));

        assertEquals(2, summary.totalEvaluated());
        assertEquals(0, summary.duplicatesFound());
    }
}
