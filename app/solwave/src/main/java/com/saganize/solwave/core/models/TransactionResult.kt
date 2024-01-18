package com.saganize.solwave.core.models

sealed class TransactionResult {
    data class Success(val transactionId: String) : TransactionResult()
    data class Error(val message: String) : TransactionResult()
}

sealed class SelectResult {
    data class Success(val publicKey: String) : SelectResult()
    data class Error(val message: String) : SelectResult()
}