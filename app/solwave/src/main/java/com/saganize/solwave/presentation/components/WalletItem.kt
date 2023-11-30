package com.saganize.solwave.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saganize.solwave.core.util.isAppInstalled
import com.saganize.solwave.ui.theme.GrayDisabled
import com.saganize.solwave.ui.theme.SaganizeBlue
import com.saganize.solwave.ui.theme.medium
import com.saganize.solwave.ui.theme.mediumEmphasis
import com.saganize.solwave.ui.theme.semiBold

@Composable
fun WalletItem(
    isSelected: Boolean,
    iconPainter: Painter,
    name: String,
    key: String?,
    walletPackage: String,
    modifier: Modifier = Modifier,
    onConnectClick: () -> Unit,
) {
    val context = LocalContext.current


    Column {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                color = Color.White,
                style = MaterialTheme.typography.body1.semiBold
            )

            TextButton(onClick = {
                if (context.isAppInstalled(walletPackage)) {
                    onConnectClick()
                }/* else {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$walletPackage")
                )
                context.startActivity(intent)
            }*/
            }) {

                if (isSelected){
                    Text(
                        text = "SELECTED",
                        style = MaterialTheme.typography.overline.semiBold,
                        color = SaganizeBlue,
                    )
                }else{
                    Text(
                        text = "SELECT",
                        color = if (context.isAppInstalled(walletPackage)) MaterialTheme.colors.onBackground else Color.Gray,
                        style = MaterialTheme.typography.overline.medium,
                    )
                }
            }
        }

        key?.let {
            if (isSelected){
                it.split("/")[0].let {key->
                    Text(
                        text = key.take(6) + "......." + key.takeLast(6),
                        style = MaterialTheme.typography.caption,
                        color = GrayDisabled.mediumEmphasis,
                        fontSize = 10.sp
                    )
                }
            }
        }

    }

}