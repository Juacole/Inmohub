package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link ChangeLeadStatusUseCase}.
 * Verifica el cambio de estado de leads con control de roles.
 * Los ADMIN pueden cambiar cualquier lead, los AGENT solo los suyos.
 * Cubre estados invalidos y lead no encontrado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeLeadStatusUseCase")
class ChangeLeadStatusUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private ChangeLeadStatusUseCase changeLeadStatusUseCase;

    private final UUID leadId = UUID.randomUUID();
    private final UUID agentId = UUID.randomUUID();
    private final UUID propertyId = UUID.randomUUID();
    private final UUID otroAgentId = UUID.randomUUID();

    private Lead crearLead() {
        return Lead.create("Pepe Montana", new Email("pepe@test.com"),
                "600123456", "Info", LeadSource.WEB, propertyId);
    }

    @Test
    @DisplayName("Debe cambiar el estado de un lead siendo ADMIN")
    void cambiarEstadoComoAdmin() {
        Lead lead = crearLead();
        when(leadRepository.findById(leadId)).thenReturn(lead);
        when(leadRepository.saveLead(any(Lead.class))).thenReturn(lead);

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "CONTACTED", agentId, "ROLE_ADMIN");

        assertTrue(result.isSuccess());
        verify(leadRepository, times(1)).findById(leadId);
        verify(leadRepository, times(1)).saveLead(any(Lead.class));
        verify(leadRepository, never()).findAssignmentsByLeadId(any());
    }

    @Test
    @DisplayName("Debe cambiar el estado si el agente esta asignado al lead")
    void cambiarEstadoAgenteAsignado() {
        Lead lead = crearLead();
        LeadAssignment assignment = LeadAssignment.create(lead.getId(), agentId, "notas");

        when(leadRepository.findById(leadId)).thenReturn(lead);
        when(leadRepository.findAssignmentsByLeadId(leadId)).thenReturn(List.of(assignment));
        when(leadRepository.saveLead(any(Lead.class))).thenReturn(lead);

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "NEGOTIATION", agentId, "ROLE_AGENT");

        assertTrue(result.isSuccess());
        verify(leadRepository, times(1)).findAssignmentsByLeadId(leadId);
    }

    @Test
    @DisplayName("Debe fallar si el agente no esta asignado al lead")
    void agenteNoAsignadoRetornaError() {
        Lead lead = crearLead();

        when(leadRepository.findById(leadId)).thenReturn(lead);
        when(leadRepository.findAssignmentsByLeadId(leadId)).thenReturn(List.of());

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "CONTACTED", otroAgentId, "ROLE_AGENT");

        assertFalse(result.isSuccess());
        assertEquals("No tienes permisos para modificar este lead.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el rol no es ADMIN ni AGENT")
    void rolNoAutorizadoRetornaError() {
        Lead lead = crearLead();
        when(leadRepository.findById(leadId)).thenReturn(lead);

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "CONTACTED", agentId, "ROLE_CLIENT");

        assertFalse(result.isSuccess());
        assertEquals("No tienes permisos para modificar este lead.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el lead no existe")
    void leadNoExisteRetornaError() {
        when(leadRepository.findById(leadId)).thenReturn(null);

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "CONTACTED", agentId, "ROLE_ADMIN");

        assertFalse(result.isSuccess());
        assertEquals("Lead no encontrado.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el nuevo estado es invalido")
    void estadoInvalidoRetornaError() {
        Lead lead = crearLead();
        when(leadRepository.findById(leadId)).thenReturn(lead);

        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, "ESTADO_INEXISTENTE", agentId, "ROLE_ADMIN");

        assertFalse(result.isSuccess());
        assertEquals("Estado de lead inválido.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el leadId es null")
    void leadIdNuloRetornaError() {
        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                null, "CONTACTED", agentId, "ROLE_ADMIN");

        assertFalse(result.isSuccess());
        assertEquals("El ID del lead no puede ser nulo.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el nuevo estado es null")
    void estadoNullRetornaError() {
        Result<LeadResponse, Error> result = changeLeadStatusUseCase.execute(
                leadId, null, agentId, "ROLE_ADMIN");

        assertFalse(result.isSuccess());
        assertEquals("El nuevo estado no puede estar vacío.", result.getErrorValue().get().getMessage());
    }
}
