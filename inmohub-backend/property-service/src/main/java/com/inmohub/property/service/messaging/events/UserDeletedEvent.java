package com.inmohub.property.service.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio que representa la eliminacion de un usuario en el sistema.
 * Recibido via Kafka desde el Auth-Service para disparar la eliminacion en cascada
 * de todas las propiedades del usuario eliminado.
 */
public record UserDeletedEvent(
        String eventType,
        UUID userId,
        LocalDateTime timestamp
) {}
