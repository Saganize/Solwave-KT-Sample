package com.saganize.solwave.ui.viewmodel.events

sealed class SolwaveNavEvents {
    object GoToLoadingScreen : SolwaveNavEvents()
    object GoToLoginScreen : SolwaveNavEvents()
    object GoToNoAccountScreen : SolwaveNavEvents()
    object GoToSignupScreen : SolwaveNavEvents()
    object GoToWallatScreen : SolwaveNavEvents()
    object GoToPayScreen : SolwaveNavEvents()
    object GoToNoFundsScreen : SolwaveNavEvents()
    object CloseWebViewScreen : SolwaveNavEvents()
    object GoToTransactionDoneScreen : SolwaveNavEvents()
    object GoToTransactionFailedScreen : SolwaveNavEvents()
    object GoToTransactionErrorScreen : SolwaveNavEvents()
    object CloseFundsScreen : SolwaveNavEvents()
}