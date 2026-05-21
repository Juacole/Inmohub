package com.inmohub.lead.service.infrastructure.adapters.in.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PropertyDeletedEvent(
        String eventType,
        UUID propertyId,
        LocalDateTime timestamp
) {}
