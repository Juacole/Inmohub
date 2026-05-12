package com.inmohub.frontend.features.property.domain

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String? = null,
    val title: String,
    val description: String,
    val price: Double,
    val address: String,
    val areaM2: Double,
    val status: String,
    val ownerId: String
)