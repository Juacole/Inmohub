package com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers;

import com.inmohub.lead.service.domain.model.enums.EventType;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadEventJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.inmohub.lead.service.domain.model.LeadAuditLog;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.UUID;

/**
 * Tests para {@link LeadEventMapper}.
 * Verifica la conversion de logs de auditoria a entidades JPA,
 * incluyendo el empaquetado de metadata con estados previo/nuevo.
 */
@DisplayName("LeadEventMapper")
class LeadEventMapperTest {

    private final LeadEventMapper eventMapper = new LeadEventMapperImpl();

    private final UUID leadId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Debe convertir LeadAuditLog a LeadEventJpaEntity con metadata")
    void dominioAJpaEntity() {
        LeadAuditLog log = LeadAuditLog.create(leadId, LeadStatus.NEW, LeadStatus.CONTACTED,
                "Lead contactado por agente", userId);

        LeadEventJpaEntity entity = eventMapper.toJpaEntity(log);

        assertNotNull(entity);
        assertEquals(EventType.STATUS_CHANGED, entity.getEventType());
        assertEquals(log.getChangedAt(), entity.getTimestamp());

        Map<String, Object> metadata = entity.getMetadata();
        assertEquals(LeadStatus.NEW, metadata.get("previousStatus"));
        assertEquals(LeadStatus.CONTACTED, metadata.get("newStatus"));
        assertEquals("Lead contactado por agente", metadata.get("actionDescription"));
        assertEquals(userId, metadata.get("changedByUserId"));
    }

    @Test
    @DisplayName("Debe manejar previousStatus null en metadata")
    void estadoPrevioNulo() {
        LeadAuditLog log = LeadAuditLog.create(leadId, null, LeadStatus.NEW,
                "Creacion de lead", userId);

        LeadEventJpaEntity entity = eventMapper.toJpaEntity(log);

        Map<String, Object> metadata = entity.getMetadata();
        assertNull(metadata.get("previousStatus"));
        assertEquals(LeadStatus.NEW, metadata.get("newStatus"));
    }
}
