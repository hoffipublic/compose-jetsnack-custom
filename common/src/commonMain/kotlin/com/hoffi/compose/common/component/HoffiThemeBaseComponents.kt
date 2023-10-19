package com.hoffi.compose.common.component

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.accorddesk.frontend.ui.theme.themeBasedAccentColorOpposite

@Composable
fun BoxScope.VerticalScrollbar(verticalScrollState: ScrollState, modifier: Modifier = Modifier) {
    androidx.compose.foundation.VerticalScrollbar(
        adapter = rememberScrollbarAdapter(verticalScrollState),
        modifier = modifier.align(Alignment.CenterEnd),
        style = LocalScrollbarStyle.current.copy(
            unhoverColor = themeBasedAccentColorOpposite().copy(alpha = 0.25f),
            hoverColor = themeBasedAccentColorOpposite().copy(alpha = 0.62f)
        )
    )
}
@Composable
fun BoxScope.HorizontalScrollbar(horizontalScrollState: ScrollState, modifier: Modifier = Modifier) {
    androidx.compose.foundation.HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(horizontalScrollState),
        modifier = modifier.align(Alignment.BottomEnd),
        style = LocalScrollbarStyle.current.copy(
            unhoverColor = themeBasedAccentColorOpposite().copy(alpha = 0.25f),
            hoverColor = themeBasedAccentColorOpposite().copy(alpha = 0.62f)
        )
    )
}
