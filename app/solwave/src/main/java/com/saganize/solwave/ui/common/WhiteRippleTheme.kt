package com.saganize.solwave.ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * This will add click effect on white buttons when darkTheme = true
 */

@Immutable
internal object WhiteRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        contentColor = MaterialTheme.colors.background,
        lightTheme = true
    )

    @Composable
    override fun rippleAlpha() = RippleTheme.defaultRippleAlpha(
        contentColor = MaterialTheme.colors.background,
        lightTheme = true
    )
}