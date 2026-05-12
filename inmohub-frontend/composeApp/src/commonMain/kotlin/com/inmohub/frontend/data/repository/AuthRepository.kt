package com.inmohub.frontend.data.repository

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.data.model.User
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String
)

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

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

object AuthRepository {
    suspend fun register(request: RegisterRequest): Boolean {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/users/create") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error registrando: ${e.message}")
            false
        }
    }

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/users/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            if (response.status.value == 200) {
                response.body<LoginResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            println("ERROR CRÍTICO EN LOGIN:")
            e.printStackTrace()
            println("Error Login: ${e.message}")
            null
        }
    }
}