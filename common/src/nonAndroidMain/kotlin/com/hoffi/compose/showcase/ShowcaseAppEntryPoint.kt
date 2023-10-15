package com.hoffi.compose.showcase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState


@Composable
fun ShowcaseAppEntryPoint(appWindowSize: MutableState<AppWindowSize>, appWindowTitle: MutableState<String>) {
    CompositionLocalProvider() {
        ShowcaseApp(appWindowSize, appWindowTitle)
    }
}
