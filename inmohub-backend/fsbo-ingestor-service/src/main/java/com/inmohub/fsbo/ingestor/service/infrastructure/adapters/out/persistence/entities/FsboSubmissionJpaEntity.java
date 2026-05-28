package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code fsbo_submissions} con los datos de cada
 * propiedad enviada por un propietario FSBO.
 */
@Entity
@Table(name = "fsbo_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FsboSubmissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "owner_phone")
    private String ownerPhone;

    @Column(name = "property_title")
    private String propertyTitle;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "area_m2")
    private Double areaM2;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FsboDedupCheckJpaEntity> dedupChecks = new ArrayList<>();

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FsboErrorJpaEntity> errors = new ArrayList<>();

    public void addDedupCheck(FsboDedupCheckJpaEntity check) {
        dedupChecks.add(check);
        check.setSubmission(this);
    }

    public void addError(FsboErrorJpaEntity error) {
        errors.add(error);
        error.setSubmission(this);
    }
}