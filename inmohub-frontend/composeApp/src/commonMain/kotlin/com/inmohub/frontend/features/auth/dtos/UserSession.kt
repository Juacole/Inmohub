package com.inmohub.frontend.features.auth.dtos

data class UserSession(
    val id: String,
    val username: String,
    val role: String,
    val token: String
)