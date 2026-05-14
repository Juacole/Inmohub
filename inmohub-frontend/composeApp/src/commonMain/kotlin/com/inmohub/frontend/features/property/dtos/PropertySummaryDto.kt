package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PropertySummaryDto(
    val id: String,
    val tittle: String,
    val price: Double,
    val city: String,
    val status: String,
    val primaryPhotoUrl: String?
)
