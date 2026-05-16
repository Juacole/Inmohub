package com.inmohub.frontend.core.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object JwtUtils {

    @OptIn(ExperimentalEncodingApi::class)
    private fun getJsonPayload(token: String) : JsonObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(Base64.UrlSafe.decode(parts[1]))

            Json.decodeFromString<JsonObject>(payload)
        } catch (ex: Exception) {
            return null
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
            rolesList?.firstOrNull()?.jsonPrimitive?.content
        } catch (ex: Exception) {
            null
        }
    }
}