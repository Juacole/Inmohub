package com.inmohub.frontend.features.auth.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserSummaryResponse(
    val id: String,
    val username: String,
    val email: String,
    val roles: Set<String>,
    val phone: String
)