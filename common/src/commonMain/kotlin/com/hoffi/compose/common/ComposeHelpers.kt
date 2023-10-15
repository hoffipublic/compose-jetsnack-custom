package com.hoffi.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.dpToPx(): Float = with(LocalDensity.current) { this@dpToPx.toPx() }
@Composable
fun Int.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }
@Composable
fun Float.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Rect.isWidthFinite() : Boolean = !this.width.isInfinite() && !this.width.isNaN()
fun Rect.isHeightFinite() : Boolean = !this.height.isInfinite() && !this.height.isNaN()
