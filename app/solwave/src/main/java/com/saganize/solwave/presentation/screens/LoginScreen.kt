package com.saganize.solwave.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.saganize.solwave.R
import com.saganize.solwave.ui.viewmodel.events.SolwaveAuthNavEvents
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.saganize.solwave.SolwaveViewModel
import com.saganize.solwave.core.util.showToast
import com.saganize.solwave.presentation.components.firebaseSignIn
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.common.componenets.ButtonPrimary
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.interFamily
import com.saganize.solwave.ui.theme.rubikFamily


@Composable
fun LoginScreen(viewModel: SolwaveViewModel? = null) {

    //back to no account screen when back btn pressed
//    BackHandler(enabled = true) {
//        viewModel?.onNav(SolwaveNavEvents.GoToNoAccountScreen)
//    }

    // TODO: change to login logic, for now same as sign up
    val context = LocalContext.current

    val firebaseSignin =
        firebaseSignIn(
            onDone = {
                viewModel?.onAuthEvent(SolwaveAuthNavEvents.InitiateLogin(it))
            },
            onErr = {
                showToast(context, it)
            }
        )



    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .paint(
                painterResource(id = R.drawable.colored_background),
                contentScale = ContentScale.FillBounds
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppNameBar()

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Login",
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontFamily = rubikFamily,
                fontWeight = FontWeight(600),
                color = Color(0xFFF9F9F9),
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import your account safe and secure",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 30.sp,
                fontFamily = interFamily,
                fontWeight = FontWeight(300),
                color = Color(0xFFF9F9F9),
            )
        )
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Connect with your email address",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = interFamily,
                fontWeight = FontWeight(500),
                color = Color(0xFFF9F9F9),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        var btnLoading by remember {
            mutableStateOf(false)
        }
        val user = FirebaseAuth.getInstance().currentUser

        ButtonPrimary(
            onClick = {
                firebaseSignin()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Box {

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Image(
                        painter = painterResource(id = R.drawable.google_ic),
                        contentDescription = null
                    )
                }

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Login with Google",
                        color = Color.Black,
                        style = MaterialTheme.typography.button.bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(240.dp))
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}