package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PropertyPatchRequest(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val areaM2: Double? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val status: String? = null,
    val features: List<PropertyFeatureDto>? = null
)
