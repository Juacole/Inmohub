package com.inmohub.frontend.features.lead.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeLeadStatusRequest(
    val status: String
)