package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProperty(
    @SerialName("title") val titulo: String,
    @SerialName("description") val descripcion: String,
    @SerialName("price") val precio: Double,
    @SerialName("areaM2") val area: Double,
    @SerialName("address") val direccion: String,
    @SerialName("city") val ciudad: String = "",
    @SerialName("state") val provincia: String = "Sin especificar",
    @SerialName("country") val pais: String = "España",
    @SerialName("features") val caracteristicas: List<PropertyFeatureDto> = emptyList()
)
