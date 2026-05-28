package com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link LeadMapper}.
 * Verifica la conversion bidireccional entre la entidad de dominio {@link Lead}
 * y la entidad JPA {@link LeadJpaEntity}, incluyendo casos null.
 */
@DisplayName("LeadMapper")
class LeadMapperTest {
    private final LeadMapper leadMapper = new LeadMapper() {};

    private final UUID leadId = UUID.randomUUID();
    private final UUID propertyId = UUID.randomUUID();

    @Test
    @DisplayName("Debe convertir Lead de dominio a JPA Entity")
    void dominioAJpaEntity() {
        Lead domain = Lead.create("Pepe", new Email("pepe@test.com"),
                "600123456", "Info", LeadSource.WEB, propertyId);

        LeadJpaEntity entity = leadMapper.toJpaEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals("pepe@test.com", entity.getEmail());
        assertEquals("Pepe", entity.getName());
        assertEquals("600123456", entity.getPhone());
        assertEquals("Info", entity.getMessage());
        assertEquals(LeadSource.WEB, entity.getSource());
        assertEquals(propertyId, entity.getPropertyId());
        assertEquals(LeadStatus.NEW, entity.getStatus());
    }

    @Test
    @DisplayName("Debe convertir JPA Entity a Lead de dominio")
    void jpaEntityADominio() {
        LeadJpaEntity entity = new LeadJpaEntity();
        entity.setId(leadId);
        entity.setName("Maria");
        entity.setEmail("maria@test.com");
        entity.setPhone("600654321");
        entity.setMessage("Quiero alquilar");
        entity.setSource(LeadSource.FSBO);
        entity.setPropertyId(propertyId);
        entity.setStatus(LeadStatus.CONTACTED);

        Lead domain = leadMapper.toDomainEntity(entity);

        assertNotNull(domain);
        assertEquals(leadId, domain.getId());
        assertEquals("Maria", domain.getName());
        assertEquals("maria@test.com", domain.getEmail().value());
        assertEquals("600654321", domain.getPhone());
        assertEquals(LeadSource.FSBO, domain.getSource());
        assertEquals(LeadStatus.CONTACTED, domain.getStatus());
    }

    @Test
    @DisplayName("Debe retornar null si el dominio es null (toJpaEntity)")
    void dominioNuloRetornaNull() {
        assertNull(leadMapper.toJpaEntity(null));
    }

    @Test
    @DisplayName("Debe retornar null si la entidad es null (toDomainEntity)")
    void entidadNulaRetornaNull() {
        assertNull(leadMapper.toDomainEntity(null));
    }
}
