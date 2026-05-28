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
 * Entidad JPA que mapea la tabla {@code fsbo_errors} con los errores de procesamiento
 * asociados a un envio FSBO.
 */
@Entity
@Table(name = "fsbo_errors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FsboErrorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private FsboSubmissionJpaEntity submission;

    @Column(name = "error_type", nullable = false)
    private String errorType;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}