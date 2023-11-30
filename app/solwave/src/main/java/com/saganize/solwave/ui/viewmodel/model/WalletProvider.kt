package com.saganize.solwave.ui.viewmodel.model

sealed class WalletProvider {
    object Saganize : WalletProvider()
    object Phantom : WalletProvider()
    object Solflare : WalletProvider()
}