package com.hoffi.compose.showcase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


@Composable
fun ShowcaseAppEntryPoint() {
    CompositionLocalProvider() {
        ShowcaseApp()
    }
}
