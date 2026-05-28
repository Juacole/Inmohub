package com.inmohub.lead.service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link LeadAssignment}.
 * Verifica la creacion y reconstituicion de asignaciones de leads a agentes,
 * incluyendo validaciones de campos obligatorios.
 */
@DisplayName("LeadAssignment (Modelo de Dominio)")
class LeadAssignmentTest {

    private final UUID leadId = UUID.randomUUID();
    private final UUID agentId = UUID.randomUUID();

    @Test
    @DisplayName("Debe crear una asignacion con los datos requeridos")
    void crearAsignacionExitosa() {
        LeadAssignment assignment = LeadAssignment.create(leadId, agentId, "Asignado por prioridad");

        assertNotNull(assignment.getId());
        assertEquals(leadId, assignment.getLeadId());
        assertEquals(agentId, assignment.getAgentId());
        assertEquals("Asignado por prioridad", assignment.getNotes());
        assertNotNull(assignment.getAssignedAt());
    }

    @Test
    @DisplayName("Debe asignar cadena vacia si las notas son null")
    void notasNulasAsignaVacio() {
        LeadAssignment assignment = LeadAssignment.create(leadId, agentId, null);
        assertEquals("", assignment.getNotes());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el leadId es null")
    void leadIdNuloLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            LeadAssignment.create(null, agentId, "notas");
        });
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el agentId es null")
    void agentIdNuloLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            LeadAssignment.create(leadId, null, "notas");
        });
    }

    @Test
    @DisplayName("Debe reconstituir una asignacion existente")
    void reconstituirAsignacion() {
        UUID id = UUID.randomUUID();
        LocalDateTime assignedAt = LocalDateTime.now();

        LeadAssignment assignment = LeadAssignment.reconstitute(id, leadId, agentId, "notas", assignedAt);

        assertEquals(id, assignment.getId());
        assertEquals(leadId, assignment.getLeadId());
        assertEquals(agentId, assignment.getAgentId());
        assertEquals(assignedAt, assignment.getAssignedAt());
    }
}
