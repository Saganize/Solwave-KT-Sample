package com.saganize.solwave.ui.viewmodel.events

import com.saganize.solwave.ui.viewmodel.model.WalletProvider

sealed class SolwaveEvents {
    data class SelectWallat(val wallat: WalletProvider, val openDeepLink: () -> Unit = {}) : SolwaveEvents()
    data class SaveWallet(val wallat: WalletProvider, val Key: String) : SolwaveEvents()
    data class SaveWalletFromWebview(val wallat: WalletProvider, val Key: String) : SolwaveEvents()
    data class PayUsingWallet(val openDL: () -> Unit = {}) : SolwaveEvents()
    data class DecryptTransactionResult(val data: String, val nonce: String) : SolwaveEvents()
    data class TransactionDone(val id: String) : SolwaveEvents()
    data class WalletError(val error: String) : SolwaveEvents()
    data class TransactionFailed(val error: String) : SolwaveEvents()
    object NoNet : SolwaveEvents()
}