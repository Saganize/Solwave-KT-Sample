package com.saganize.solwave.ui.viewmodel.model

import com.saganize.solwave.domain.model.TransactionParams

// TODO: will be put in their files later
sealed class Screens {
    object LoadingScreen : Screens()
    object LoginScreen : Screens()
    object NoAccountScreen : Screens()
    object SignupScreen : Screens()
    object WallatScreen : Screens()
    data class PayScreen(val transactionParams: TransactionParams,): Screens()
    object NoFundsScreen : Screens()
    object TransactionDoneScreen : Screens()
    object TransactionFailedScreen : Screens()
    object TransactionErrorScreen : Screens()
}