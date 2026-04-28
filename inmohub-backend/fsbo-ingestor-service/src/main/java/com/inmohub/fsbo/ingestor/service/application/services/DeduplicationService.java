package com.inmohub.fsbo.ingestor.service.application.services;

import com.inmohub.fsbo.ingestor.service.application.dtos.DeduplicationSummary;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeduplicationService {

    private final IFsboRepository repository;

    public DeduplicationService(IFsboRepository repository) {
        this.repository = repository;
    }

    public DeduplicationSummary processPotentiallyDuplicated(List<PropertyRecord> properties) {
        if (properties == null || properties.isEmpty()) {
            return new DeduplicationSummary(0, 0);
        }

        int duplicatesFound = 0;
        Set<String> propertyKeysInCurrentBatch = new HashSet<>();

        for (PropertyRecord property : properties) {
            if (!property.canBeProcessed()) continue;

            String propertyKey = generatePropertyKey(property);

            if (isDuplicatedInCurrentBatch(propertyKey, propertyKeysInCurrentBatch) || existsInSystem(property)) {
                property.markAsError("Inmueble duplicado en archivo o sistema.");
                duplicatesFound++;
                continue;
            }

            propertyKeysInCurrentBatch.add(propertyKey);
        }

        return new DeduplicationSummary(properties.size(), duplicatesFound);
    }

    private String generatePropertyKey(PropertyRecord property) {
        return (property.getAddress() + "|" + property.getCity())
                .toLowerCase()
                .replaceAll("\\s+", "");
    }

    private boolean isDuplicatedInCurrentBatch(String key, Set<String> seenKeys) {
        return !seenKeys.add(key);
    }

    private boolean existsInSystem(PropertyRecord record) {
        return repository.existsByAddressAndCity(
                record.getAddress(),
                record.getCity()
        );
    }
}
