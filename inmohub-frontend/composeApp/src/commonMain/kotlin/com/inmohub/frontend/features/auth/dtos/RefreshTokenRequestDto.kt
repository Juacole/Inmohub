package com.inmohub.frontend.features.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequestDto(
  val refreshToken: String?
){}
