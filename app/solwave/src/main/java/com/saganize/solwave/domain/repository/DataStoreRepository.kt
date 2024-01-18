package com.saganize.solwave.domain.repository

interface DataStoreRepository {
    suspend fun saveWallet(value: String)
    suspend fun getWallet(): String?
}
