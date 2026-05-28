package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.mappers;

import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities.FsboDedupCheckJpaEntity;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities.FsboErrorJpaEntity;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities.FsboSubmissionJpaEntity;
import org.mapstruct.*;

/**
 * Mapper MapStruct que convierte registros de dominio {@link PropertyRecord} y {@link OwnerDetails}
 * en entidades JPA {@link FsboSubmissionJpaEntity}, incluyendo el manejo de errores y duplicados.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FsboMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "propertyTitle", source = "record.title")
    @Mapping(target = "ownerId", source = "owner.ownerId")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "ownerPhone", source = "owner.phone")
    @Mapping(target = "status", expression = "java(record.getStatus().name())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "dedupChecks", ignore = true)
    @Mapping(target = "errors", ignore = true)
    FsboSubmissionJpaEntity toEntity(PropertyRecord record, OwnerDetails owner);

    @AfterMapping
    default void handleStatusDetails(PropertyRecord record, @MappingTarget FsboSubmissionJpaEntity entity) {
        if (record.getStatus().name().equals("ERROR")) {
            String errorMsg = record.getErrorMessage();

            // Error por duplicidad
            if (errorMsg != null && (errorMsg.contains("duplicado") || errorMsg.contains("registrado"))) {
                FsboDedupCheckJpaEntity dedup = new FsboDedupCheckJpaEntity();
                dedup.setReason(errorMsg);
                entity.addDedupCheck(dedup);
            } else { // error por validación o formato
                FsboErrorJpaEntity error = new FsboErrorJpaEntity();
                error.setErrorType("VALIDATION_OR_BUSINESS_ERROR");
                error.setDetails(errorMsg);
                entity.addError(error);
            }
        }
    }
}