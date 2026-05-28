package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.abstractions.Unit;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link DeleteLeadsByPropertyIdUseCase}.
 * Verifica la eliminacion de todos los leads asociados a una propiedad.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteLeadsByPropertyIdUseCase")
class DeleteLeadsByPropertyIdUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private DeleteLeadsByPropertyIdUseCase useCase;

    private final UUID propertyId = UUID.randomUUID();

    @Test
    @DisplayName("Debe eliminar los leads asociados a la propiedad")
    void eliminarLeadsExitoso() {
        doNothing().when(leadRepository).deleteByPropertyId(propertyId);

        Result<Unit, Error> result = useCase.execute(propertyId);

        assertTrue(result.isSuccess());
        verify(leadRepository, times(1)).deleteByPropertyId(propertyId);
    }

    @Test
    @DisplayName("Debe fallar si el propertyId es null")
    void propertyIdNuloRetornaError() {
        Result<Unit, Error> result = useCase.execute(null);

        assertFalse(result.isSuccess());
        assertEquals("El ID de la propiedad no puede ser nulo.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).deleteByPropertyId(any());
    }
}
