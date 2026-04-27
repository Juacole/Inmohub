package com.inmohub.fsbo.ingestor.service.domain.services;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeduplicationService {

    private final IFsboRepository repository;

    public DeduplicationService(IFsboRepository repository) {
        this.repository = repository;
    }

    public void deduplicate(List<FsboRecord> records) {
        Set<String> seenEmails = new HashSet<>();

        for (FsboRecord record : records) {
            if (seenEmails.contains(record.getOwnerEmail().value())) {
                record.markAsDuplicated("Duplicado en el mismo archivo CSV.");
            } else {
                seenEmails.add(record.getOwnerEmail().value());

                if (repository.existsByEmailOrPhone(record.getOwnerEmail().value(), record.getOwnerPhone())) {
                    record.markAsDuplicated("Email o teléfono ya existe en el sistema.");
                }
            }
        }
    }
}