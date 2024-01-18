package com.saganize.solwave.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitiateAuthResponse(
    @SerialName("userId")
    val userId: String,
    @SerialName("email")
    val email: String,
    @SerialName("authIdempotencyId")
    val idempotencyId: String,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("url")
    val url: String
)