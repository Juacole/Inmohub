package com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities;

import com.inmohub.lead.service.domain.model.enums.EventType;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code lead_events} en la base de datos.
 * Registra eventos del ciclo de vida del lead con su tipo, metadatos en JSON y marca de tiempo.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "lead_events")
@EntityListeners(AuditingEntityListener.class)
public class LeadEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "lead_id", nullable = false)
    private UUID leadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON) // Mapea el Map a JSON
    private Map<String, Object> metadata;

    @Timestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}