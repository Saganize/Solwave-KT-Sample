package com.saganize.solwave.ui.common.componenets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.White
import com.saganize.solwave.ui.theme.bold
import com.saganize.solwave.ui.theme.interFamily
import com.saganize.solwave.ui.theme.light
import com.saganize.solwave.ui.theme.mediumEmphasis

@Composable
fun AppNameBar() {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(), horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Solwave",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = interFamily,
                fontWeight = FontWeight(400),
                color = White,
                letterSpacing = 0.29.sp,
            )
        )
        Spacer(modifier = Modifier.padding(1.dp))
        Text(
            text = buildAnnotatedString {
                append("Powered by ")
                withStyle(
                    style = MaterialTheme.typography.caption.bold.toSpanStyle()
                ) {
                    append("Saga")
                }
                withStyle(
                    style = MaterialTheme.typography.caption.toSpanStyle()
                ) {
                    append("nize")
                }
            },
            style = MaterialTheme.typography.caption.light,
            color = GrayDisabled.mediumEmphasis,
        )
    }
}

@Preview
@Composable
fun AppNameBarPreview() {
    AppNameBar()
}