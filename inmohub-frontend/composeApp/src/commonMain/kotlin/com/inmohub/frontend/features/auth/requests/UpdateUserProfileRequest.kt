package com.inmohub.frontend.features.auth.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
)
