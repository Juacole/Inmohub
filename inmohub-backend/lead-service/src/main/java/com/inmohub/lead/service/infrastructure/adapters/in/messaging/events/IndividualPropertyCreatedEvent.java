package com.inmohub.lead.service.infrastructure.adapters.in.messaging.events;

import java.util.UUID;

/**
 * Evento que notifica la creacion individual de una propiedad (no masiva).
 * Contiene los datos del propietario y la propiedad para generar un lead.
 */
public record IndividualPropertyCreatedEvent(
        String eventType,
        UUID propertyId,
        UUID ownerId,
        String ownerName,
        String ownerEmail,
        String ownerPhone,
        String ingestionSource
) {}