package com.inmohub.property.service.messaging.events;

import java.util.UUID;

/**
 * Evento de dominio que representa la creacion individual de una propiedad.
 * Se publica en Kafka cuando un propietario registra un inmueble manualmente
 * para que el servicio de leads inicie el seguimiento comercial.
 */
public record IndividualPropertyCreatedEvent(
        String eventType,
        UUID propertyId,
        UUID ownerId,
        String ownerName,
        String ownerEmail,
        String ownerPhone,
        String ingestionSource
) {
}