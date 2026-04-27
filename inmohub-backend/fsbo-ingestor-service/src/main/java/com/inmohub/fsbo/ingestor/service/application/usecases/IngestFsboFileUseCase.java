package com.inmohub.fsbo.ingestor.service.application.usecases;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboRecord;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import com.inmohub.fsbo.ingestor.service.domain.ports.ICsvParser;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import com.inmohub.fsbo.ingestor.service.domain.services.DeduplicationService;

import java.io.InputStream;

public class IngestFsboFileUseCase {

    private final ICsvParser csvParser;
    private final DeduplicationService deduplicationService;
    private final IFsboRepository repository;
    private final ILeadEventPublisher eventPublisher;

    public IngestFsboFileUseCase(
            ICsvParser csvParser,
            DeduplicationService deduplicationService,
            IFsboRepository repository,
            ILeadEventPublisher eventPublisher
    ) {
        this.csvParser = csvParser;
        this.deduplicationService = deduplicationService;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public Result<Integer, String> execute(InputStream fileStream) {
        Result<FsboBatch, String> parseResult = csvParser.parse(fileStream);
        if (!parseResult.isSuccess()) {
            return Result.error("Error al procesar el archivo: " + parseResult.getErrorValue());
        }

        FsboBatch batch = parseResult.getValue();

        deduplicationService.deduplicate(batch.getRecords());

        int leadsCreated = 0;
        for (FsboRecord record : batch.getRecords()) {
            if (record.getStatus() == RecordStatus.PENDING) {
                record.markAsProcessed();
                eventPublisher.publishLeadCreatedEvent(record);
                leadsCreated++;
            }
        }

        repository.saveBatch(batch);

        return Result.success(leadsCreated);
    }
}