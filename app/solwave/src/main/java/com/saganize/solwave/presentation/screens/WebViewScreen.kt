package com.saganize.solwave.presentation.screens

import android.R
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.models.Response.Companion.genericErrorMsg
import com.saganize.solwave.presentation.WebViewInterface
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.saganize.solwave.ui.viewmodel.model.WalletProvider


// TODO: test if webview opened twice in same session, probably close on back button should resolve
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(viewModel: SolwaveViewModel? = null) {

    val state = viewModel?.state?.value ?: throw IllegalStateException(genericErrorMsg)

    BackHandler(enabled = true) {
        viewModel.onNav(SolwaveNavEvents.CloseWebViewScreen)
    }


    // TODO: find a better way to log these errors
    lateinit var progressBar: ProgressBar

    val context = LocalContext.current
    progressBar = remember { ProgressBar(context) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()

/*
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            progressBar.visibility = View.VISIBLE
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            progressBar.visibility = View.GONE
                            super.onPageFinished(view, url)
                        }
                    }
*/


                    settings.javaScriptEnabled = true

                    state.url?.let {
                        loadUrl(state.url)
                    }

                    addJavascriptInterface(
                        WebViewInterface(
                            viewModel,
                            it,
                            state.transactionParams,
                            // TODO: save email id too
                            onWalletReceived = { _, publicKey ->
                                viewModel.onEvent(
                                    SolwaveEvents.SaveWalletFromWebview(
                                        WalletProvider.Saganize,
                                        publicKey
                                    )
                                )
                            },
                            onToast = { message, shouldEndWebView ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (shouldEndWebView) {
                                    viewModel.onNav(SolwaveNavEvents.CloseWebViewScreen)
                                } else {
                                    viewModel.onEvent(SolwaveEvents.WalletError(message))
                                }
                            },
                            onClosed = { success, failure ->
                                viewModel.onNav(SolwaveNavEvents.CloseWebViewScreen)
                            }
                        ),
                        "Solwave"
                    )
                }
            }
        ) { webView ->

//            webView.webViewClient = object : WebViewClient() {
//
//                // TODO: webview listeners
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    // Do something when the page finishes loading
//                    Log.d("WebView", "your current url when webpage loading.. finish $url")
//
//                    // Check if the URL is not null and contains a query string
//                    if (url != null && url.contains("?")) {
//                        val uri = Uri.parse(url)
//
//                        val queryParameterNames = uri.queryParameterNames
//
//                        // Iterate through the parameters and their values
//                        for (paramName in queryParameterNames) {
//                            val paramValue = uri.getQueryParameter(paramName)
//                            Log.d("WebView", "Parameter: $paramName = $paramValue")
//                        }
//                    }
//
//                    when (state.screen) {
//                        Screens.SignupScreen -> viewModel.onAuthEvent(
//                            SolwaveAuthNavEvents.OnCreateUserDone(null)
//                        )
//
//                        Screens.LoginScreen -> viewModel.onAuthEvent(
//                            SolwaveAuthNavEvents.OnLoginDone(null)
//                        )
//
//                        is Screens.PayScreen -> viewModel.onAuthEvent(
//                            SolwaveAuthNavEvents.OnTransactionDone(null)
//                        )
//
//                        else -> {}
//                    }
//                }
//
//                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
//                    super.onPageStarted(view, url, favicon)
//                    Log.d("WebView", "your current url when webpage loading..$url")
//                }
//
//                override fun onLoadResource(view: WebView?, url: String?) {
//                    // TODO Auto-generated method stub
//                    super.onLoadResource(view, url)
//                }
//
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest?
//                ): Boolean {
//                    Log.d(
//                        "WebView",
//                        "your current url when webpage loading.. finish${request?.url}"
//                    )
//
//                    // Get the URL of the requested page
//                    val url = request?.url?.toString()
//
//                    // Check if the URL is not null and contains a query string
//                    if (url != null && url.contains("?")) {
//                        val uri = Uri.parse(url)
//
//                        val queryParameterNames = uri.queryParameterNames
//
//                        // Iterate through the parameters and their values
//                        for (paramName in queryParameterNames) {
//                            val paramValue = uri.getQueryParameter(paramName)
//                            Log.d("WebView", "Parameter: $paramName = $paramValue")
//                        }
//                    }
//
//                    return super.shouldOverrideUrlLoading(view, request)
//                }
//
//                override fun onReceivedError(
//                    view: WebView?,
//                    request: WebResourceRequest?,
//                    error: WebResourceError?
//                ) {
//                    super.onReceivedError(view, request, error)
//
//                    Log.d("WebView", "onReceivedError: $error")
//                }
//            }
        }

        // top icon to close
        Row(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(
                onClick = { viewModel.onNav(SolwaveNavEvents.CloseWebViewScreen) }
            ) {
                Image(
                    painter = painterResource(id = com.saganize.solwave.R.drawable.ic_wv_back),
                    contentDescription = null
                )
            }
        }

    }
}
