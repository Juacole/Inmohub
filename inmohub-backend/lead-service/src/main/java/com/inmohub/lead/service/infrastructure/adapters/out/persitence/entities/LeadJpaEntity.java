package com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities;

import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "leads")
@EntityListeners(AuditingEntityListener.class)
public class LeadJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Column(nullable = false)
    private Email email;

    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private LeadSource source;

    @Column(nullable = false)
    private UUID propertyId;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}