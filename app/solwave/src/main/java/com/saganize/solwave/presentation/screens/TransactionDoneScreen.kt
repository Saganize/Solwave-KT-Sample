package com.saganize.solwave.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.saganize.solwave.R
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.presentation.components.shareLink
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.common.componenets.ButtonPrimary
import com.saganize.solwave.ui.theme.CardBackground
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.interFamily
import com.saganize.solwave.ui.theme.mediumEmphasis
import com.saganize.solwave.ui.theme.semiBold


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionDoneScreen(viewModel: SolwaveViewModel? = null) {
    val state = viewModel!!.state.value

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppNameBar()

            Surface(color = CardBackground, shape = CircleShape, onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.padding(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.FillBounds,
                    painter = painterResource(id = R.drawable.ic_transaction_done),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Transaction Complete",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6.semiBold,
                color = MaterialTheme.colors.onBackground,
                fontSize = 22.sp
            )
            Text(
                text = "Check status on Solscan from below",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onBackground.mediumEmphasis
            )
        }

        Spacer(modifier = Modifier.height(88.dp))

        // TODO: change devnet
        val context = LocalContext.current
        val url = "https://explorer.solana.com/tx/${state.transactionId}?cluster=devnet"

        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 27.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ButtonPrimary(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(5f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF15171E)),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = "View Transaction",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = interFamily,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFF9F9F9),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            IconButton(
                onClick = {context.shareLink(url)},
                modifier = Modifier
                    .size(50.dp)
                    .weight(1f)
                    .padding(start = 15.dp)
                    .background(Color(0xFF15171E), RoundedCornerShape(100))
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_share), contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(58.dp))

    }
}

@Preview
@Composable
fun TransactionDonePreview() {
    TransactionDoneScreen()
}