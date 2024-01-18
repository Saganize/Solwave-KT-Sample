package com.saganize.solwave.presentation.WalletScreen


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.R
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.util.display
import com.saganize.solwave.presentation.components.SOL_PUBLIC_KEY
import com.saganize.solwave.presentation.components.SolWallatesLauncher
import com.saganize.solwave.presentation.components.WalletItem
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.theme.CardBackground
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.SaganizeBlue
import com.saganize.solwave.ui.theme.SolwaveTheme
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.medium
import com.saganize.solwave.ui.theme.mediumEmphasis
import com.saganize.solwave.ui.theme.semiBold
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.solana.core.PublicKey


@Composable
fun WalletScreen(
    viewModel: SolwaveViewModel? = null
) {
//    BackHandler(enabled = true){}
    val state = viewModel!!.state.value

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AppNameBar()

        Spacer(modifier = Modifier.padding(12.dp))

        Text(
            text = "RECOMMENDED",
            style = MaterialTheme.typography.overline.semiBold,
            color = GrayDisabled.mediumEmphasis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth().clickable {
            viewModel.onEvent(
                SolwaveEvents.SelectWallat(
                    WalletProvider.Saganize
                )
            )
        }, backgroundColor = CardBackground) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(22.dp)
                                .padding(2.dp),
                            painter = painterResource(id = R.drawable.ic_saga),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    append("Saga")
                                    withStyle(
                                        style = MaterialTheme.typography.body1.medium.toSpanStyle()
                                    ) {
                                        append("nize")
                                    }
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.body1.bold
                            )
                        }
                    }

                    if (state.wallet?.walletProvider == WalletProvider.Saganize) {
                        Text(
                            text = "SELECTED",
                            style = MaterialTheme.typography.overline.semiBold,
                            color = SaganizeBlue,
                        )
                    } else {
                        Text(
                            text = "SELECT",
                            style = MaterialTheme.typography.overline.medium,
                            color = MaterialTheme.colors.onBackground,
                        )
                    }

                }
                if (state.wallet?.walletProvider == WalletProvider.Saganize){

                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = PublicKey(state.wallet.key.split("/")[0]).display(),
                        style = MaterialTheme.typography.caption,
                        color = GrayDisabled.mediumEmphasis,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        Text(
            text = "OTHER WALLETS",
            style = MaterialTheme.typography.overline.semiBold,
            color = GrayDisabled.mediumEmphasis
        )

        Spacer(modifier = Modifier.padding(8.dp))

        val phantomOpen = SolWallatesLauncher(
            "https://phantom.app/ul/v1/connect?" +
                    "app_url=https://saganize.com" +
                    "&dapp_encryption_public_key=$SOL_PUBLIC_KEY" +
                    "&cluster=devnet" +
                    "&redirect_link=app%3A%2F%2Fsolwave.com%2Fdeeplink"
        )
        val solflareOpen = SolWallatesLauncher(
            "https://solflare.com/ul/v1/connect?" +
                    "app_url=https://saganize.com" +
                    "&dapp_encryption_public_key=$SOL_PUBLIC_KEY" +
                    "&cluster=devnet" +
                    "&redirect_link=app%3A%2F%2Fsolwave.com%2Fdeeplink"
        )

        Card(backgroundColor = CardBackground, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                WalletItem(
                    isSelected = state.wallet?.walletProvider == WalletProvider.Phantom,
                    iconPainter = painterResource(id = R.drawable.ic_phantom),
                    name = "Phantom",
                    key = state.wallet?.key,
                    walletPackage = "app.phantom",
                    onConnectClick = { viewModel.onEvent(SolwaveEvents.SelectWallat(WalletProvider.Phantom, phantomOpen))  }
                )

                WalletItem(
                    isSelected = state.wallet?.walletProvider == WalletProvider.Solflare,
                    iconPainter = painterResource(id = R.drawable.ic_solflare),
                    name = "Solflare",
                    key = state.wallet?.key,
                    walletPackage = "com.solflare.mobile",
                    onConnectClick = { viewModel.onEvent(SolwaveEvents.SelectWallat(WalletProvider.Solflare, solflareOpen)) }
                )


            }
        }

        Spacer(modifier = Modifier.padding(66.dp))

        /*
            ButtonPrimary(onClick = { */
                /*onPayClick()*//*
            }, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = buildAnnotatedString {
                    append("Pay amount")
                    withStyle(
                        style = MaterialTheme.typography.button.light.toSpanStyle()
                    ) {
                        append(" SOL")
                    }
                },
                style = MaterialTheme.typography.button.bold,
                color = MaterialTheme.colors.onBackground
            )
        }
        */
    }
}



@Preview
@Composable
private fun ChooseWalletPreview() {
    SolwaveTheme {
        WalletScreen(viewModel = null)
    }
}
