package com.inmohub.frontend.features.property.data

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.property.dtos.CreateProperty
import com.inmohub.frontend.features.property.dtos.PropertyDto
import com.inmohub.frontend.features.property.dtos.PropertyPatchRequest
import com.inmohub.frontend.features.property.dtos.PropertyPhotoDto
import com.inmohub.frontend.features.property.dtos.PropertySummaryDto
import com.inmohub.frontend.features.property.responses.PagedListResponse
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

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

    suspend fun createPropertyWithImages(
        datos: CreateProperty,
        imageBytes: List<ByteArray>
    ): PropertyDto? {
        return try {
            val propertyJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }.encodeToString(datos)
            println("JSON enviado al crear propiedad: $propertyJson")
            val boundary = "----FormBoundary${System.currentTimeMillis()}"
            val body = buildPropertyMultipartBody(boundary, propertyJson, imageBytes)
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/properties/create") {
                header(HttpHeaders.ContentType, "multipart/form-data; boundary=$boundary")
                setBody(body)
            }
            if (response.status.value in 200..299) {
                response.body()
            } else {
                println("La propiedad no pudo crearse: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Error al crear propiedad con imágenes: ${e.message}")
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

    suspend fun updateProperty(id: String, request: PropertyPatchRequest): PropertyDto? {
        return try {
            val response = NetworkClient.client.patch("${NetworkClient.BASE_URL}/properties/update/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                println("Error al actualizar propiedad: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Error al actualizar propiedad: ${e.message}")
            null
        }
    }

    suspend fun deleteProperty(id: String): Boolean {
        return try {
            val response = NetworkClient.client.delete("${NetworkClient.BASE_URL}/properties/delete-by-id/$id")
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error al eliminar propiedad: ${e.message}")
            false
        }
    }

    suspend fun uploadPropertyImages(propertyId: String, imageBytes: List<ByteArray>): List<PropertyPhotoDto>? {
        return try {
            imageBytes.forEachIndexed { index, bytes ->
                println("Imagen $index: ${bytes.size} bytes")
            }
            val boundary = "----FormBoundary${System.currentTimeMillis()}"
            val body = buildImageMultipartBody(boundary, imageBytes)
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/properties/$propertyId/images") {
                header(HttpHeaders.ContentType, "multipart/form-data; boundary=$boundary")
                setBody(body)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                println("Error al subir imágenes: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Error al subir imágenes: ${e.message}")
            null
        }
    }

    suspend fun uploadCsvFile(fileBytes: ByteArray): Boolean {
        return try {
            val boundary = "----FormBoundary${System.currentTimeMillis()}"
            val body = buildCsvMultipartBody(boundary, fileBytes)
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/fsbo/properties/bulk") {
                header(HttpHeaders.ContentType, "multipart/form-data; boundary=$boundary")
                setBody(body)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error al subir archivo CSV: ${e.message}")
            false
        }
    }

    private fun buildImageMultipartBody(boundary: String, images: List<ByteArray>): ByteArray {
        val crlf = "\r\n".encodeToByteArray()
        val doubleDash = "--".encodeToByteArray()
        val boundaryBytes = boundary.encodeToByteArray()
        val partHeader = "Content-Disposition: form-data; name=\"photos\"; filename=\"image.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n".encodeToByteArray()

        val parts = images.flatMap { bytes ->
            listOf(doubleDash, boundaryBytes, crlf, partHeader, bytes, crlf)
        }
        val closing = doubleDash + boundaryBytes + doubleDash + crlf

        val totalSize = parts.sumOf { it.size } + closing.size
        val output = ByteArray(totalSize)
        var offset = 0
        for (part in parts) {
            part.copyInto(output, offset)
            offset += part.size
        }
        closing.copyInto(output, offset)
        return output
    }

    private fun buildPropertyMultipartBody(boundary: String, propertyJson: String, images: List<ByteArray>): ByteArray {
        val crlf = "\r\n".encodeToByteArray()
        val doubleDash = "--".encodeToByteArray()
        val boundaryBytes = boundary.encodeToByteArray()
        val jsonHeader = "Content-Disposition: form-data; name=\"property\"\r\nContent-Type: application/json\r\n\r\n".encodeToByteArray()
        val imageHeader = "Content-Disposition: form-data; name=\"photos\"; filename=\"image.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n".encodeToByteArray()
        val jsonBytes = propertyJson.encodeToByteArray()

        val parts = mutableListOf<ByteArray>()
        parts.addAll(listOf(doubleDash, boundaryBytes, crlf, jsonHeader, jsonBytes, crlf))
        images.forEach { bytes ->
            parts.addAll(listOf(doubleDash, boundaryBytes, crlf, imageHeader, bytes, crlf))
        }
        val closing = doubleDash + boundaryBytes + doubleDash + crlf

        val totalSize = parts.sumOf { it.size } + closing.size
        val output = ByteArray(totalSize)
        var offset = 0
        for (part in parts) {
            part.copyInto(output, offset)
            offset += part.size
        }
        closing.copyInto(output, offset)
        return output
    }

    private fun buildCsvMultipartBody(boundary: String, fileBytes: ByteArray): ByteArray {
        val crlf = "\r\n".encodeToByteArray()
        val doubleDash = "--".encodeToByteArray()
        val boundaryBytes = boundary.encodeToByteArray()
        val fileHeader = "Content-Disposition: form-data; name=\"file\"; filename=\"data.csv\"\r\nContent-Type: text/csv\r\n\r\n".encodeToByteArray()

        val output = ByteArray(
            doubleDash.size + boundaryBytes.size + crlf.size +
            fileHeader.size + fileBytes.size + crlf.size +
            doubleDash.size + boundaryBytes.size + doubleDash.size + crlf.size
        )
        var offset = 0
        doubleDash.copyInto(output, offset); offset += doubleDash.size
        boundaryBytes.copyInto(output, offset); offset += boundaryBytes.size
        crlf.copyInto(output, offset); offset += crlf.size
        fileHeader.copyInto(output, offset); offset += fileHeader.size
        fileBytes.copyInto(output, offset); offset += fileBytes.size
        crlf.copyInto(output, offset); offset += crlf.size
        doubleDash.copyInto(output, offset); offset += doubleDash.size
        boundaryBytes.copyInto(output, offset); offset += boundaryBytes.size
        doubleDash.copyInto(output, offset); offset += doubleDash.size
        crlf.copyInto(output, offset)
        return output
    }
}
