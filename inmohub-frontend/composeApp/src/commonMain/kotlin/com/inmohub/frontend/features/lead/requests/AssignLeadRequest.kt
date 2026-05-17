package com.inmohub.frontend.features.lead.requests

import kotlinx.serialization.Serializable

@Serializable
data class AssignLeadRequest(
    val agentId: String,
    val assignmentNotes: String? = null
)