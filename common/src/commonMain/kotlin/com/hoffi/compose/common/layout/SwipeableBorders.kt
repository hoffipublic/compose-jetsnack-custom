package com.hoffi.compose.common.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.accorddesk.frontend.ui.theme.hiddenFuncBackground
import com.accorddesk.frontend.ui.theme.themeBasedAccentColorOpposite
import com.hoffi.compose.common.formatted
import com.hoffi.compose.common.glasslayer.GlassLayerSheet
import com.hoffi.compose.common.glasslayer.GlassLayerSheetClass
import com.hoffi.compose.common.glasslayer.RectSize
import com.hoffi.compose.common.pxToDp
import com.hoffi.compose.common.rectInWindow
import com.hoffi.compose.showcase.appState
import kotlinx.coroutines.launch

enum class SheetPosition { HIDDEN, PARTIALLY_EXPANDED, EXPANDED }

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeableBorders(
    modifier: Modifier = Modifier,
    partiallyExpandedAt: Float = 1f, // if 1f then NO partially expanded state in anchors of swipeable
    topSheetContent: GlassLayerSheetClass = GlassLayerSheetClass.NOCONTENT,
    bottomSheetContent: GlassLayerSheetClass = GlassLayerSheetClass.NOCONTENT,
    leftSheetContent: GlassLayerSheetClass = GlassLayerSheetClass.NOCONTENT,
    rightSheetContent: GlassLayerSheetClass = GlassLayerSheetClass.NOCONTENT,
    content: @Composable () -> Unit
) {
    val rectInWindowOfBoxWithConstraints: MutableState<Rect> = remember { mutableStateOf(Rect.Zero) }
    val swipeableStateTop: SwipeableState<SheetPosition> = rememberSwipeableState(SheetPosition.HIDDEN)
    val swipeableStateBottom: SwipeableState<SheetPosition> = rememberSwipeableState(SheetPosition.HIDDEN)
    val swipeableStateLeft: SwipeableState<SheetPosition> = rememberSwipeableState(SheetPosition.HIDDEN)
    val swipeableStateRight: SwipeableState<SheetPosition> = rememberSwipeableState(SheetPosition.HIDDEN)
    val drawerSize: Dp = 7.dp

    BoxWithConstraints(modifier = modifier
        // TODO actual rectInWindow is without each drawerSize at the Borders
        .onGloballyPositioned { rectInWindowOfBoxWithConstraints.value = it.rectInWindow() } // layoutCoordinates: LayoutCoordinates -> parentComposableRect = Rect(layoutCoordinates.positionInWindow(), layoutCoordinates.size.toSize())
    ) {
        val boxConstraints = this@BoxWithConstraints.constraints
        val boxConstraintsMaxWidth: Dp = boxConstraints.maxWidth.pxToDp()
        val boxConstraintsMaxHeight: Dp = boxConstraints.maxHeight.pxToDp()
        if ( (topSheetContent != GlassLayerSheetClass.NOCONTENT || bottomSheetContent != GlassLayerSheetClass.NOCONTENT) && ! boxConstraints.hasBoundedHeight) { throw Exception("SwipeableBorders Composable with top/bottom sheet cannot have unbounded/infinite height") }
        if ( (leftSheetContent != GlassLayerSheetClass.NOCONTENT || rightSheetContent != GlassLayerSheetClass.NOCONTENT) && ! boxConstraints.hasBoundedWidth) { throw Exception("SwipeableBorders Composable with left/right sheet cannot have unbounded/infinite width") }
        // Paddings for triangledDrawers, so that they can be "overlayed" by the sheet, which takes the _whole_ size of rectInWindowOfBoxWithConstraints
        val paddingValues = PaddingValues(
            start  = if (leftSheetContent != GlassLayerSheetClass.NOCONTENT) drawerSize else 0.dp,
            top    = if (topSheetContent != GlassLayerSheetClass.NOCONTENT) drawerSize else 0.dp,
            end    = if (rightSheetContent != GlassLayerSheetClass.NOCONTENT) drawerSize else 0.dp,
            bottom = if (bottomSheetContent != GlassLayerSheetClass.NOCONTENT) drawerSize else 0.dp,
        )
        // if partiallyExpandedAt = 1f then NO partially expanded state in anchors of swipeable as 3rd map item will _overwrite_ the 2nd map item
        // if the sheetContent is "smaller" than this boxConstraints.maxXXX, anchors will be "rewritten" in `GlassLayerSheetClass` after first rendering
        // (as Jetpack Compose has a "measure once" policy, we cannot know the size of the sheetContent before it is rendered on the glass pane for the first time)
        val verticalAnchorsTop: MutableState<Map<Float, SheetPosition>> = remember { mutableStateOf(mapOf( // for top and bottom vertical swipeable sheet
            0f                                               to SheetPosition.HIDDEN,
            (boxConstraints.maxHeight * partiallyExpandedAt) to SheetPosition.PARTIALLY_EXPANDED,
            (boxConstraints.maxHeight * 1f                 ) to SheetPosition.EXPANDED
        ))}
        val verticalAnchorsBottom: MutableState<Map<Float, SheetPosition>> = remember { mutableStateOf(verticalAnchorsTop.value.mapKeys { it.key * -1f }) }
        val horizontalAnchorsLeft: MutableState<Map<Float, SheetPosition>> = remember { mutableStateOf(mapOf( // for left and right horizontal swipeable sheets
            0f                                               to SheetPosition.HIDDEN,
            (boxConstraints.maxWidth * partiallyExpandedAt)  to SheetPosition.PARTIALLY_EXPANDED,
            (boxConstraints.maxWidth * 1f                 )  to SheetPosition.EXPANDED
        ))}
        val horizontalAnchorsRight: MutableState<Map<Float, SheetPosition>> = remember { mutableStateOf(horizontalAnchorsLeft.value.mapKeys { it.key * -1f }) }

        // the main content
        Box(Modifier.padding(paddingValues)) {
            content()
        }

        // a filled box with small triangles Canvas a) to indicate that there is something "swipeable" b) to have a drag anchor
        // padding of (almost) the complete BoxWithConstraints, except drawerSize
        if (topSheetContent != GlassLayerSheetClass.NOCONTENT) {
            Box(Modifier
                .padding(bottom = (boxConstraintsMaxHeight - drawerSize).coerceAtLeast(0.dp))
                .height(drawerSize)
            ) {
                triangledDrawerHorizontalCanvas(BORDER.TOP, swipeableStateTop, verticalAnchorsTop, drawerSize)
            }
        }
        if (bottomSheetContent != GlassLayerSheetClass.NOCONTENT) {
            Box(Modifier
                .padding(top = (boxConstraintsMaxHeight - drawerSize).coerceAtLeast(0.dp))
                .height(drawerSize)
            ) {
                triangledDrawerHorizontalCanvas(BORDER.BOTTOM, swipeableStateBottom, verticalAnchorsBottom, drawerSize)
            }
        }
        if (leftSheetContent != GlassLayerSheetClass.NOCONTENT) {
            //triangledDrawerVertical(boxConstraintsMaxWidth, swipeableStateLeft, drawerSize, horizontalLeftAnchors, BORDER.LEFT)
        }
        if (rightSheetContent != GlassLayerSheetClass.NOCONTENT) {
            //triangledDrawerVertical(boxConstraintsMaxWidth, swipeableStateRight, drawerSize, horizontalRightAnchors, BORDER.RIGHT)
        }

        // the swipeable Sheets (if given and visible)
        if (topSheetContent != GlassLayerSheetClass.NOCONTENT && (swipeableStateTop.currentValue != SheetPosition.HIDDEN || swipeableStateTop.offset.value > 0f)) {
            GlassLayerSheet(topSheetContent, swipeableStateTop, drawerSize, verticalAnchorsTop, RectSize.ExactlyAs(rectInWindowOfBoxWithConstraints))
        }
        if (bottomSheetContent != GlassLayerSheetClass.NOCONTENT && (swipeableStateBottom.currentValue != SheetPosition.HIDDEN || swipeableStateBottom.offset.value < 0f)) {
            GlassLayerSheet(bottomSheetContent, swipeableStateBottom, drawerSize, verticalAnchorsBottom, RectSize.ExactlyAs(rectInWindowOfBoxWithConstraints))
        }
        if (leftSheetContent != GlassLayerSheetClass.NOCONTENT && (swipeableStateLeft.currentValue != SheetPosition.HIDDEN || swipeableStateLeft.offset.value > 0f)) {
            GlassLayerSheet(leftSheetContent, swipeableStateLeft, drawerSize, horizontalAnchorsLeft, RectSize.ExactlyAs(rectInWindowOfBoxWithConstraints))
        }
        if (rightSheetContent != GlassLayerSheetClass.NOCONTENT && (swipeableStateRight.currentValue != SheetPosition.HIDDEN || swipeableStateRight.offset.value < 0f)) {
            GlassLayerSheet(rightSheetContent, swipeableStateRight, drawerSize, horizontalAnchorsRight, RectSize.ExactlyAs(rectInWindowOfBoxWithConstraints))
        }
    } // outer BoxWithConstraints of SwipeableBorders
}

