package com.inmohub.lead.service.infrastructure.adapters.out.messaging.events;

import java.util.UUID;

/**
 * Representa evento exacto tal y como lo emitira el fsbo-service
 */
public record FsboPropertyIngestedEvent(
        UUID propertyId,
        String ownerName,
        String ownerEmail,
        String ownerPhone,
        String ingestionSource
) {}