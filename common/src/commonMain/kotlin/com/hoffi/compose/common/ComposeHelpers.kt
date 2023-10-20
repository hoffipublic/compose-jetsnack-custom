package com.hoffi.compose.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*

val NOCONTENT: @Composable () -> Unit = {}
val NOCONTENTwithPadding: @Composable (PaddingValues) -> Unit = {}

@Composable fun Dp.dpToPx(): Float         = with(LocalDensity.current) { this@dpToPx.toPx() }
@Composable fun Int.pxToDp(): Dp           = with(LocalDensity.current) { this@pxToDp.toDp() }
@Composable fun Float.pxToDp(): Dp         = with(LocalDensity.current) { this@pxToDp.toDp() }
@Composable fun Size.toDpSize(): DpSize    = with(LocalDensity.current) { this@toDpSize.toDpSize() }
@Composable fun Rect.toDpRect(): DpRect    = with(LocalDensity.current) { DpRect(left.toDp(), top.toDp(), right.toDp(), bottom.toDp()) }
@Composable fun IntRect.toDpRect(): DpRect = with(LocalDensity.current) { DpRect(left.toDp(), top.toDp(), right.toDp(), bottom.toDp()) }

fun LayoutCoordinates.rectInWindow() = Rect(this.positionInWindow(), this.size.toSize())
fun Rect.isWidthFinite() : Boolean = !this.width.isInfinite() && !this.width.isNaN()
fun Rect.isHeightFinite() : Boolean = !this.height.isInfinite() && !this.height.isNaN()

fun Float.formatted(              before: Int = 4, after: Int = 2): String = "%+.${after}f".format(this).padStart(before + after + 2) // +/- and comma
fun formatted(x: Float, y: Float, before: Int = 4, after: Int = 2): String = "${x.formatted(before, after)}, ${y.formatted( before, after)}"
fun Size.formatted(               before: Int = 4, after: Int = 2) = formatted(this.width, this.height,                     before, after)
fun IntSize.formatted(            before: Int = 4, after: Int = 2) = formatted(this.width.toFloat(), this.height.toFloat(), before, after)
fun Offset.formatted(             before: Int = 4, after: Int = 2) = formatted(this.x, this.y,                              before, after)

operator fun PaddingValues.plus(pv: PaddingValues): PaddingValues {
    return PaddingValues(
        start = this.calculateLeftPadding(LayoutDirection.Ltr) + pv.calculateLeftPadding(LayoutDirection.Ltr),
        top = this.calculateTopPadding() + pv.calculateTopPadding(),
        end = this.calculateRightPadding(LayoutDirection.Ltr) + pv.calculateRightPadding(LayoutDirection.Ltr),
        bottom = this.calculateBottomPadding() + pv.calculateBottomPadding()
    )
}
