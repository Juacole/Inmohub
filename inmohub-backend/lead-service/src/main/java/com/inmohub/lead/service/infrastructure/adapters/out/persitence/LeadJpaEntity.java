package com.inmohub.lead.service.infrastructure.adapters.out.persitence;

import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leads")
public class LeadJpaEntity {
    @Id
    private UUID id;
    private String name;
    private Email email;
    private String phone;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Enumerated(EnumType.STRING)
    private LeadSource source;
    private UUID propertyId;
    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}