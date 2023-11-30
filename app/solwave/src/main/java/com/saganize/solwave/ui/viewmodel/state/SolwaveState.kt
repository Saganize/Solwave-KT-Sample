package com.saganize.solwave.ui.viewmodel.state

import com.saganize.solwave.domain.model.TransactionParams
import com.saganize.solwave.ui.viewmodel.model.Screens
import com.saganize.solwave.ui.viewmodel.model.WalletInfo

data class SolwaveState(
    val screen: Screens? = null,
    val wallet: WalletInfo? = null,
    val transactionParams: TransactionParams,
    val url: String? = null,
    val deepLink: String = "",
    val error: String = "Something went wrong please try again later.",
    val transactionId: String = "",
    val funds: Long? = null,
    )