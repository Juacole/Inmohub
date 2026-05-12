package com.inmohub.frontend.features.auth.dtos

import com.inmohub.frontend.features.auth.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String? = null,
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val phone: String
) {
    fun toUser(): User {
        return User(
            id = this.id,
            username = this.username,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            role = this.role,
            phone = this.phone,
            status = "ACTIVE"
        )
    }
}