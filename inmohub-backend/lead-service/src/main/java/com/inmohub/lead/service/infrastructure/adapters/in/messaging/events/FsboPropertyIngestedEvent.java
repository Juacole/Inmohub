package com.inmohub.lead.service.infrastructure.adapters.in.messaging.events;

import java.util.UUID;

/**
 * Evento que notifica la ingestion masiva de propiedades FSBO (venta directa por propietario).
 * Contiene los datos del propietario y la propiedad para generar leads.
 */
public record FsboPropertyIngestedEvent(
        String eventType,
        UUID ownerId,
        String ownerName,
        String ownerEmail,
        String ownerPhone,
        String ingestionSource,
        UUID propertyId
) {}