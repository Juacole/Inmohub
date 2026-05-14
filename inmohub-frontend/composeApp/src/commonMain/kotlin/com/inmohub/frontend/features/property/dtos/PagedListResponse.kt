package com.inmohub.frontend.features.property.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PagedListResponse<T>(
    val content: List<T>,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 0,
    val number: Int = 0
)
