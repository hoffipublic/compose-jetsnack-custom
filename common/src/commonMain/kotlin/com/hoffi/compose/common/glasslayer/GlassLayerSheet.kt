package com.hoffi.compose.common.glasslayer

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.*
import com.hoffi.compose.common.layout.BORDER
import com.hoffi.compose.common.layout.SheetPosition
import com.hoffi.compose.common.layout.triangledDrawerHorizontalCanvas
import com.hoffi.compose.common.layout.triangledDrawerVerticalCanvas
import com.hoffi.compose.common.toDpRect
import kotlin.math.absoluteValue

/** wrapper Composable that remembers "the whole" @Composable content function<br/>
 * to be drawn later in GlassLayer.GlassPane rendering (don't forget to add AppWithGlassLayers to your app's top entry point)<br/>
 * YOU have to make sure that the `val originRect: MutableState<Rect>` inside RectSize is updated on any UI change to its size/position<br/>
 * via `Modifier.onGloballyPositioned { yourComponentSizeMutableState.value = it.rectInWindow() }`
 *
 * @param glassLayerSheetClass The wrapped @Composable, which also has (an initial, or from former visibility) `var rectSize: MutableState<RectSize>`
 *                              which in turn has the `val originRect: MutableState<Rect>`
 * @param parentBoxWithConstraintsRectInWindow the NEW `RectSize` with the `originRect: MutableState<Rect>` to be put into `glassLayerSheetClass.rectSize`
 * */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun GlassLayerSheet(
    glassLayerSheetClass: GlassLayerSheetClass,
    swipeableState: SwipeableState<SheetPosition>,
    drawerSize: Dp,
    anchors: MutableState<Map<Float, SheetPosition>>,
    parentBoxWithConstraintsRectInWindow: RectSize
) {
    glassLayerSheetClass.swipeableState = swipeableState
    glassLayerSheetClass.drawerSize = drawerSize
    glassLayerSheetClass.anchors = anchors
    glassLayerSheetClass.rectSize.value = parentBoxWithConstraintsRectInWindow

    DisposableEffect(glassLayerSheetClass) {
println("GlassLayers.sheetGlassPane DisposableEffect added '${glassLayerSheetClass.id}'")
        GlassLayers.sheetGlassPane.addComposable(glassLayerSheetClass)
        onDispose {
println("-> disposing GlassLayers.sheetGlassPane '${glassLayerSheetClass.id}'")
            GlassLayers.sheetGlassPane.removeComposable(glassLayerSheetClass)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
class GlassLayerSheetClass(
    val border: BORDER,
    id: Any,
    var swipeableState: SwipeableState<SheetPosition> = SwipeableState(initialValue = SheetPosition.HIDDEN, animationSpec = SwipeableDefaults.AnimationSpec, confirmStateChange = { true }),
    var drawerSize: Dp = 0.dp,
    var anchors: MutableState<Map<Float, SheetPosition>> = mutableStateOf(mapOf(0f to SheetPosition.HIDDEN)),
    content: @Composable () -> Unit
) : AGlassLayerComposableClass(
    id,
    mutableStateOf(RectSize.Zero),
    content
) {
    companion object { val NOCONTENT = GlassLayerSheetClass(BORDER.TOP, "<GlassLayerSheetClass NOCONTENT>", SwipeableState(initialValue = SheetPosition.HIDDEN, animationSpec = SwipeableDefaults.AnimationSpec, confirmStateChange = { true }), 0.dp, mutableStateOf(mapOf(0f to SheetPosition.HIDDEN)), com.hoffi.compose.common.NOCONTENT) }
    @Composable
    override fun ComposableContent() {
        val sizedRect: IntRect = rectSize.value.sizedRect().roundToIntRect()
        val sizedRectDp: DpRect = sizedRect.toDpRect()
        val sheetContentSize: MutableState<IntSize> = remember { mutableStateOf(IntSize.Zero) }

        Box(Modifier
            .offset(sizedRectDp.left, sizedRectDp.top)
            .size(sizedRectDp.width, sizedRectDp.height)
            .clipToBounds() // clip anything inside this Box to the size of this Box (might be worth commenting out for debugging)
        ) {
            val offsetFunc: Density.() -> IntOffset = {if (sheetContentSize.value != IntSize.Zero)  {
                // if pulling up/left swipeableState.offset is negative (for BOTTOM/RIGHT)
                when (border) {
                    BORDER.TOP ->    { IntOffset(x = 0, y = ( -1 * sheetContentSize.value.height + swipeableState.offset.value.toInt())) }
                    BORDER.BOTTOM -> { IntOffset(x = 0, y = ( +1 * sizedRect.height              + swipeableState.offset.value.toInt()))}
                    BORDER.LEFT ->   { IntOffset(x =        ( -1 * sheetContentSize.value.width  + swipeableState.offset.value.toInt()), y = 0) }
                    BORDER.RIGHT ->  { IntOffset(x =        ( +1 * sizedRect.width               + swipeableState.offset.value.toInt()), y = 0) }
                }} else {
                when (border) { // put outside clipped box to prevent flickering on first rendering
                    BORDER.TOP ->    { IntOffset(x = 0, y = sizedRect.height) }
                    BORDER.BOTTOM -> { IntOffset(x = 0, y = sizedRect.height)}
                    BORDER.LEFT ->   { IntOffset(x =        sizedRect.width, y = 0) }
                    BORDER.RIGHT ->  { IntOffset(x =        sizedRect.width, y = 0) }
                }}
            }
            val rememberSheetContentSizeAndAdjustAnchorsFunc: (layoutCoordinates: LayoutCoordinates) -> Unit = { layoutCoordinates: LayoutCoordinates ->
                // if the size of the swipeable sheet content is _smaller_ than the main size content of SwipeableBorders
                // we can only know this AFTER first rendering was completed, because of "only once" measuring of jetpack compose
                if (sheetContentSize.value == IntSize.Zero) {
                    sheetContentSize.value = layoutCoordinates.size
                }
                    // remove all anchor points with height/width greater than actual sheet content
                    // (by only keeping (filterKeys { }) all that are smaller than actual sheet content)
                    // and also add the _actual_ content height/width as EXPANDED position (negative(!) for bottom/right SheetContent !!!)
                when (border) {
                    BORDER.TOP -> {
                        if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.height) {
                            anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.height }.toMutableMap()
                                                        .also { it[+1f * sheetContentSize.value.height] = SheetPosition.EXPANDED }
                        }
                    }
                    BORDER.BOTTOM -> {
                        if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.height) {
                            anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.height }.toMutableMap()
                                                        .also { it[-1f * sheetContentSize.value.height] = SheetPosition.EXPANDED }
                        }
                    }
                    BORDER.LEFT -> {
                        if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.width) {
                            anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.width }.toMutableMap()
                                                        .also { it[+1f * sheetContentSize.value.width] = SheetPosition.EXPANDED }
                        }
                    }
                    BORDER.RIGHT ->  {
                        if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.width) {
                            anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.width }.toMutableMap()
                                                        .also { it[-1f * sheetContentSize.value.width] = SheetPosition.EXPANDED }
                        }
                    }
                }
            }
            val triangledDrawerCanvas: (border: BORDER) -> @Composable () -> Unit = { border: BORDER ->
                when (border) {
                    BORDER.TOP    -> { { triangledDrawerHorizontalCanvas(border, swipeableState, anchors, drawerSize) } }
                    BORDER.BOTTOM -> { { triangledDrawerHorizontalCanvas(border, swipeableState, anchors, drawerSize) } }
                    BORDER.LEFT   -> { { triangledDrawerVerticalCanvas(border, swipeableState, anchors, drawerSize) } }
                    BORDER.RIGHT  -> { { triangledDrawerVerticalCanvas(border, swipeableState, anchors, drawerSize) } }
                }
            }

            Box(modifier = Modifier.offset(offsetFunc)) { // swipe animation via offset
                when (border) {
                    BORDER.TOP -> {
                        Column(Modifier.onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
//println("s    wiped ${"%-6s".format("TOP")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - sizedRect.top).formatted()} anchors:(${anchors.value.keys})")
                            rememberSheetContentSizeAndAdjustAnchorsFunc(layoutCoordinates)
                        }) {
                            Box(Modifier.weight(1f, fill = false), contentAlignment = Alignment.BottomStart) {
                                // we need this box and weight, as otherwise a Modifier.fillMaxXXX() on the content() will eat up the complete column/row space
                                // and the triangledDrawerHorizontalCanvas will no more be visible
                                // on the other hand, if the content has fixed size a weight with fill = true would give left over space to the content,
                                // so on swipe down the "gap" would appear first and only after swiping really far the content "eventually" appears
                                content()
                            }
                            triangledDrawerCanvas(BORDER.TOP).invoke() // triangledDrawer after/below content
                        }
                    }
                    BORDER.BOTTOM -> {
                        Column(Modifier.onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
//println("swiped ${"%-6s".format("BOTTOM")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - sizedRect.top).formatted()} anchors:(${anchors.value.keys})")
                            rememberSheetContentSizeAndAdjustAnchorsFunc(layoutCoordinates)
                        }) {
                            triangledDrawerCanvas(BORDER.BOTTOM).invoke() // triangledDrawer before/above content
                            content()
                        }

                    }
                    BORDER.LEFT -> {
                        Row(Modifier.onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
//println("swiped ${"%-6s".format("LEFT")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - sizedRect.top).formatted()} anchors:(${anchors.value.keys})")
                            rememberSheetContentSizeAndAdjustAnchorsFunc(layoutCoordinates)
                        }) {
                            Box(Modifier.weight(1f, fill = false), contentAlignment = Alignment.TopEnd) {
                                // we need this box and weight, as otherwise a Modifier.fillMaxXXX() on the content() will eat up the complete column/row space
                                // and the triangledDrawerHorizontalCanvas will no more be visible
                                // on the other hand, if the content has fixed size a weight with fill = true would give left over space to the content,
                                // so on swipe down the "gap" would appear first and only after swiping really far the content "eventually" appears
                                content()
                            }
                            triangledDrawerCanvas(BORDER.LEFT).invoke() // triangledDrawer after/right of content
                        }
                    }
                    BORDER.RIGHT -> {
                        Row(Modifier.onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
//println("swiped ${"%-6s".format("RIGHT")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - sizedRect.top).formatted()} anchors:(${anchors.value.keys})")
                            rememberSheetContentSizeAndAdjustAnchorsFunc(layoutCoordinates)
                        }) {
                            triangledDrawerCanvas(BORDER.RIGHT).invoke() // triangledDrawer before/left of content
                            content()
                        }
                    }
                }
            }
        } // clip Box
    } // fun ComposableContent()
} // class GlassLayerSheetClass
