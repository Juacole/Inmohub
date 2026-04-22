package com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "lead_assignments")
public class LeadAssignmentJpaEntity {
    @Id
    private UUID id;

    @Column(name = "lead_id", nullable = false)
    private UUID leadId;

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    private String notes;
}