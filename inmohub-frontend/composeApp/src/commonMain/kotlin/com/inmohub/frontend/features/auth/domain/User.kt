package com.inmohub.frontend.features.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<String>,
    val phone: String,
    val token: String? = null,
    val status: String,
    val createdAt: String,
    val updatedAt: String? = null
)