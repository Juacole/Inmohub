package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PropertyPhotoDto(
    val id: String,
    val photoUrl: String,
    val isPrimary: Boolean = false
)