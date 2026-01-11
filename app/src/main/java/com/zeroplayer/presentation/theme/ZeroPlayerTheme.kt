package com.zeroplayer.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ZeroPlayerTheme(
    content: @Composable () -> Unit,
) {
    // Use a clean system sans-serif stack (no bundled font files required).
    val fontFamily = FontFamily.SansSerif

    val typography = Typography(
        titleLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
        titleMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        bodyMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    )

    MaterialTheme(
        typography = typography,
    ) {
        Surface {
            content()
        }
    }
}

