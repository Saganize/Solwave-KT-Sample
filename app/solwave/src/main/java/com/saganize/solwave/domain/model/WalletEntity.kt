package com.saganize.solwave.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.saganize.solwave.ui.viewmodel.model.WalletInfo


@Entity(tableName = "walletEntity")
data class WalletEntity(
    @PrimaryKey val name: String,
    val key: String,
) {
    fun toWallatInfo() = WalletInfo(
        walletProvider = when (name) {
            "Solflare" -> WalletProvider.Solflare
            "Phantom" -> WalletProvider.Phantom
            else -> WalletProvider.Saganize
        },
        key = key
    )
}