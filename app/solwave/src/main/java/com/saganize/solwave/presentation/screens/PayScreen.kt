package com.saganize.solwave.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.di.TAG
import com.saganize.solwave.core.util.displayWallet
import com.saganize.solwave.core.util.formatFee
import com.saganize.solwave.core.util.toSol
import com.saganize.solwave.domain.model.TransactionParams
import com.saganize.solwave.domain.model.TransactionPayload
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.saganize.solwave.presentation.components.SolWallatesLauncher
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.common.componenets.ButtonPrimary
import com.saganize.solwave.ui.theme.CardBackground
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.Green100
import com.saganize.solwave.ui.theme.Red100
import com.saganize.solwave.ui.theme.SolwaveTheme
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.light
import com.saganize.solwave.ui.theme.lowEmphasis
import com.saganize.solwave.ui.theme.medium
import com.saganize.solwave.ui.theme.mediumEmphasis
import com.saganize.solwave.ui.theme.semiBold
import com.saganize.solwave.ui.viewmodel.events.SolwaveAuthNavEvents
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.solana.core.Transaction


@Composable
fun PayScreen(
    transactionParams: TransactionParams,
    viewModel: SolwaveViewModel? = null
) {
//    BackHandler(enabled = true){}

    val state = viewModel!!.state.value
    val currentWallet = state.wallet ?: return

    val payButtonText = if (transactionParams.data.lamports != null) {
        buildAnnotatedString {
            append("Continue ${transactionParams.data.lamports.toSol()}")
            withStyle(
                style = MaterialTheme.typography.button.light.toSpanStyle()
            ) {
                append(" SOL")
            }
        }
    } else {
        buildAnnotatedString {
            withStyle(
                style = MaterialTheme.typography.button.light.toSpanStyle()
            ) {
                append("Approve")
            }
        }
    }

    Log.d(TAG+2,transactionParams.data.lamports.toString())

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AppNameBar()

        Spacer(modifier = Modifier.padding(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = CardBackground,
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colors.onBackground.lowEmphasis
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Estimated Changes",
                    style = MaterialTheme.typography.caption.medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (transactionParams.data.lamports != null){
                    Text(
                        text = buildAnnotatedString {
                            append("Send ${transactionParams.data.lamports.toSol()}")
                            withStyle(
                                style = MaterialTheme.typography.body2.light.toSpanStyle()
                            ) {
                                append("SOL")
                            }
                        },
                        style = MaterialTheme.typography.body2.semiBold,
                        color = Red100
                    )
                    Text(
                        text = "From:  ${state.wallet.key.displayWallet()}",
                        style = MaterialTheme.typography.body2.semiBold,
                        color = Green100
                    )
                    Text(
                        text = "To:  ${transactionParams.data.to?.displayWallet()}",
                        style = MaterialTheme.typography.body2.semiBold,
                        color = Green100
                    )
                }else{
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "Unable to simulate the transaction.  If you trust the app then only proceed.",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = CardBackground,
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colors.onBackground.lowEmphasis
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Network Fee: ",
                    style = MaterialTheme.typography.body2,
                    color = GrayDisabled.mediumEmphasis
                )
                Text(
                    text = buildAnnotatedString {
                        append(transactionParams.data.fees.formatFee())
                        withStyle(
                            style = MaterialTheme.typography.body2.light.toSpanStyle()
                        ) {
                            append(" SOL")
                        }
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.body2.semiBold
                )
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        viewModel.hasFunds()

        state.funds?.let {
            if (it < state.transactionParams.data.fees) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackground,
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = MaterialTheme.colors.onBackground.lowEmphasis
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "Please ensure you have sufficient funds to avoid potential transaction failure.",
                        style = MaterialTheme.typography.body2,
                        color = Red100
                    )
                }

                Spacer(modifier = Modifier.padding(12.dp))

            }
        }



        Card(backgroundColor = CardBackground, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = "pay using",
                    style = MaterialTheme.typography.caption,
                    color = GrayDisabled.mediumEmphasis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    when (state.wallet.walletProvider) {
                        WalletProvider.Saganize -> Text(
                            text = buildAnnotatedString {
                                append("Saga")
                                withStyle(
                                    style = MaterialTheme.typography.body1.medium.toSpanStyle()
                                ) {
                                    append("nize")
                                }
                            },
                            style = MaterialTheme.typography.body1.bold,
                            color = Color.White,
                        )

                        WalletProvider.Phantom -> Text(
                            text = "Phantom",
                            style = MaterialTheme.typography.body1.bold,
                            color = Color.White,
                        )

                        WalletProvider.Solflare -> Text(
                            text = "Solflare",
                            style = MaterialTheme.typography.body1.bold,
                            color = Color.White,
                        )

                        else -> {}
                    }
                }

                val context = LocalContext.current
                Text(
                    modifier = Modifier.clickable {
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                            ClipData.newPlainText("label", state.wallet.key))
                    },
                    text = state.wallet.key.displayWallet(),
                    style = MaterialTheme.typography.caption,
                    color = GrayDisabled.mediumEmphasis,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))


        var startPay by remember {
            mutableStateOf(false)
        }

        val paymentEvent = SolWallatesLauncher(state.deepLink)
        if (startPay) {
            paymentEvent()
            startPay = false
        }

        ButtonPrimary(onClick = {
            if (currentWallet.walletProvider == WalletProvider.Saganize) {

                val showNoFunds: Boolean =
                    (state.funds?.toInt() ?: 0) <= (transactionParams.data.lamports?.toInt() ?: 0)

                if(showNoFunds){ viewModel.onNav(SolwaveNavEvents.GoToNoFundsScreen) }

                else{
                    viewModel.onAuthEvent(
                        SolwaveAuthNavEvents.InitiateTransaction(state.transactionParams.data.transaction)
                    )
                }



            } else {
                viewModel.onEvent(SolwaveEvents.PayUsingWallet())
                startPay = true
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = payButtonText,
                style = MaterialTheme.typography.button.bold,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}


@Preview
@Composable
fun PayBottomSheetPreview() {
    SolwaveTheme {
        PayScreen(
            transactionParams = TransactionParams(
                data = TransactionPayload(
                    transaction = Transaction(),
                ),
            )
        )
    }
}

