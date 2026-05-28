package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code fsbo_dedup_check} con el registro de duplicados
 * detectados durante la ingestion FSBO.
 */
@Entity
@Table(name = "fsbo_dedup_check")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FsboDedupCheckJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private FsboSubmissionJpaEntity submission;

    @Column(name = "matched_property_id")
    private UUID matchedPropertyId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}