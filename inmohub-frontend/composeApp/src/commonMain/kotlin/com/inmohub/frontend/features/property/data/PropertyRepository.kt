package com.inmohub.frontend.features.property.data

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.property.domain.Property
import com.inmohub.frontend.features.property.dtos.CreateProperty
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object PropertyRepository {
    suspend fun getPropertiesByOwner(ownerId: String): List<Property> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/search-by-owner-id/$ownerId")
            if (response.status.value == 200) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching properties: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllProperties(): List<Property> {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/properties/all")
            if (response.status.value == 200) {
                response.body()
            } else {
                getMockProperties()
            }
        } catch (e: Exception) {
            getMockProperties()
        }
    }

    suspend fun createProperty(datos: CreateProperty): Property? {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/properties/create") {
                contentType(ContentType.Application.Json)
                setBody(datos)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                println("Error creación: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al crear propiedad: ${e.message}")
            null
        }
    }

    fun getMockProperties(): List<Property> {
        return listOf(
//            Property(null, "Ático en Madrid", "Precioso ático centro", 350000.0, "Calle Mayor 1", 95.0, "AVAILABLE", ""),
//            Property(null, "Villa en la Costa", "Vistas al mar", 850000.0, "Marbella", 250.0, "AVAILABLE", ""),
//            Property(null, "Piso de Estudiantes", "Cerca universidad", 120000.0, "Valencia", 80.0, "RENTED", ""),
//            Property(null, "Casa Rural", "Perfecta para desconectar", 180000.0, "Asturias", 150.0, "AVAILABLE", "")
        )
    }
}