package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PropertyFeatureDto(
    val featureName: String,
    val featureValue: String
)
