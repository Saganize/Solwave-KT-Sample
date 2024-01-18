package com.saganize.solwave

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.GsonBuilder
import com.saganize.solwave.core.di.SolwaveAppModuleImpl
import com.saganize.solwave.core.di.TAG
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.saganize.solwave.core.util.viewModelFactory
import com.saganize.solwave.presentation.components.SOL_PRIVET_KEY
import com.saganize.solwave.presentation.components.decryptData
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.solana.core.Transaction


class SolwaveActivity : ComponentActivity() {

    lateinit var viewModel: SolwaveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        val start = intent.getStringExtra("start") ?: ""

        val apiKey =
            intent.getStringExtra("apiKey") ?: throw IllegalStateException("apiKey is null")
        val transactionString = intent.getStringExtra("transaction")
            ?: if (start == "pay"){
                intent.getStringExtra("transaction") ?: throw IllegalStateException("transaction is null")
            } else {
                ""
            }

        val transaction = if(transactionString.isNotEmpty()) {
            GsonBuilder().create().fromJson(transactionString, Transaction::class.java)
        } else {
            Transaction()
        }

        setContent {

            val context = LocalContext.current

            val module = SolwaveAppModuleImpl(this, apiKey)
            viewModel = viewModel(
                factory = viewModelFactory {
                    SolwaveViewModel(
                        module.databaseRepository,
                        module.apiRepo,
                        module.datastoreRepository,
                        start,
                        transaction,
                        apiKey,
                        context
                    )
                },
            )

            SolwaveScreen(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == Intent.ACTION_VIEW) {

            val uri = intent.data

            Log.d(TAG, uri.toString())

            val nonce = uri?.getQueryParameter("nonce") ?: ""
            val data = uri?.getQueryParameter("data") ?: ""

            uri?.getQueryParameter("solflare_encryption_public_key")?.let {
                viewModel.onEvent(
                    SolwaveEvents.SaveWallet(
                        WalletProvider.Solflare,
                        decryptData(it, SOL_PRIVET_KEY, nonce, data)
                    )
                )
            }

            uri?.getQueryParameter("phantom_encryption_public_key")?.let {
                viewModel.onEvent(
                    SolwaveEvents.SaveWallet(
                        WalletProvider.Phantom,
                        decryptData(it, SOL_PRIVET_KEY, nonce, data)
                    )
                )
            }


            if (
                uri?.getQueryParameter("solflare_encryption_public_key").isNullOrEmpty() &&
                uri?.getQueryParameter("phantom_encryption_public_key").isNullOrEmpty()
            ) {
                uri?.getQueryParameter("data")?.let {
                    viewModel.onEvent(SolwaveEvents.DecryptTransactionResult(it, nonce))
                }
            }

            uri?.getQueryParameter("errorMessage")?.let {
                viewModel.onEvent(SolwaveEvents.WalletError(it))
            }

        }
    }
}

