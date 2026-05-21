package com.inmohub.auth.service.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeletedEvent(
        String eventType,
        UUID userId,
        LocalDateTime timestamp
) {
    public UserDeletedEvent {
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType no puede ser nulo");
        if (userId == null) throw new IllegalArgumentException("userId no puede ser nulo");
        if (timestamp == null) throw new IllegalArgumentException("timestamp no puede ser nulo");
    }

    public static UserDeletedEvent of(UUID userId) {
        return new UserDeletedEvent(
                "USER_DELETED",
                userId,
                LocalDateTime.now()
        );
    }
}
