package com.inmohub.frontend.features.lead.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LeadDetailDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val message: String? = null,
    val status: String,
    val source: String,
    val propertyId: String,
    val senderParticipantId: String? = null,
    val agentId: String? = null,
    val assignedAt: String? = null,
    val assignmentNotes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)