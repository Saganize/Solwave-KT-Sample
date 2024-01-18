package com.saganize.solwave.ui.viewmodel.events

import com.google.firebase.auth.FirebaseUser
import com.solana.core.Transaction

sealed class SolwaveAuthNavEvents {
    data class InitCreateUser(val user: FirebaseUser?) : SolwaveAuthNavEvents()
    data class OnCreateUserDone(val key: String?) : SolwaveAuthNavEvents()

    data class InitiateLogin(val user: FirebaseUser?) : SolwaveAuthNavEvents()
    data class OnLoginDone(val key: String?) : SolwaveAuthNavEvents()

    data class InitiateTransaction(val user: Transaction?) : SolwaveAuthNavEvents()
    data class OnTransactionDone(val key: String?) : SolwaveAuthNavEvents()
}
