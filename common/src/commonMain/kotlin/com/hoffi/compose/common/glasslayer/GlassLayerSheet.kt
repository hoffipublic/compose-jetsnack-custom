package com.hoffi.compose.common.glasslayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.hoffi.compose.common.dpToPx
import com.hoffi.compose.common.formatted
import com.hoffi.compose.common.layout.BORDER
import com.hoffi.compose.common.layout.SheetPosition
import com.hoffi.compose.common.layout.triangledDrawerHorizontalCanvas
import com.hoffi.compose.common.pxToDp
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
        val sizedRect: DpRect = rectSize.value.sizedRect().toDpRect()
        val sheetContentSize: MutableState<IntSize> = remember { mutableStateOf(IntSize.Zero) }
        if (border == BORDER.BOTTOM) {
                Box(Modifier
                        .offset(sizedRect.left, sizedRect.top)
                        .size(sizedRect.width, sizedRect.height) // TODO necessary?
                        .clipToBounds() // clip anything inside this Box to the size of this Box (might be worth commenting out for debugging)
//                    contentAlignment = Alignment.BottomStart
                ) {
                    Box(modifier = Modifier // swipe animation via offset
                        // if pulling up/left swipeableState.offset is negative
                        .let {
                            if (sheetContentSize.value != IntSize.Zero)
                                it.offset(x = 0.dp, ( +1f * sheetContentSize.value.height + swipeableState.offset.value).pxToDp())
                            else it.offset(x = 0.dp, y = sizedRect.height) /* prevent flashing on first rendering where sheetContentSize is yet unknown */
                        }
                    ) {
                        Column(Modifier
                            .onGloballyPositioned { layoutCoordinates ->
                                // if the size of the swipeable sheet content is _smaller_ than the main size content of SwipeableBorders
                                // we can only know this AFTER first rendering was completed, because of "only once" measuring of jetpack compose
//println("swiped ${"%-6s".format("BOTTOM")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - (sizedRect.top.value * 2f)).formatted()} anchors:(${anchors.value.keys})")
                                if (sheetContentSize.value == IntSize.Zero) sheetContentSize.value = layoutCoordinates.size
                                if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.height) {
                                    // remove all anchor points with height greater than actual sheet content
                                    // (by only keeping (filterKeys { }) all that are smaller than actual sheet content
                                    // and also add the _actual_ content size as EXPANDED position (negative(!) for bottomSheetContent !!!)
                                    anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.height }.toMutableMap().also { it[-1f * sheetContentSize.value.height] = SheetPosition.EXPANDED }
                                }
                            }
                        ) {
                            triangledDrawerHorizontalCanvas(BORDER.BOTTOM, swipeableState, anchors, drawerSize) // triangledDrawer above content
                            content()
                        }
                    }
                }
        } else if (border == BORDER.TOP) {
            Box(Modifier
                .offset(sizedRect.left, sizedRect.top)
                .size(sizedRect.width, sizedRect.height) // TODO necessary?
                .clipToBounds() // clip anything inside this Box to the size of this Box (might be worth commenting out for debugging)
//                contentAlignment = Alignment.TopStart
            ) {
                Box(modifier = Modifier // swipe animation via offset
                        // if pulling up/left swipeableState.offset is negative
                        .let {
                            if (sheetContentSize.value != IntSize.Zero)
                                it.offset(x = 0.dp, ( -1f * sheetContentSize.value.height + swipeableState.offset.value).pxToDp())
                            else it.offset(x = 0.dp, y = sizedRect.height) /* prevent flashing on first rendering where sheetContentSize is yet unknown */
                        }
                ) {
                    Column(Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            // if the size of the swipeable sheet content is _smaller_ than the main size content of SwipeableBorders
                            // we can only know this AFTER first rendering was completed, because of "only once" measuring of jetpack compose
//println("swiped ${"%-6s".format("TOP")} at ${"%-19s".format(swipeableState.currentValue.toString())}: ${swipeableState.offset.value.formatted()} contSize(${sheetContentSize.value.formatted()}) parSize(${layoutCoordinates.size.formatted()}) Y-posInParent:${(layoutCoordinates.positionInWindow().y - (sizedRect.top.value * 2f)).formatted()} anchors:(${anchors.value.keys})")
                            if (sheetContentSize.value == IntSize.Zero) sheetContentSize.value = layoutCoordinates.size
                            if (anchors.value.keys.last().absoluteValue > sheetContentSize.value.height) {
                                // remove all anchor points with height greater than actual sheet content
                                // (by only keeping (filterKeys { }) all that are smaller than actual sheet content
                                // and also add the _actual_ content size as EXPANDED position (positive(!) for topSheetContent !!!)
                                anchors.value = anchors.value.filterKeys { anchorPoint -> anchorPoint.absoluteValue < sheetContentSize.value.height }.toMutableMap().also { it[+1f * sheetContentSize.value.height] = SheetPosition.EXPANDED }
                            }
                        }
                    ) {
                        //Box(Modifier.sizeIn(maxHeight = sheetContentSize.value.height.pxToDp() - drawerSize).weight(1f)) {
                        Box(Modifier.weight(1f, fill = false), contentAlignment = Alignment.BottomStart) {
                            // we need this box and weight, as otherwise a Modifier.fillMaxXXX() on the content() will eat up the complete column space
                            // and the triangledDrawerHorizontalCanvas will no more be visible
                            // on the other hand, if the content has fixed size a weight with fill = true would give left over space to the content
                            // at the bottom, so on swipe down the content would appear "last"
                            content()
                        }
                        triangledDrawerHorizontalCanvas(BORDER.TOP, swipeableState, anchors, drawerSize)
                    }
                }
            }
        } else {
            throw NotImplementedError("not implemented")
        }
    }
}
