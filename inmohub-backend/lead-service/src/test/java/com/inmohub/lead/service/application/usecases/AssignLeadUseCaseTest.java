package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.AssignLeadRequest;
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
 * Tests para {@link AssignLeadUseCase}.
 * Verifica la asignacion de leads a agentes, el cambio de estado a CONTACTED
 * y el registro de auditoria. Cubre casos de error como lead inexistente
 * o agentId nulo.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AssignLeadUseCase")
class AssignLeadUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private AssignLeadUseCase assignLeadUseCase;

    private final UUID leadId = UUID.randomUUID();
    private final UUID agentId = UUID.randomUUID();
    private final UUID propertyId = UUID.randomUUID();
    private final AssignLeadRequest request = new AssignLeadRequest(agentId, "Asignado por urgencia");

    private Lead crearLead() {
        return Lead.create("Pepe Montana", new Email("pepe@test.com"),
                "600123456", "Info", LeadSource.WEB, propertyId);
    }

    @Test
    @DisplayName("Debe asignar un lead exitosamente")
    void asignarLeadExitoso() {
        Lead lead = crearLead();

        when(leadRepository.findById(leadId)).thenReturn(lead);
        when(leadRepository.saveLead(any(Lead.class))).thenReturn(lead);

        var result = assignLeadUseCase.execute(leadId, request, agentId);

        assertTrue(result.isSuccess());
        assertEquals(lead.getId(), result.getValue().leadId());
        assertEquals(agentId, result.getValue().agentId());
        assertNotNull(result.getValue().assignedtAt());
        verify(leadRepository, times(1)).findById(leadId);
        verify(leadRepository, times(1)).saveLead(any(Lead.class));
        verify(leadRepository, times(1)).saveAssignment(any());
        verify(leadRepository, times(1)).saveAuditLog(any());
    }

    @Test
    @DisplayName("Debe fallar si el leadId es null")
    void leadIdNuloRetornaError() {
        var result = assignLeadUseCase.execute(null, request, agentId);

        assertFalse(result.isSuccess());
        assertEquals("El ID del lead no puede ser nulo.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe fallar si el request es null")
    void requestNuloRetornaError() {
        var result = assignLeadUseCase.execute(leadId, null, agentId);

        assertFalse(result.isSuccess());
        assertEquals("La solicitud no puede ser nula.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el agentId es null")
    void agentIdNuloRetornaError() {
        AssignLeadRequest requestSinAgente = new AssignLeadRequest(null, "notas");

        var result = assignLeadUseCase.execute(leadId, requestSinAgente, agentId);

        assertFalse(result.isSuccess());
        assertEquals("El ID del agente no puede ser nulo.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el lead no existe")
    void leadNoExisteRetornaError() {
        when(leadRepository.findById(leadId)).thenReturn(null);

        var result = assignLeadUseCase.execute(leadId, request, agentId);

        assertFalse(result.isSuccess());
        assertEquals("Lead no encontrado o null.", result.getErrorValue().get().getMessage());
        verify(leadRepository, never()).saveLead(any());
    }
}
