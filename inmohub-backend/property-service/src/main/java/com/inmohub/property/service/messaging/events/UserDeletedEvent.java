package com.inmohub.property.service.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeletedEvent(
        String eventType,
        UUID userId,
        LocalDateTime timestamp
) {}
