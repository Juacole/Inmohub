package com.inmohub.frontend.features.lead.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LeadSummaryDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val status: String,
    val source: String? = "",
    val propertyId: String,
    val senderParticipantId: String? = null,
    val createdAt: String? = null
)