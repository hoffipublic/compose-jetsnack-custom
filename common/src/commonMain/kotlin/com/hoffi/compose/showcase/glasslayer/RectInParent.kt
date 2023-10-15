package com.hoffi.compose.showcase.glasslayer

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.hoffi.compose.common.isHeightFinite
import com.hoffi.compose.common.isWidthFinite
import com.hoffi.compose.common.pxToDp

/**
 * a) holds the original `Rect`  (e.g. the parent component rectInWindow, which triggers e.g. a popup)
 * b) holds an actualSize `Rect` (e.g. the size of the popup after being rendered via modifier.onGloballyPositioned
 * c) can calculate a "derived" Rect from the original one via `sizedRect()`
 * d) holds "adjusted" `SizeConstraints` if `sizedRect()` dimensions have been altered (because of e.g. no more space in the window)
 * e) holds the eventualSizeConstraints for the rendered Component inside the adjusted SizeConstraints and obeying the actualRect size
 */
@Immutable
abstract class RectSize(val originRect: Rect) {
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
         originRect : ${originRect} (size: ${originRect.size})
         sizedRect  : ${sizedRect()} (size: ${sizedRect().size})
         eventual   : ${eventualSizeConstraints}"""

    @Composable
    fun constrainToSizeConstraints(modifier: Modifier): Modifier = modifier.composed {
        sizeIn(minWidth = eventualSizeConstraints.minWidth.pxToDp(), minHeight = eventualSizeConstraints.minHeight.pxToDp(), maxWidth = eventualSizeConstraints.maxWidth.pxToDp(), maxHeight = eventualSizeConstraints.maxHeight.pxToDp())
    }

    companion object {
        val Zero = object : RectSize(Rect.Zero) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = originRect
        }
        val Unspecified = object : RectSize(Rect.Zero) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = throw Exception("UNSPECIFIED")
        }
        val Infinite = object : RectSize(Rect.Zero.copy(right = Float.POSITIVE_INFINITY, bottom = Float.POSITIVE_INFINITY)) {
            override fun resetSizeConstraints() { }
            override fun restrictSizeConstraintsToActualRect() { }
            override fun sizedRect(): Rect = originRect
        }
    }

    class ExactlyAs(rect: Rect) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { }
        override fun sizedRect(): Rect =         Rect(originRect.left, originRect.top, originRect.right, originRect.bottom)
    }
    class ExactHeightInfWidthAs(rect: Rect) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxWidth = eventualSizeConstraints.maxWidth.coerceAtMost(actualRect.width).coerceAtLeast(originRect.width) }
        override fun sizedRect(): Rect =         Rect(originRect.left, originRect.top, Float.NEGATIVE_INFINITY, originRect.bottom)
    }
    class ExactWidthInfHeightAs(rect: Rect) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxHeight = eventualSizeConstraints.maxHeight.coerceAtMost(actualRect.height).coerceAtLeast(originRect.height) }
        override fun sizedRect(): Rect =         Rect(originRect.left, originRect.top, originRect.right, Float.NEGATIVE_INFINITY)
    }
    class ExactlyAsAdjusted(rect: Rect, val widthPlus: Float = 0f, val heightPlus: Float = 0f) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.left, top = originRect.top,
            right = if (originRect.isWidthFinite()) originRect.right + widthPlus else originRect.right,
            bottom = if (originRect.isHeightFinite()) originRect.bottom + heightPlus else originRect.bottom
        )
    }
    class ExactlySizedAs(rect: Rect, var perCentWidth: Float, var perCentHeight: Float) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = if (originRect.isInfinite) originRect else Rect(left = originRect.left, top = originRect.top,
            right = if (originRect.isWidthFinite())  originRect.left + ((originRect.right  - originRect.left) * perCentWidth) else originRect.right,
            bottom = if (originRect.isHeightFinite()) originRect.top  + ((originRect.bottom - originRect.top)  * perCentHeight) else originRect.bottom
        )
    }
    class ExactlySizedWithHeightAs(rect: Rect, var perCentWidth: Float, var heightPlus: Float = 0f) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.left, top = originRect.top,
            right = if (originRect.isWidthFinite()) originRect.left + ((originRect.right  - originRect.left) * perCentWidth) else originRect.right,
            bottom = if (originRect.isHeightFinite()) originRect.bottom + heightPlus else originRect.bottom
        )
    }
    class ExactlySizedWithWidthAs(rect: Rect, var widthPlus: Float = 0f, var perCentHeight: Float) : RectSize(rect) {
        override var eventualSizeConstraints = sizedRect().createSizeConstraints()
        override fun resetSizeConstraints()  { sizedRect().createSizeConstraints() }
        override fun restrictSizeConstraintsToActualRect() { resetSizeConstraints() }
        override fun sizedRect(): Rect = Rect(left = originRect.left, top = originRect.top,
            right = if (originRect.isWidthFinite()) originRect.right  + widthPlus else originRect.right,
            bottom = if (originRect.isHeightFinite()) originRect.top  + ((originRect.bottom - originRect.top)  * perCentHeight) else originRect.bottom
        )
    }

    /** used for AutoCompleteDropdowns ... TODO not sure about the other ones above! */
    class WidthAsButHeightVariable(rect: Rect, var widthPlus: Float = 0f, var minHeight: Float = 0f, maxHeight: Float = Float.NEGATIVE_INFINITY) : RectSize(rect) {
        override var eventualSizeConstraints =                           SizeConstraints(sizedRect().width + widthPlus, minHeight, maxWidth = sizedRect().width + widthPlus, maxHeight = maxHeight)
        override fun resetSizeConstraints()  { eventualSizeConstraints = SizeConstraints(sizedRect().width + widthPlus, minHeight, maxWidth = sizedRect().width + widthPlus, maxHeight = Float.POSITIVE_INFINITY) }
        override fun restrictSizeConstraintsToActualRect() { eventualSizeConstraints.maxHeight.coerceAtMost(actualRect.height) }
        override fun sizedRect(): Rect = Rect(left = originRect.left, top = originRect.top,
            right = if (originRect.isWidthFinite()) originRect.right + widthPlus else originRect.right,
            // CAN  BE NULL(!!!) if called from override var eventualSizeConstraints above
            bottom = eventualSizeConstraints?.maxHeight ?: Float.NEGATIVE_INFINITY
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
