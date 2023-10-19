package com.hoffi.compose.common.glasslayer

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.hoffi.compose.common.isHeightFinite
import com.hoffi.compose.common.isWidthFinite
import com.hoffi.compose.common.pxToDp

/**
 * Represents a Rect (e.g. on an App `GlassLayer`) that is derived in Size, Offset and Constraints from an originRect (probably constraines of a BoxWithConstraints).
 *
 * As the originRect Size, Offset and therefore its Constraints might change.
 *
 * a) holds the original `Rect`  (e.g. the parent component rectInWindow, which triggers e.g. a popup)
 * b) holds an actualSize `Rect` (e.g. the size of the popup after being rendered via modifier.onGloballyPositioned
 * c) can calculate a "derived" Rect from the original one via `sizedRect()`
 * d) holds "adjusted" `SizeConstraints` if `sizedRect()` dimensions have been altered (because of e.g. no more space in the window)
 * e) holds the eventualSizeConstraints for the rendered Component inside the adjusted SizeConstraints and obeying the actualRect size
 */
abstract class RectSize(val originRect: MutableState<Rect>) {
    protected open val eventualSizeConstraints: SizeConstraints = SizeConstraints() // initially the sizedRect() but maybe adjusted after creation, e.g. if not enough space in window
    var actualRect: Rect = Rect.Zero.copy(right = Float.POSITIVE_INFINITY, bottom = Float.POSITIVE_INFINITY) // usually set in modifier.onGloballyPositioned of the e.g. Popup itself
        set(value) { field = value ; restrictSizeConstraintsToActualRect() }
    fun sizedSize() : Size = sizedRect().size
    fun adjustEventualSizeConstraints(sizeConstraints: SizeConstraints) {
        eventualSizeConstraints.minWidth = sizeConstraints.minWidth
        eventualSizeConstraints.minHeight = sizeConstraints.minHeight
        eventualSizeConstraints.maxWidth = sizeConstraints.maxWidth
        eventualSizeConstraints.maxHeight = sizeConstraints.maxHeight
    }
    fun adjustEventualSizeConstraintsMinWidth(minWidth: Float) { eventualSizeConstraints.minWidth = minWidth }
    fun adjustEventualSizeConstraintsMinHeight(minHeight: Float) { eventualSizeConstraints.minHeight = minHeight }
    fun adjustEventualSizeConstraintsMaxWidth(maxWidth: Float) { eventualSizeConstraints.maxWidth = maxWidth }
    fun adjustEventualSizeConstraintsMaxHeight(maxHeight: Float) { eventualSizeConstraints.maxHeight = maxHeight }
    fun getEventualSizeConstraintsMinWidth() = eventualSizeConstraints.minWidth
    fun getEventualSizeConstraintsMinHeight() = eventualSizeConstraints.minHeight
    fun getEventualSizeConstraintsMaxWidth() = eventualSizeConstraints.maxWidth
    fun getEventualSizeConstraintsMaxHeight() = eventualSizeConstraints.maxHeight
    abstract fun resetSizeConstraints()
    abstract fun restrictSizeConstraintsToActualRect()
    abstract fun sizedRect() : Rect
    override fun toString(): String = """
         originRect : ${originRect.value} (size: ${originRect.value.size})
         sizedRect  : ${sizedRect()} (size: ${sizedRect().size})
         eventual   : $eventualSizeConstraints"""

    @Composable
    fun constrainToSizeConstraints(modifier: Modifier): Modifier = modifier.composed {
        sizeIn(minWidth = eventualSizeConstraints.minWidth.pxToDp(), minHeight = eventualSizeConstraints.minHeight.pxToDp(), maxWidth = eventualSizeConstraints.maxWidth.pxToDp(), maxHeight = eventualSizeConstraints.maxHeight.pxToDp())
    }

