package com.saganize.solwave.data.local

import com.saganize.solwave.domain.model.WalletEntity
import com.saganize.solwave.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl(private val dao: SolwaveDao): DatabaseRepository {
    override suspend fun saveWallet(wallet: WalletEntity) {
        dao.saveWallet(wallet)
    }

    override suspend fun getWallets(): List<WalletEntity>? {
        if (dao.getWallet().isNullOrEmpty()) return null
        return dao.getWallet()
    }
}