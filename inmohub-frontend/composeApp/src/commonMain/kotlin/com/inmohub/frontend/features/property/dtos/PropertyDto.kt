package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PropertyDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val areaM2: Double,
    val address: String,
    val city: String,
    val status: String,
    val ownerId: String,
    val photos: List<String> = emptyList(),
    val features: List<PropertyFeatureDto> = emptyList()
)