    companion object {
        val Zero = object : RectSize(mutableStateOf(Rect.Zero)) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = originRect.value
        }
        val Unspecified = object : RectSize(mutableStateOf(Rect.Zero)) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = throw Exception("UNSPECIFIED")
        }
        val Infinite = object : RectSize(mutableStateOf(Rect.Zero.copy(right = Float.POSITIVE_INFINITY, bottom = Float.POSITIVE_INFINITY))) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = originRect.value
        }
    }

    class ExactlyAs(rect: MutableState<Rect>) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { }
        override fun sizedRect(): Rect =         Rect(originRect.value.left, originRect.value.top, originRect.value.right, originRect.value.bottom)
    }
    class ExactHeightInfWidthAs(rect: MutableState<Rect>) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxWidth = eventualSizeConstraints.maxWidth.coerceAtMost(actualRect.width).coerceAtLeast(originRect.value.width) }
        override fun sizedRect(): Rect =         Rect(originRect.value.left, originRect.value.top, Float.NEGATIVE_INFINITY, originRect.value.bottom)
    }
    class ExactWidthInfHeightAs(rect: MutableState<Rect>) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxHeight = eventualSizeConstraints.maxHeight.coerceAtMost(actualRect.height).coerceAtLeast(originRect.value.height) }
        override fun sizedRect(): Rect =         Rect(originRect.value.left, originRect.value.top, originRect.value.right, Float.NEGATIVE_INFINITY)
    }
    class ExactlyAsAdjusted(rect: MutableState<Rect>, val widthPlus: Float = 0f, val heightPlus: Float = 0f) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.value.left, top = originRect.value.top,
            right = if (originRect.value.isWidthFinite()) originRect.value.right + widthPlus else originRect.value.right,
            bottom = if (originRect.value.isHeightFinite()) originRect.value.bottom + heightPlus else originRect.value.bottom
        )
    }
    class ExactlySizedAs(rect: MutableState<Rect>, var perCentWidth: Float, var perCentHeight: Float) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = if (originRect.value.isInfinite) originRect.value else Rect(left = originRect.value.left, top = originRect.value.top,
            right = if (originRect.value.isWidthFinite())  originRect.value.left + ((originRect.value.right  - originRect.value.left) * perCentWidth) else originRect.value.right,
            bottom = if (originRect.value.isHeightFinite()) originRect.value.top  + ((originRect.value.bottom - originRect.value.top)  * perCentHeight) else originRect.value.bottom
        )
    }
    class ExactlySizedWithHeightAs(rect: MutableState<Rect>, var perCentWidth: Float, var heightPlus: Float = 0f) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.value.left, top = originRect.value.top,
            right = if (originRect.value.isWidthFinite()) originRect.value.left + ((originRect.value.right  - originRect.value.left) * perCentWidth) else originRect.value.right,
            bottom = if (originRect.value.isHeightFinite()) originRect.value.bottom + heightPlus else originRect.value.bottom
        )
    }
    class ExactlySizedWithWidthAs(rect: MutableState<Rect>, var widthPlus: Float = 0f, var perCentHeight: Float) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.value.left, top = originRect.value.top,
            right = if (originRect.value.isWidthFinite()) originRect.value.right  + widthPlus else originRect.value.right,
            bottom = if (originRect.value.isHeightFinite()) originRect.value.top  + ((originRect.value.bottom - originRect.value.top)  * perCentHeight) else originRect.value.bottom
        )
    }

    /** used for AutoCompleteDropdowns ... TODO not sure about the other ones above! */
    class WidthAsButHeightVariable(rect: MutableState<Rect>, var widthPlus: Float = 0f, var minHeight: Float = 0f, maxHeight: Float = Float.NEGATIVE_INFINITY) : RectSize(rect) {
        override var eventualSizeConstraints =                           SizeConstraints(sizedRect().width + widthPlus, minHeight, maxWidth = sizedRect().width + widthPlus, maxHeight = maxHeight)
        override fun resetSizeConstraints()  { eventualSizeConstraints = SizeConstraints(sizedRect().width + widthPlus, minHeight, maxWidth = sizedRect().width + widthPlus, maxHeight = Float.POSITIVE_INFINITY) }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxHeight.coerceAtMost(actualRect.height) }
        override fun sizedRect(): Rect = Rect(left = originRect.value.left, top = originRect.value.top,
            right = if (originRect.value.isWidthFinite()) originRect.value.right + widthPlus else originRect.value.right,
            // CAN  BE NULL(!!!) if called from override var eventualSizeConstraints above
            bottom = eventualSizeConstraints.maxHeight
        )
    }
}


/** Helper Class to pass around Rect Constraints */
class SizeConstraints(var minWidth: Float = 0f, var minHeight: Float = 0f, var maxWidth: Float = Float.POSITIVE_INFINITY, var maxHeight: Float = Float.POSITIVE_INFINITY) {
    fun reset() { minWidth = 0f; minHeight = 0f; maxWidth = Float.POSITIVE_INFINITY; maxHeight = Float.POSITIVE_INFINITY }
    fun coerceHeightAtMost(height: Float): SizeConstraints {
        maxHeight = maxHeight.coerceAtMost(height)
        return this
    }
    fun coerceWidthAtMost(width: Float): SizeConstraints {
        maxWidth = maxWidth.coerceAtMost(width)
        return this
    }
    override fun toString(): String = "SizeConstraints(min:${minWidth}, ${minHeight}, max:${maxWidth}, ${maxHeight})"
}

fun Rect.createSizeConstraints() : SizeConstraints = SizeConstraints(if(isWidthFinite()) width else 20f, if(isHeightFinite()) height else 20f, width, height)
@Composable fun Modifier.constrainToSizeConstraints(sizeConstraints: SizeConstraints): Modifier = composed {
    sizeIn(minWidth = sizeConstraints.minWidth.pxToDp(), minHeight = sizeConstraints.minHeight.pxToDp(), maxWidth = sizeConstraints.maxWidth.pxToDp(), maxHeight = sizeConstraints.maxHeight.pxToDp())
}
@Composable fun Modifier.constrainToSizeConstraints(rectSize: RectSize): Modifier = rectSize.constrainToSizeConstraints(this)
