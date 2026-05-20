package com.inmohub.auth.service.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeletedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime timestamp
) {
    public UserDeletedEvent {
        if (eventId == null) throw new IllegalArgumentException("eventId no puede ser nulo");
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType no puede ser nulo");
        if (userId == null) throw new IllegalArgumentException("userId no puede ser nulo");
        if (timestamp == null) throw new IllegalArgumentException("timestamp no puede ser nulo");
    }

    public static UserDeletedEvent of(UUID userId) {
        return new UserDeletedEvent(
                UUID.randomUUID(),
                "USER_DELETED",
                userId,
                LocalDateTime.now()
        );
    }
}
