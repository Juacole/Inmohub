package com.inmohub.frontend.core.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object JwtUtils {

    @OptIn(ExperimentalEncodingApi::class)
    private fun getJsonPayload(token: String): JsonObject? {
        return try {
            val parts = token.trim().split(".")
            if (parts.size != 3) return null

            val decoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
            val payload = String(decoder.decode(parts[1]))

            println("JWT Payload decodificado: $payload")

            Json.decodeFromString<JsonObject>(payload)
        } catch (ex: Exception) {
            println("Error decodificando JWT: ${ex.message}")
            null
        }
    }

    fun getUserId(token: String): String? {
        val jsonObject = getJsonPayload(token) ?: return null

        return try {
            jsonObject["userId"]?.jsonPrimitive?.content
        } catch (ex: Exception) {
            null
        }
    }

    fun getUserRoleFromToken(token: String): String? {
        val jsonObject = getJsonPayload(token) ?: return null

        return try {
            val rolesList = jsonObject["roles"]?.jsonArray
            val rawRole = rolesList?.firstOrNull()?.jsonPrimitive?.content

            println("Rol crudo extraído: $rawRole")

            rawRole?.replace("ROLE_", "")
        } catch (ex: Exception) {
            println("Error extrayendo rol: ${ex.message}")
            null
        }
    }

    fun isTokenExpired(token: String): Boolean {
        val jsonObject = getJsonPayload(token) ?: return true
        return try {
            val exp = jsonObject["exp"]?.jsonPrimitive?.content?.toLongOrNull()
            if (exp == null) true
            else exp < System.currentTimeMillis() / 1000
        } catch (ex: Exception) {
            true
        }
    }
}