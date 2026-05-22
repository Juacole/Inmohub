package com.inmohub.frontend.features.auth.data

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.auth.domain.User
import com.inmohub.frontend.features.auth.responses.LoginResponse
import com.inmohub.frontend.features.auth.requests.LoginRequest
import com.inmohub.frontend.features.auth.requests.RegisterRequest
import com.inmohub.frontend.features.auth.responses.UserSummaryResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object AuthRepository {
    private val sessionManager = NetworkClient.sessionManager

    suspend fun register(request: RegisterRequest): Boolean {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error registrando usuario: ${e.message}")
            false
        }
    }

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            if (response.status.value == 200) {
                val tokens = response.body<LoginResponse>()
                sessionManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                tokens
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

    suspend fun getUsersByRole(role: String): List<UserSummaryResponse> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/auth/role/$role")
            if (response.status.value == 200) {
                response.body()
            } else {
               emptyList()
            }
        } catch (e: Exception) {
            println("Error al recuperar usuarios con rol ${role}: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/auth/search-by-id/$userId")
            if (response.status.value == 200) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}