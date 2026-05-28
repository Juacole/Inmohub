package com.inmohub.property.service.messaging.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inmohub.property.service.messaging.dtos.PropertyNodeDto;

import java.util.List;

/**
 * Evento de dominio para la creacion masiva de propiedades via Kafka.
 * Transporta el ID del propietario y la lista de propiedades a registrar en lote.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BulkPropertyEvent(
        String ownerId,
        List<PropertyNodeDto> properties
) {}