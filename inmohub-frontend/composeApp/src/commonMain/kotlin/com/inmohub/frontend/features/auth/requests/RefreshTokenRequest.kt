package com.inmohub.frontend.features.auth.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
  val refreshToken: String?
){}
