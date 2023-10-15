package com.hoffi.compose.common.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.accorddesk.frontend.ui.theme.hiddenFuncBackground
import com.accorddesk.frontend.ui.theme.themeBasedAccentColorOpposite
import com.hoffi.compose.common.pxToDp

@Composable
fun SwipableBorders(
    vararg border: BorderLayout.BORDER,
    content: @Composable () -> Unit
) {
    var totalDragDelta = 0f
    val state: DraggableState = rememberDraggableState(
        onDelta = { delta -> totalDragDelta += delta ; println("totalDrag: $totalDragDelta") }
    )
    val drawerSize: Dp = 7.dp
    BoxWithConstraints(
        // TODO Modifier.onGloballyPositioned { textFieldRect = it.rectInWindow() }
    ) {
        val boxConstraints = constraints
        val boxMaxWidth: Dp = boxConstraints.maxWidth.pxToDp()
        val boxMaxHeight: Dp = boxConstraints.maxHeight.pxToDp()
        val paddingValues = PaddingValues(
            start  = if (BorderLayout.BORDER.LEFT   in border) drawerSize else 0.dp,
            top    = if (BorderLayout.BORDER.TOP    in border) drawerSize else 0.dp,
            end    = if (BorderLayout.BORDER.RIGHT  in border) drawerSize else 0.dp,
            bottom = if (BorderLayout.BORDER.BOTTOM in border) drawerSize else 0.dp,
        )
        Box(Modifier.padding(paddingValues)) {
            content()
        }
        // small triangles at start, center and end in direction of possible swipe
        if (BorderLayout.BORDER.BOTTOM in border) {
            Box(Modifier.padding(top = boxMaxHeight - drawerSize).height(drawerSize)
                .draggable(
                    state = state,
                    orientation = Orientation.Vertical,
                    onDragStarted = { totalDragDelta = 0f },
                    onDragStopped = { if (totalDragDelta < -10f) println("Bottom-up drag detected: $totalDragDelta") else println("not enough Drag... $totalDragDelta") }
                )
            ) {
                Canvas(Modifier.fillMaxWidth().height(drawerSize).background(hiddenFuncBackground())) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    //drawCircle(
                    //    brush = Brush.horizontalGradient(colors = listOf(Color.Green, Color.Yellow)),
                    //    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    //    radius = size.minDimension / 2,
                    //    style = Stroke(3F)
                    //)
                    val baseLength = 4.dp.toPx()
                    val halfBase = baseLength / 2f
                    val horCen = canvasWidth / 2f
                    val between = 15.dp.toPx()
                    val padTop = 1.dp.toPx()
                    val padBot = 2.dp.toPx()
                    val padStartEnd = 25.dp.toPx()
                    for (x in if (canvasWidth >= 120) listOf(
                        padStartEnd, padStartEnd + between, padStartEnd + 2*between,
                        horCen - between - halfBase, horCen - halfBase, horCen + between - halfBase,
                        canvasWidth - padStartEnd - baseLength, canvasWidth - padStartEnd - between - baseLength, canvasWidth - padStartEnd - 2*between - baseLength
                        ) else listOf(horCen - between - halfBase, horCen - halfBase, horCen + between - halfBase)
                    ) {
                        val trianglePath = Path().apply {
                            moveTo(x = x                         , y = canvasHeight - padBot)
                            lineTo(x = x + baseLength    , y = canvasHeight - padBot)
                            lineTo(x = x + halfBase, y = padTop)
                        }
                        drawPath(
                            color = themeBasedAccentColorOpposite(),
                            path = trianglePath
                        )
                    }
                }
            }
        }
    }
}
