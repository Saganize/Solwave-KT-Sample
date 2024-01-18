package com.saganize.solwave.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.GsonBuilder
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.models.Response.Companion.genericErrorMsg
import com.saganize.solwave.core.util.stringify
import com.saganize.solwave.domain.model.TransactionSerializer
import com.saganize.solwave.domain.model.TransactionParams
import com.saganize.solwave.domain.model.TransactionParamsStringHolder
import com.saganize.solwave.domain.model.TransactionPayloadStringHolder
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.saganize.solwave.ui.viewmodel.model.Screens
import com.solana.core.Transaction


class WebViewInterface(
    private val viewmodel: SolwaveViewModel,
    private val context: Context,
    private val transactionParams: TransactionParams,
    private val onWalletReceived: (emailId: String, publicKey: String) -> Unit,
    private val onToast: (message: String, shouldEndWebView: Boolean) -> Unit,
    private val onClosed: (success: Long, failure: String) -> Unit,
) {
    @JavascriptInterface
    fun getEmailAndPublicKey(emailId: String, publicKey: String) {
        Log.d("WebViewInterface", "getEmailAndPublicKey: $emailId, $publicKey")
        onWalletReceived(emailId, publicKey)
    }

    @JavascriptInterface
    fun copyPublicKey(publicKey: String) {
        Log.d("WebViewInterface", "copyPublicKey: $publicKey")
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Public key", publicKey)
        clipboardManager?.setPrimaryClip(clip) ?: Log.e(
            "Clipboard",
            "Failed to access the clipboard."
        )
    }

    @JavascriptInterface
    fun showToast(
        message: String = genericErrorMsg,
        shouldEndWebView: Boolean = false
    ) {
        Log.d("WebViewInterface", "showToast: $message")
        onToast(message, shouldEndWebView)
    }

    @JavascriptInterface
    fun getTransaction(): String {
        Log.d("WebViewInterface", "getTransaction: ${transactionParams.data.transaction}")

        val params = TransactionParamsStringHolder(
            type = transactionParams.type,
            status = transactionParams.status,
            data = TransactionPayloadStringHolder(
                transaction = transactionParams.data.transaction.stringify(),
                from = transactionParams.data.from,
                to = transactionParams.data.to,
                lamports = transactionParams.data.lamports,
                fees = transactionParams.data.fees,
            ),
        )

        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        gsonBuilder.registerTypeAdapter(Transaction::class.java, TransactionSerializer())
        val gsonString = gson.toJson(params)
        Log.d("WebViewInterface", "getTransaction: gson: $gsonString")

        return gsonString
    }

    @JavascriptInterface
    fun transactionComplete(signature: String, idempotencyId: String) {
        Log.d("WebViewInterface", "transactionCompleted: signature: $signature, idempotencyId: $idempotencyId")
        // TODO:
        viewmodel.onEvent(SolwaveEvents.TransactionDone(signature))
    }

    @JavascriptInterface
    fun close(event: Long, message: String) {
        Log.d("WebViewInterface", "close: WebView closed, event: ${getWebViewClosingEvent(event).name}, message: $message")
        onClosed(event, message)
        // TODO:

        when(getWebViewClosingEvent(event)){
            WebViewClosingEvents.UserCreationSuccess -> TODO()
            WebViewClosingEvents.LoginSuccessful -> TODO()
            WebViewClosingEvents.TransactionCompleted -> TODO()
            WebViewClosingEvents.UserCreationFailure -> TODO()
            WebViewClosingEvents.LoginFailure -> TODO()
            WebViewClosingEvents.ServerError -> TODO()
            WebViewClosingEvents.TransactionFailed -> TODO()
        }

    }
}

enum class WebViewClosingEvents {
    UserCreationSuccess,
    LoginSuccessful,
    TransactionCompleted,
    UserCreationFailure,
    LoginFailure,
    ServerError,
    TransactionFailed
}

fun getWebViewClosingEvent(ordinal: Long): WebViewClosingEvents {
    return when (ordinal) {
        0L -> WebViewClosingEvents.UserCreationSuccess
        1L -> WebViewClosingEvents.LoginSuccessful
        2L -> WebViewClosingEvents.TransactionCompleted
        3L -> WebViewClosingEvents.UserCreationFailure
        4L -> WebViewClosingEvents.LoginFailure
        5L -> WebViewClosingEvents.ServerError
        6L -> WebViewClosingEvents.TransactionFailed
        else -> throw IllegalStateException("Invalid event")
    }
}

// Dhiraj#123456789