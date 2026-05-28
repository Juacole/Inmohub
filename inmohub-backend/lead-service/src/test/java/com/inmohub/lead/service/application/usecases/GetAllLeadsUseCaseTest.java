package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
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
 * Tests para {@link GetAllLeadsUseCase}.
 * Verifica la paginacion de leads con validacion de parametros
 * (pagina negativa, tamano cero, tamano maximo excedido).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllLeadsUseCase")
class GetAllLeadsUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private GetAllLeadsUseCase getAllLeadsUseCase;

    private final UUID propertyId = UUID.randomUUID();

    private Lead crearLead(String nombre) {
        return Lead.create(nombre, new Email(nombre.toLowerCase().replace(" ", ".") + "@test.com"),
                "600123456", "Info", LeadSource.WEB, propertyId);
    }

    @Test
    @DisplayName("Debe retornar lista paginada de leads exitosamente")
    void obtenerTodosExitoso() {
        Lead lead1 = crearLead("Pepe Montana");
        Lead lead2 = crearLead("Maria Perez");
        List<Lead> leads = List.of(lead1, lead2);
        PaginatedResult<Lead> pagina = PaginatedResult.of(leads, 0, 10, 2);

        when(leadRepository.findAll(0, 10)).thenReturn(pagina);

        Result<PaginatedResult<LeadResponse>, Error> result = getAllLeadsUseCase.execute(0, 10);

        assertTrue(result.isSuccess());
        PaginatedResult<LeadResponse> response = result.getValue();
        assertEquals(2, response.totalElements());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals("Pepe Montana", response.content().get(0).name());
        assertEquals("Maria Perez", response.content().get(1).name());
        verify(leadRepository, times(1)).findAll(0, 10);
    }

    @Test
    @DisplayName("Debe retornar error si la pagina es negativa")
    void paginaNegativaRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = getAllLeadsUseCase.execute(-1, 10);

        assertFalse(result.isSuccess());
        assertEquals("El número de página no puede ser negativo.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe retornar error si el tamano es menor o igual a cero")
    void tamanoInvalidoRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = getAllLeadsUseCase.execute(0, 0);

        assertFalse(result.isSuccess());
        assertEquals("El tamaño de página debe ser mayor a cero.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe retornar error si el tamano excede 100")
    void tamanoExcedidoRetornaError() {
        Result<PaginatedResult<LeadResponse>, Error> result = getAllLeadsUseCase.execute(0, 200);

        assertFalse(result.isSuccess());
        assertEquals("El tamaño de página no puede exceder 100.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe retornar lista vacia si no hay leads")
    void sinResultadosRetornaVacio() {
        PaginatedResult<Lead> paginaVacia = PaginatedResult.of(List.of(), 0, 10, 0);
        when(leadRepository.findAll(0, 10)).thenReturn(paginaVacia);

        Result<PaginatedResult<LeadResponse>, Error> result = getAllLeadsUseCase.execute(0, 10);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getValue().totalElements());
        assertTrue(result.getValue().content().isEmpty());
    }
}
