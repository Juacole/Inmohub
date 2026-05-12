package com.inmohub.frontend.data.repository

import com.inmohub.frontend.core.network.NetworkClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class UserSummary(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val phone: String
)

object UserRepository {
    suspend fun getUsersByRole(role: String): List<UserSummary> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/users/role/$role")
            if (response.status.value == 200) {
                response.body()
            } else {
                datosPrueba(role)
            }
        } catch (e: Exception) {
            datosPrueba(role)
        }
    }

    private fun datosPrueba(role: String): List<UserSummary> {
        return if (role == "CLIENT") {
            listOf(
//                UserSummary("1", "Ana Garcia", "ana@gmail.com", "CLIENT", "600111222"),
//                UserSummary("2", "Pedro Lopez", "pedro@hotmail.com", "CLIENT", "600333444"),
//                UserSummary("3", "Maria Sanz", "maria@yahoo.es", "CLIENT", "600555666")
            )
        } else { // OWNER
            listOf(
//                UserSummary("4", "Carlos Dueño", "carlos@gmail.com", "OWNER", "600777888"),
//                UserSummary("5", "Luisa Casera", "luisa@inmo.com", "OWNER", "600999000")
            )
        }
    }
}