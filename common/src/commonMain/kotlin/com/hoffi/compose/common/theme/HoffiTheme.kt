package com.hoffi.compose.common.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.accorddesk.frontend.ui.theme.HoffiThemeDarkColorPalette
import com.accorddesk.frontend.ui.theme.HoffiThemeDarkTypography
import com.accorddesk.frontend.ui.theme.HoffiThemeLightColorPalette
import com.accorddesk.frontend.ui.theme.HoffiThemeLightTypography

@Composable
fun HoffiMaterialTheme(isDarkTheme: MutableState<Boolean>, content: @Composable () -> Unit) {
    if (isDarkTheme.value) {
        MaterialTheme(
            colors = HoffiThemeDarkColorPalette(),
            typography = HoffiThemeDarkTypography()
        ) {
            content()
        }
    } else {
        MaterialTheme(
            colors = HoffiThemeLightColorPalette(),
            typography = HoffiThemeLightTypography()
        ) {
            content()
        }
    }
}
