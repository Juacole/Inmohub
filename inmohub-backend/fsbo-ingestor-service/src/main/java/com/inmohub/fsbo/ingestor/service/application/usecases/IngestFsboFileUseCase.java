package com.inmohub.fsbo.ingestor.service.application.usecases;

import com.inmohub.fsbo.ingestor.service.application.dtos.DeduplicationSummary;
import com.inmohub.fsbo.ingestor.service.application.dtos.FsboResponse;
import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.ICsvParser;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import com.inmohub.fsbo.ingestor.service.domain.ports.IPropertyEventPublisher;
import com.inmohub.fsbo.ingestor.service.application.services.DeduplicationService;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class IngestFsboFileUseCase {

    private final ICsvParser csvParser;
    private final DeduplicationService deduplicationService;
    private final IFsboRepository repository;
    private final ILeadEventPublisher leadPublisher;
    private final IPropertyEventPublisher propertyPublisher;

    public IngestFsboFileUseCase(
            ICsvParser csvParser,
            DeduplicationService deduplicationService,
            IFsboRepository repository,
            ILeadEventPublisher eventPublisher,
            IPropertyEventPublisher propertyPublisher
    ) {
        this.csvParser = csvParser;
        this.deduplicationService = deduplicationService;
        this.repository = repository;
        this.leadPublisher = eventPublisher;
        this.propertyPublisher = propertyPublisher;
    }

    public Result<FsboResponse, String> execute(InputStream fileStream, OwnerDetails ownerDetails) {
        if (fileStream == null) {
            return Result.error("El flujo de archivo no puede ser nulo.");
        }
        if (ownerDetails == null) {
            return Result.error("El ID del propietario es obligatorio.");
        }

        Result<FsboBatch, String> parseResult = csvParser.parse(fileStream, ownerDetails);
        if (!parseResult.isSuccess()) {
            return Result.error("Error leyendo el CSV: " + parseResult.getErrorValue());
        }

        FsboBatch batch = parseResult.getValue();

        DeduplicationSummary summary = deduplicationService.processPotentiallyDuplicated(batch.getProperties());

        if (summary.isAllDuplicated()) {
            repository.saveBatch(batch);
            return Result.error("Se procesó el archivo, pero TODOS los inmuebles estaban duplicados.");
        }

        repository.saveBatch(batch);

        List<PropertyRecord> validProperties = batch.getValidProperties();

        if (validProperties.isEmpty()) {
            return Result.error("El archivo fue procesado, pero no contiene propiedades válidas o todas resultaron estar duplicadas.");
        }

        leadPublisher.publishOwnerAsLeadEvent(batch);
        propertyPublisher.publishBulkProperties(batch);

        validProperties.forEach(PropertyRecord::markAsProcessed);

        if (summary.hasDuplicates()) {
            return Result.success(
                    new FsboResponse("Carga parcial por duplicación: " + summary.duplicatesFound())
            );
        }

        return Result.success(
                new FsboResponse("Carga exitosa: " + validProperties.size() + " inmuebles procesados.")
        );
    }
}