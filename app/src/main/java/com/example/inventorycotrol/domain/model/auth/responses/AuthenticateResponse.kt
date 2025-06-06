package com.example.inventorycotrol.domain.model.auth.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateResponse(
    @SerialName("user_id")
    val userId: String,
)