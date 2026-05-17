package com.inmohub.frontend.features.lead.responses

import kotlinx.serialization.Serializable

@Serializable
data class LeadAssignmentResponse(
    val leadId: String,
    val agentId: String,
    val assignedAt: String
)