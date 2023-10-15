package com.hoffi.compose.showcase.glasslayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect

class ComposableClass(
    val name: String,
    val popupPlacement: PopupPlacement = PopupPlacement.VARIABLE,
    val originInParent: OriginInParent = OriginInParent.BOTTOMLEFT,
    val offsetInParent: State<Rect> = mutableStateOf(Rect.Zero), // State, because if offset is changed dynamically when created by `derivedStateOf`
    val offsetPlacement: OriginInParent = OriginInParent.BOTTOMLEFT,
    val popupTooBig: PopupTooBig = PopupTooBig.COERCE_MAX_OFFSET,
    val content: @Composable () -> Unit
) {
    var rectSize: MutableState<RectSize> = mutableStateOf(RectSize.Zero)

    @Composable
    fun ComposableContent() {
        content()
    }
}
