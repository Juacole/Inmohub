package com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers;

import com.inmohub.lead.service.domain.model.LeadAuditLog;
import com.inmohub.lead.service.domain.model.enums.EventType;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadEventJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper que convierte la entidad de auditoria {@link LeadAuditLog}
 * a la entidad JPA {@link LeadEventJpaEntity}, empaquetando los metadatos en formato JSON.
 */
@Mapper(componentModel = "spring", imports = EventType.class)
public interface LeadEventMapper {

    @Mapping(target = "eventType", constant = "STATUS_CHANGED")
    @Mapping(target = "timestamp", source = "changedAt")
    @Mapping(target = "metadata", expression = "java(buildMetadata(log))") // Empaquetado JSON
    LeadEventJpaEntity toJpaEntity(LeadAuditLog log);

    // Método de apoyo para construir el json del atributo metadata de entidad jpa
    default Map<String, Object> buildMetadata(LeadAuditLog log) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("previousStatus", log.getPreviousStatus());
        metadata.put("newStatus", log.getNewStatus());
        metadata.put("actionDescription", log.getActionDescription());
        metadata.put("changedByUserId", log.getChangedByUserId());
        return metadata;
    }
}