package com.inmohub.frontend.features.property.data

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.property.dtos.CreateProperty
import com.inmohub.frontend.features.property.responses.PagedListResponse
import com.inmohub.frontend.features.property.dtos.PropertyDto
import com.inmohub.frontend.features.property.dtos.PropertySummaryDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object PropertyRepository {
    suspend fun getPropertiesByOwner(ownerId: String): List<PropertyDto> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/search-by-owner-id/$ownerId")
            if (response.status.value == 200) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error al consultar listado de propiedades relacionadas a un ownerId: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllProperties(): List<PropertyDto> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/all")
            if (response.status.value == 200) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error al consultar listado de propiedades: ${e.message}")
            emptyList()
        }
    }

    suspend fun createProperty(datos: CreateProperty): PropertyDto? {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/properties/create") {
                contentType(ContentType.Application.Json)
                setBody(datos)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                println("La propiedad no pudo crearse: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Error al crear propiedad: ${e.message}")
            null
        }
    }

    suspend fun searchProperties(
        page: Int = 0,
        size: Int = 10,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        status: String? = null
    ): PagedListResponse<PropertySummaryDto>? {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/search") {
                parameter("page", page)
                parameter("size", size)
                city?.let { parameter("city", it) }
                minPrice?.let { parameter("minPrice", it) }
                maxPrice?.let { parameter("maxPrice", it) }
                status?.let { parameter("status", it) }
            }

            if (response.status.value == 200) {
                response.body()
            } else null
        } catch (e: Exception) {
            println("Error buscando propiedades por filtros especificos: ${e.message}")
            null
        }
    }

    suspend fun getPropertyById(id: String): PropertyDto? {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/search-by-id/$id")
            if (response.status.value == 200) {
                response.body()
            } else null
        } catch (e: Exception) {
            println("Error buscando una propiedad por su ID: ${e.message}")
            null
        }
    }

    suspend fun getPropertySummary(
        page: Int = 0,
        size: Int = 10
    ): PagedListResponse<PropertySummaryDto>? {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/summary") {
                parameter("page", page)
                parameter("size", size)
            }
            if (response.status.value == 200) {
                response.body()
            } else null
        } catch (e: Exception) {
            println("Error obteniendo catálogo resumido: ${e.message}")
            null
        }
    }
}