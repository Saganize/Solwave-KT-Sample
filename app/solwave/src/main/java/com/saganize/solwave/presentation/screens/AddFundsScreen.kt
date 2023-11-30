package com.saganize.solwave.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.R
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.util.displayWallet
import com.saganize.solwave.core.util.showToast
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.common.componenets.ButtonPrimary
import com.saganize.solwave.ui.theme.CardBackground
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.poppins
import com.saganize.solwave.ui.theme.rubikFamily

@Composable
fun AddFundsScreen(viewModel: SolwaveViewModel? = null) {

    val state = viewModel!!.state.value

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppNameBar()
        Image(
            modifier = Modifier.padding(top = 60.dp, bottom = 24.dp),
            painter = painterResource(id = R.drawable.no_funds),
            contentDescription = null
        )

        Text(
            text = "Add funds to wallet",
            style = TextStyle(
                fontSize = 28.sp,
                lineHeight = 30.sp,
                fontFamily = rubikFamily,
                fontWeight = FontWeight(600),
                color = Color(0xFFF9F9F9),

                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your wallet is low on balance. Add some funds and try again later.",
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = poppins,
                fontWeight = FontWeight(300),
                color = Color(0xBFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )

        Box(
            modifier = Modifier
                .padding(top = 52.dp, bottom = 80.dp)
                .background(CardBackground, RoundedCornerShape(100))
                .clip(RoundedCornerShape(100))
                .height(60.dp)
                .wrapContentWidth(),
            contentAlignment = Alignment.Center
        ) {

            Row(
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.wallet!!.key.split("/")[0].displayWallet(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 32.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFF9F9F9),
                    )
                )

                Spacer(modifier = Modifier.size(16.dp))

                val context = LocalContext.current


                IconButton(onClick = {
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                        ClipData.newPlainText("label", state.wallet.key.split("/")[0])
                    )
                }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.copy),
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = null
                    )
                }
            }

        }

        val context = LocalContext.current


        ButtonPrimary(
            onClick = {

                viewModel.hasFunds()

                // TODO: fix this
                val hasFundsNow = state.funds?.toDouble() == state.transactionParams.data.lamports

                if (hasFundsNow){
                    viewModel.onNav(SolwaveNavEvents.GoToPayScreen)
                }else{
                    showToast(context, "still has no funds.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Close",
                style = MaterialTheme.typography.button.bold,
                color = MaterialTheme.colors.onBackground
            )
        }


        Spacer(modifier = Modifier.height(52.dp))
    }

}