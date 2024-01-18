package com.saganize.solwave

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.google.firebase.FirebaseApp
import com.google.gson.GsonBuilder
import com.saganize.solwave.core.models.SelectResult
import com.saganize.solwave.core.models.TransactionResult
import com.saganize.solwave.core.util.showToast
import com.saganize.solwave.domain.model.TransactionSerializer
import com.solana.core.Transaction


lateinit var onComplete: (it: TransactionResult) -> Unit
lateinit var onSelect: (it: SelectResult) -> Unit

@Keep
class Solwave(private val context: Context, private val apiKey: String) {

    fun selectWallet(
        onSelect: (it: SelectResult) -> Unit = {}
    ) {
        try {
            FirebaseApp.getInstance()
        } catch (e: Exception) {
            showToast(context, "Firebase is not initialized. Please initialize Firebase before using the library.")
            return
        }

        val intent = Intent(context, SolwaveActivity::class.java).apply {
            putExtra("start", "select")
            putExtra("apiKey", apiKey)
        }

        com.saganize.solwave.onSelect = onSelect

        context.startActivity(intent)
    }

    fun performTransaction(
        transaction: Transaction,
        onComplete: (it: TransactionResult) -> Unit = {},
    ) {
        try {
            FirebaseApp.getInstance()
        } catch (e: Exception) {
            showToast(context, "Firebase is not initialized. Please initialize Firebase before using the library.")
            return
        }

        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        gsonBuilder.registerTypeAdapter(Transaction::class.java, TransactionSerializer())
        val transactionString = gson.toJson(transaction)


        val intent = Intent(context, SolwaveActivity::class.java).apply {
            putExtra("start", "pay")
            putExtra("apiKey", apiKey)
            putExtra("transaction", transactionString)
        }

        com.saganize.solwave.onComplete = onComplete

        context.startActivity(intent)
    }
}
