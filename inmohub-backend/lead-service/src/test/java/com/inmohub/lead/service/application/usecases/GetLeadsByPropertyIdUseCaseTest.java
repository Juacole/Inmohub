package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link GetLeadsByPropertyIdUseCase}.
 * Verifica la obtencion paginada de leads filtrados por propiedad.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetLeadsByPropertyIdUseCase")
class GetLeadsByPropertyIdUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private GetLeadsByPropertyIdUseCase useCase;

    private final UUID propertyId = UUID.randomUUID();

    @Test
    @DisplayName("Debe retornar leads paginados por propiedad")
    void obtenerLeadsPorPropiedadExitoso() {
        Lead lead = Lead.create("Pepe", new Email("pepe@test.com"), "600123456",
                "Info", LeadSource.WEB, propertyId);
        PaginatedResult<Lead> pagina = PaginatedResult.of(List.of(lead), 0, 10, 1);

        when(leadRepository.findByPropertyId(propertyId, 0, 10)).thenReturn(pagina);

        Result<PaginatedResult<LeadResponse>, Error> result = useCase.execute(propertyId, 0, 10);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getValue().totalElements());
        verify(leadRepository, times(1)).findByPropertyId(propertyId, 0, 10);
    }

    @Test
    @DisplayName("Debe fallar si el propertyId es null")
    void propertyIdNuloRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = useCase.execute(null, 0, 10);

        assertFalse(result.isSuccess());
        assertEquals("El ID de la propiedad no puede ser nulo.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si la pagina es negativa")
    void paginaNegativaRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = useCase.execute(propertyId, -1, 10);

        assertFalse(result.isSuccess());
        assertEquals("El número de página no puede ser negativo.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el tamano es mayor a 100")
    void tamanoExcedidoRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = useCase.execute(propertyId, 0, 200);

        assertFalse(result.isSuccess());
        assertEquals("El tamaño de página no puede exceder 100.", result.getErrorValue().get().getMessage());
    }
}
