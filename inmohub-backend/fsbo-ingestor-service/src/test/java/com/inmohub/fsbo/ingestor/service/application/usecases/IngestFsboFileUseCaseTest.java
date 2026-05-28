package com.inmohub.fsbo.ingestor.service.application.usecases;

import com.inmohub.fsbo.ingestor.service.application.dtos.DeduplicationSummary;
import com.inmohub.fsbo.ingestor.service.application.dtos.FsboResponse;
import com.inmohub.fsbo.ingestor.service.application.services.DeduplicationService;
import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.ICsvParser;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import com.inmohub.fsbo.ingestor.service.domain.ports.IPropertyEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link IngestFsboFileUseCase}.
 * Verifica el flujo completo de ingestion de archivos FSBO:
 * parseo CSV, deduplicacion, persistencia y publicacion de eventos Kafka.
 * Cubre casos de error (archivo null, owner null, CSV invalido, todos duplicados).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IngestFsboFileUseCase")
class IngestFsboFileUseCaseTest {

    @Mock
    private ICsvParser csvParser;

    @Mock
    private DeduplicationService deduplicationService;

    @Mock
    private IFsboRepository repository;

    @Mock
    private ILeadEventPublisher leadPublisher;

    @Mock
    private IPropertyEventPublisher propertyPublisher;

    @InjectMocks
    private IngestFsboFileUseCase useCase;

    private final UUID ownerId = UUID.randomUUID();
    private final OwnerDetails owner = new OwnerDetails(ownerId, "Pepe Montana", "pepe@test.com", "600123456");
    private final InputStream inputStream = new ByteArrayInputStream("test".getBytes());

    @Test
    @DisplayName("Debe procesar un archivo CSV exitosamente")
    void procesarArchivoExitoso() {
        PropertyRecord record = PropertyRecord.create("Titulo", "Desc", new BigDecimal("100"),
                100.0, "Calle Mayor 123", "Madrid", "Madrid", "España", Map.of());
        FsboBatch batch = FsboBatch.create(owner, LocalDateTime.now(), List.of(record));
        DeduplicationSummary summary = new DeduplicationSummary(1, 0);

        when(csvParser.parse(inputStream, owner)).thenReturn(Result.success(batch));
        when(deduplicationService.processPotentiallyDuplicated(batch.getProperties())).thenReturn(summary);

        Result<FsboResponse, String> result = useCase.execute(inputStream, owner);

        assertTrue(result.isSuccess());
        assertTrue(result.getValue().message().contains("Carga exitosa"));
        verify(repository, times(1)).saveBatch(batch);
        verify(leadPublisher, times(1)).publishOwnerAsLeadEvent(batch);
        verify(propertyPublisher, times(1)).publishBulkProperties(batch);
    }

    @Test
    @DisplayName("Debe fallar si el InputStream es null")
    void inputStreamNuloRetornaError() {
        Result<FsboResponse, String> result = useCase.execute(null, owner);

        assertFalse(result.isSuccess());
        assertEquals("El flujo de archivo no puede ser nulo.", result.getErrorValue().get());
    }

    @Test
    @DisplayName("Debe fallar si el OwnerDetails es null")
    void ownerNuloRetornaError() {
        Result<FsboResponse, String> result = useCase.execute(inputStream, null);

        assertFalse(result.isSuccess());
        assertEquals("El ID del propietario es obligatorio.", result.getErrorValue().get());
    }

    @Test
    @DisplayName("Debe fallar si el parser de CSV falla")
    void parseoCsvFallido() {
        when(csvParser.parse(inputStream, owner)).thenReturn(Result.error("Formato CSV invalido"));

        Result<FsboResponse, String> result = useCase.execute(inputStream, owner);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorValue().get().contains("Error leyendo el CSV"));
        verify(repository, never()).saveBatch(any());
    }

    @Test
    @DisplayName("Debe retornar error si todas las propiedades son duplicadas")
    void todasDuplicadasRetornaError() {
        PropertyRecord record = PropertyRecord.create("Titulo", "Desc", new BigDecimal("100"),
                100.0, "Calle Mayor 123", "Madrid", "Madrid", "España", Map.of());
        FsboBatch batch = FsboBatch.create(owner, LocalDateTime.now(), List.of(record));
        DeduplicationSummary summary = new DeduplicationSummary(1, 1);

        when(csvParser.parse(inputStream, owner)).thenReturn(Result.success(batch));
        when(deduplicationService.processPotentiallyDuplicated(batch.getProperties())).thenReturn(summary);

        Result<FsboResponse, String> result = useCase.execute(inputStream, owner);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorValue().get().contains("TODOS los inmuebles estaban duplicados"));
        verify(repository, times(1)).saveBatch(batch);
        verify(leadPublisher, never()).publishOwnerAsLeadEvent(any());
    }

    @Test
    @DisplayName("Debe fallar si no hay propiedades validas despues de deduplicacion")
    void sinPropiedadesValidas() {
        PropertyRecord record = PropertyRecord.create("Titulo", "Desc", new BigDecimal("100"),
                100.0, "Calle", "Madrid", "Madrid", "España", Map.of());
        record.markAsError("Duplicado");
        FsboBatch batch = FsboBatch.create(owner, LocalDateTime.now(), List.of(record));
        DeduplicationSummary summary = new DeduplicationSummary(1, 1);

        when(csvParser.parse(inputStream, owner)).thenReturn(Result.success(batch));
        when(deduplicationService.processPotentiallyDuplicated(batch.getProperties())).thenReturn(summary);

        Result<FsboResponse, String> result = useCase.execute(inputStream, owner);

        assertFalse(result.isSuccess());
        verify(repository, times(1)).saveBatch(batch);
        verify(leadPublisher, never()).publishOwnerAsLeadEvent(any());
    }

    @Test
    @DisplayName("Debe permitir carga parcial con duplicados")
    void cargaParcialConDuplicados() {
        PropertyRecord valido = PropertyRecord.create("Valido", "Desc", new BigDecimal("100"),
                100.0, "Calle Mayor 123", "Madrid", "Madrid", "España", Map.of());
        FsboBatch batch = FsboBatch.create(owner, LocalDateTime.now(), List.of(valido));
        DeduplicationSummary summary = new DeduplicationSummary(2, 1);

        when(csvParser.parse(inputStream, owner)).thenReturn(Result.success(batch));
        when(deduplicationService.processPotentiallyDuplicated(batch.getProperties())).thenReturn(summary);

        Result<FsboResponse, String> result = useCase.execute(inputStream, owner);

        assertTrue(result.isSuccess());
        assertTrue(result.getValue().message().contains("Carga parcial"));
        verify(leadPublisher, times(1)).publishOwnerAsLeadEvent(batch);
    }
}
