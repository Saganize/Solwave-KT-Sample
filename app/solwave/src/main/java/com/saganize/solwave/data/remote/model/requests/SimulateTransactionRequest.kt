package com.saganize.solwave.data.remote.model.requests

import com.solana.core.Transaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimulateTransactionRequest(
    @SerialName("transaction")
    val transaction: String,
    @SerialName("publicKey")
    val publicKey: String,
)