/** a colored area with small triangles (drawn on Canvas)<br/>
 *  a) to indicate that there is something "swipeable"
 *  b) to have a swipe anchor
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun triangledDrawerHorizontalCanvas(
    trianglesAtTopOrBottom: BORDER,
    verticalSwipeableState: SwipeableState<SheetPosition>,
    verticalAnchors: MutableState<Map<Float, SheetPosition>>,
    drawerSize: Dp,
) {
    if (trianglesAtTopOrBottom != BORDER.TOP && trianglesAtTopOrBottom != BORDER.BOTTOM) throw Exception("illegal trianglesAtTopOrBottom BORDER '$trianglesAtTopOrBottom' in triangledDrawerHorizontal()")
    Canvas(Modifier
        .fillMaxWidth()
        .height(drawerSize)
        .background(hiddenFuncBackground())
        .swipeable(
            state = verticalSwipeableState,
            anchors = verticalAnchors.value,
            // cannot take FractionalThreshold, because when sheet content is considerably smaller than main content (parent BoxWithConstraints)
            // the threshold is still computed for that considerably bigger main content parent BoxWithConstraints
            thresholds = { _, _ -> FixedThreshold(25.dp) },
            orientation = Orientation.Vertical,
            resistance = null // resistance to get past max swipe position
        )
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { tapOffsetOnComposable ->
                    when (verticalSwipeableState.currentValue) {
                        SheetPosition.HIDDEN -> {
                            val target = if (verticalAnchors.value.containsValue(SheetPosition.PARTIALLY_EXPANDED)) {
                                SheetPosition.PARTIALLY_EXPANDED
                            } else {
                                SheetPosition.EXPANDED
                            }
                            appState.coroutineScope.launch { verticalSwipeableState.animateTo(target) }

                        }

                        SheetPosition.PARTIALLY_EXPANDED -> appState.coroutineScope.launch { verticalSwipeableState.animateTo(SheetPosition.HIDDEN) }
                        SheetPosition.EXPANDED -> appState.coroutineScope.launch { verticalSwipeableState.animateTo(SheetPosition.HIDDEN) }
                    }
                }
            )
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val baseLength = 7.dp.toPx()
        val halfBase = baseLength / 2f
        val horCen = canvasWidth / 2f
        val between = 15.dp.toPx()
        val padTop = 1.dp.toPx()
        val padBot = 0.dp.toPx()
        val padStartEnd = 25.dp.toPx()
        for (x in if (canvasWidth >= 120) listOf(
            // left three triangles
            padStartEnd,
            padStartEnd + between,
            padStartEnd + 2 * between,
            // center three triangles
            horCen - between - halfBase,
            horCen - halfBase,
            horCen + between - halfBase,
            // right three triangles
            canvasWidth - padStartEnd - baseLength,
            canvasWidth - padStartEnd - between - baseLength,
            canvasWidth - padStartEnd - 2 * between - baseLength
        ) else listOf(horCen - between - halfBase, horCen - halfBase, horCen + between - halfBase) // center three triangles
        ) { // for body: drawing triangles
            if (    (trianglesAtTopOrBottom == BORDER.BOTTOM) && (verticalSwipeableState.currentValue == SheetPosition.HIDDEN) ||
                    (trianglesAtTopOrBottom == BORDER.TOP)    && (verticalSwipeableState.currentValue != SheetPosition.HIDDEN)
            ) {
                // triangles pointing up
                val trianglePath = Path().apply {
                    moveTo(x = x, y = canvasHeight - padBot)
                    lineTo(x = x + baseLength, y = canvasHeight - padBot)
                    lineTo(x = x + halfBase, y = padTop)
                }
                drawPath(
                    color = themeBasedAccentColorOpposite(),
                    path = trianglePath
                )
            } else {
                // triangles pointing down
                val trianglePath = Path().apply {
                    moveTo(x = x, y = padBot)
                    lineTo(x = x + baseLength, y = padBot)
                    lineTo(x = x + halfBase, y = canvasHeight - padTop)
                }
                drawPath(
                    color = themeBasedAccentColorOpposite(),
                    path = trianglePath
                )
            }
        }
    }
}
