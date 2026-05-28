package com.inmohub.auth.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inmohub.auth.service.messaging.events.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link KafkaUserEventPublisher}.
 * Verifica la publicacion de eventos de usuario eliminado en Kafka
 * y el manejo de errores de serializacion JSON.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaUserEventPublisher")
class KafkaUserEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private KafkaUserEventPublisher publisher;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Debe publicar evento de usuario eliminado exitosamente")
    void publicarEventoUsuarioEliminado() {
        UserDeletedEvent event = UserDeletedEvent.of(userId);

        publisher.publishUserDeleted(event);

        verify(kafkaTemplate, times(1)).send(
                eq("user.lifecycle.events"),
                eq(userId.toString()),
                anyString()
        );
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si la serializacion falla")
    void serializacionFallida() {
        ObjectMapper badMapper = mock(ObjectMapper.class);
        KafkaUserEventPublisher badPublisher = new KafkaUserEventPublisher(kafkaTemplate, badMapper);

        UserDeletedEvent event = UserDeletedEvent.of(userId);
        try {
            when(badMapper.writeValueAsString(event))
                    .thenThrow(new JsonProcessingException("Error") {});
        } catch (JsonProcessingException e) {
            fail("Mock setup failed");
        }

        assertThrows(RuntimeException.class, () -> badPublisher.publishUserDeleted(event));
    }
}
