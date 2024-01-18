package com.saganize.solwave.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.R
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.common.componenets.ButtonPrimary
import com.saganize.solwave.ui.theme.Dimensions
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.interFamily
import com.saganize.solwave.ui.theme.poppins
import com.saganize.solwave.ui.theme.rubikFamily

@Composable
fun NoAccountScreen(viewModel: SolwaveViewModel? = null) {
//    BackHandler(enabled = true){}
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .paint(
                painterResource(id = R.drawable.colored_background),
                contentScale = ContentScale.FillBounds
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppNameBar()
        Image(
            modifier = Modifier.padding(top = 60.dp, bottom = 24.dp),
            painter = painterResource(id = R.drawable.ic_saga),
            contentDescription = null
        )
        Text(
            text = "One account for secure transactions",
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
            text = "Saganize becomes a secure wallet that store \n" +
                    "all the funds for easy and safe transactions.",
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = poppins,
                fontWeight = FontWeight(300),
                color = Color(0xBFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(110.dp))

        ButtonPrimary(
            onClick = { viewModel?.onNav(SolwaveNavEvents.GoToSignupScreen) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create new account",
                style = MaterialTheme.typography.button.bold,
                color = MaterialTheme.colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.MediumPadding.dp))

        Row {
            Text(
                text = "Already have an account? ",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = interFamily,
                    fontWeight = FontWeight(300),
                    color = Color(0x80F9F9F9),
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures {
                        viewModel?.onNav(
                            SolwaveNavEvents.GoToLoginScreen
                        )
                    }
                },
                text = " LOGIN",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            )
        }


        Spacer(modifier = Modifier.height(52.dp))
    }
}


@Preview
@Composable
fun NoAccountScreenPreview() {
    NoAccountScreen()
}