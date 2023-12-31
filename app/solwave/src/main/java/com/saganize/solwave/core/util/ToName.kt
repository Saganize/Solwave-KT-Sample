package com.saganize.solwave.core.util

import com.saganize.solwave.ui.viewmodel.model.WalletProvider

internal fun WalletProvider.toName() = when (this) {
    WalletProvider.Phantom -> "Phantom"
    WalletProvider.Saganize -> "Saganize"
    WalletProvider.Solflare -> "Solflare"
}