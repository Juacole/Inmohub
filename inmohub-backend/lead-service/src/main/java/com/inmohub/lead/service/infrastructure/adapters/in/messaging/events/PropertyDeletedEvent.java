package com.inmohub.lead.service.infrastructure.adapters.in.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento que notifica la eliminacion de una propiedad.
 * Contiene el tipo de evento, el ID de la propiedad y la marca de tiempo.
 */
public record PropertyDeletedEvent(
        String eventType,
        UUID propertyId,
        LocalDateTime timestamp
) {}
