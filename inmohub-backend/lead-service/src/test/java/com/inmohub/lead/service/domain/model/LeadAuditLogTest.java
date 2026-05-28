package com.inmohub.lead.service.domain.model;

import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link LeadAuditLog}.
 * Verifica el registro de auditoria de cambios de estado en leads,
 * incluyendo el caso de primer evento (previousStatus null)
 * y cambios realizados por el sistema (changedByUserId null).
 */
@DisplayName("LeadAuditLog (Modelo de Dominio)")
class LeadAuditLogTest {

    private final UUID leadId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Debe crear un registro de auditoria con estado previo")
    void crearAuditLogConEstadoPrevio() {
        LeadAuditLog log = LeadAuditLog.create(leadId, LeadStatus.NEW, LeadStatus.CONTACTED,
                "Lead contactado", userId);

        assertNotNull(log.getId());
        assertEquals(leadId, log.getLeadId());
        assertEquals(LeadStatus.NEW, log.getPreviousStatus());
        assertEquals(LeadStatus.CONTACTED, log.getNewStatus());
        assertEquals("Lead contactado", log.getActionDescription());
        assertEquals(userId, log.getChangedByUserId());
        assertNotNull(log.getChangedAt());
    }

    @Test
    @DisplayName("Debe permitir previousStatus null (primer evento)")
    void crearAuditLogSinEstadoPrevio() {
        LeadAuditLog log = LeadAuditLog.create(leadId, null, LeadStatus.NEW,
                "Creacion de lead", userId);

        assertNull(log.getPreviousStatus());
        assertEquals(LeadStatus.NEW, log.getNewStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si leadId es null")
    void leadIdNuloLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            LeadAuditLog.create(null, LeadStatus.NEW, LeadStatus.CONTACTED, "desc", userId);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepcion si newStatus es null")
    void newStatusNuloLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            LeadAuditLog.create(leadId, LeadStatus.NEW, null, "desc", userId);
        });
    }

    @Test
    @DisplayName("Debe asignar cadena vacia si actionDescription es null")
    void descripcionNulaAsignaVacio() {
        LeadAuditLog log = LeadAuditLog.create(leadId, LeadStatus.NEW, LeadStatus.CONTACTED, null, userId);
        assertEquals("", log.getActionDescription());
    }

    @Test
    @DisplayName("Debe permitir changedByUserId null (cambio por sistema)")
    void usuarioSistemaNull() {
        LeadAuditLog log = LeadAuditLog.create(leadId, LeadStatus.NEW, LeadStatus.CONTACTED,
                "Cambio automatico", null);

        assertEquals(LeadStatus.CONTACTED, log.getNewStatus());
        assertNull(log.getChangedByUserId());
    }
}
