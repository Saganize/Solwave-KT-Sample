package com.saganize.solwave

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.saganize.solwave.core.util.ConnectionState
import com.saganize.solwave.core.util.connectivityState
import com.saganize.solwave.presentation.screens.AddFundsScreen
import com.saganize.solwave.presentation.screens.LoadingScreen
import com.saganize.solwave.presentation.screens.LoginScreen
import com.saganize.solwave.presentation.screens.NoAccountScreen
import com.saganize.solwave.presentation.screens.PayScreen
import com.saganize.solwave.presentation.screens.SignupScreen
import com.saganize.solwave.presentation.WalletScreen.WalletScreen
import com.saganize.solwave.presentation.screens.TransactionDoneScreen
import com.saganize.solwave.presentation.screens.TransactionErrorScreen
import com.saganize.solwave.presentation.screens.TransactionFailedScreen
import com.saganize.solwave.presentation.screens.WebViewScreen
import com.saganize.solwave.ui.common.componenets.AppNameBar
import com.saganize.solwave.ui.theme.BackBlur
import com.saganize.solwave.ui.theme.BackgroundBlack
import com.saganize.solwave.ui.theme.CardBackground
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.SolwaveTheme
import com.saganize.solwave.ui.theme.mediumEmphasis
import com.saganize.solwave.ui.theme.semiBold
import com.saganize.solwave.ui.viewmodel.model.Screens
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun SolwaveScreen(viewModel: SolwaveViewModel) {

    val state = viewModel.state.value

    val context = LocalContext.current as Activity

    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Expanded)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)


    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available

    LaunchedEffect(isConnected) {
        if (!isConnected){
            viewModel.onEvent(SolwaveEvents.WalletError("No internet connection."))
        }
    }

    LaunchedEffect(bottomSheetState.isCollapsed) {
        if (bottomSheetState.isCollapsed) {
            context.finish()
        }
    }

    BottomSheetScaffold(
        backgroundColor = BackBlur,
        sheetGesturesEnabled = state.screen == Screens.TransactionErrorScreen || state.screen == Screens.TransactionDoneScreen || state.screen == Screens.TransactionFailedScreen,
        drawerShape = RoundedCornerShape(topEnd = 18.dp, topStart = 18.dp),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        contentColor = Color.Transparent,
        drawerBackgroundColor = Color.Transparent,
        drawerContentColor = Color.Transparent,
        drawerScrimColor = Color.Transparent,
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetContent = {
            SolwaveTheme {
                AnimatedContent(
                    targetState = state.screen,
                    label = "",
                ) { state ->

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(
                                BackgroundBlack,
                                RoundedCornerShape(topEnd = 18.dp, topStart = 18.dp)
                            ),
                    ) {
                        when (state) {
                            Screens.NoAccountScreen -> NoAccountScreen(viewModel = viewModel)
                            Screens.SignupScreen -> SignupScreen(viewModel = viewModel)
                            Screens.LoginScreen -> LoginScreen(viewModel = viewModel)
                            Screens.WallatScreen -> WalletScreen(viewModel = viewModel)

                            is Screens.PayScreen -> PayScreen(
                                viewModel = viewModel,
                                transactionParams = state.transactionParams,
                            )

                            Screens.LoadingScreen -> LoadingScreen(viewModel = viewModel)
                            Screens.NoFundsScreen -> AddFundsScreen(viewModel = viewModel)
                            Screens.TransactionDoneScreen -> TransactionDoneScreen(viewModel = viewModel)
                            Screens.TransactionErrorScreen -> TransactionErrorScreen(viewModel = viewModel)
                            Screens.TransactionFailedScreen -> TransactionFailedScreen(viewModel = viewModel)
                            else -> {

                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(
                                        R.raw.loading_animation
                                    )
                                )

                                var iterations by remember {
                                    mutableIntStateOf(LottieConstants.IterateForever)
                                }

                                val progress by animateLottieCompositionAsState(
                                    composition = composition,
                                    iterations = iterations,
                                )

                                Column(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AppNameBar()

                                    Spacer(modifier = Modifier.padding(24.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        LottieAnimation(
                                            composition = composition,
                                            progress = { progress },
                                            modifier = Modifier.size(100.dp),
                                            contentScale = ContentScale.FillBounds,

                                            )

                                    }

                                    Spacer(modifier = Modifier.padding(50.dp))

                                }


                                // TODO: maybe add loading screen??
                                Spacer(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .background(Color.Transparent)
                                )
                            }
                        }

                    }
                }
            }
        }
    ) {}

    AnimatedVisibility(visible = !state.url.isNullOrEmpty(), enter = fadeIn(), exit = fadeOut()) {
        WebViewScreen(viewModel = viewModel)
    }
}
