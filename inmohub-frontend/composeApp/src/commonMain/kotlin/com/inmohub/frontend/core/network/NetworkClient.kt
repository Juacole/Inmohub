package com.inmohub.frontend.core.network

import com.inmohub.frontend.features.auth.data.local.SessionManager
import com.inmohub.frontend.features.auth.responses.LoginResponse
import com.inmohub.frontend.features.auth.requests.RefreshTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient { // singleton
    const val BASE_URL = "http://localhost:8080/api/v1"
    lateinit var sessionManager: SessionManager

    val client by lazy {
        HttpClient {
            install(ContentNegotiation) { // Configuración especifica para JSON
                json(Json {
                    ignoreUnknownKeys = true // evita que el cliente no falle si la request tiene campos que los dtos no
                    prettyPrint = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens { // Inyecta el token en cada petición
                        val accessToken = sessionManager.getAccessToken()
                        val refreshToken = sessionManager.getRefreshToken()
                        if(accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken = accessToken, refreshToken = refreshToken) // Inyecta tokens en el header de la request
                        } else {
                            null
                        }
                    }

                    refreshTokens { // Cuando el access token expire se realizara una llamada http al servidor para refrescar el token
                        val currentRefreshToken = sessionManager.getRefreshToken() // Recuperamos el refresh token

                        try {
                            val refreshClient = HttpClient { // Creación en segundo plano de cliente http temporal para refrescar token
                                install(ContentNegotiation) {
                                    json(Json {
                                        ignoreUnknownKeys = true

                                    })
                                }
                            }

                            // Se lanza petición post para refresh token
                            val response = refreshClient.post("$BASE_URL/users/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(RefreshTokenRequest(refreshToken = currentRefreshToken))
                            }

                            if(response.status.value == 200) {
                                val tokens = response.body<LoginResponse>()
                                sessionManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                                BearerTokens(tokens.accessToken, tokens.accessToken)
                            } else {
                                sessionManager.clearSession()
                                null
                            }
                        } catch (ex: Exception) {
                            sessionManager.clearSession()
                            null
                        }
                    }
                }
            }
        }
    }
}