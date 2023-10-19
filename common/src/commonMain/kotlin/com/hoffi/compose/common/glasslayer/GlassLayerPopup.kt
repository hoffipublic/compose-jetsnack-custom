package com.hoffi.compose.common.glasslayer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.hoffi.compose.common.dpToPx
import com.hoffi.compose.showcase.appState

enum class PopupPlacement { VARIABLE, CENTERED, UNDER, ABOVE, TORIGHT, TOLEFT }
enum class OriginInParent { TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT }
enum class PopupTooBig { COERCE_MAX_OFFSET, SHRINK_SIZE, REPOSITION }

/**
 * AppPopup will be rendered on its GlassLayer and does remember its global position</br>
 * It has to be in a `Box` with the Component it should appear on/over/below.
 */
@Composable
fun AppPopup(
    name: String,
    glassPane: GlassLayers.GlassPane,
    parentRectSize: MutableState<RectSize>,
    popupPlacement: PopupPlacement = PopupPlacement.VARIABLE,
    originInParent: OriginInParent = OriginInParent.BOTTOMLEFT,
    offsetInParent: State<Rect> = mutableStateOf(Rect.Zero),
    offsetPlacement: OriginInParent = OriginInParent.BOTTOMLEFT,
    popupTooBig: PopupTooBig = PopupTooBig.COERCE_MAX_OFFSET,
    content: @Composable () -> Unit
) {
    val popupClass = remember { PopupClass(name, popupPlacement, originInParent, offsetInParent, offsetPlacement, popupTooBig, content) }
    if (parentRectSize.value === RectSize.Zero) {
        // this Box will have the same global position as the Box of the Component containing this AppPopup
        // but this way we cannot know the parents width and height which we'll simply set to zero
        Box(Modifier.onGloballyPositioned { popupClass.rectSize = mutableStateOf(RectSize.ExactlyAs(mutableStateOf(Rect(it.positionInWindow(), Size.Zero)))) })
    } else {
        popupClass.rectSize = parentRectSize // in the caller get this by Modifier.onGloballyPositioned { componentRect = it.rectInWindow() }
    }
    DisposableEffect(popupClass) {
        glassPane.addPopup(popupClass)
        onDispose {
            glassPane.removePopup(popupClass)
            //sizePopup.value = SizePopup()
        }
    }
}

/**
 * encapsulates the composable popup content
 */
class PopupClass(
    id: Any,
    val popupPlacement: PopupPlacement = PopupPlacement.VARIABLE,
    val originInParent: OriginInParent = OriginInParent.BOTTOMLEFT,
    val offsetInParent: State<Rect> = mutableStateOf(Rect.Zero), // State, because if offset is changed dynamically when created by `derivedStateOf`
    val offsetPlacement: OriginInParent = OriginInParent.BOTTOMLEFT,
    val popupTooBig: PopupTooBig = PopupTooBig.COERCE_MAX_OFFSET,
    content: @Composable () -> Unit
) : AGlassLayerComposableClass(id, mutableStateOf(RectSize.Zero), content)  {
    companion object {
        val POPUP_MIN_SIZE = IntSize(width  = 300, height = 250)
    }

    @Composable override fun ComposableContent() {
        if (rectSize.value.originRect.value !== Rect.Zero) { // sentinel if initialized already

            val totalOffset = when(originInParent) {
                OriginInParent.TOPLEFT ->     rectSize.value.originRect.value.topLeft
                OriginInParent.TOPRIGHT ->    rectSize.value.originRect.value.topRight
                OriginInParent.BOTTOMLEFT ->  rectSize.value.originRect.value.bottomLeft
                OriginInParent.BOTTOMRIGHT -> rectSize.value.originRect.value.bottomRight
            } + when (offsetPlacement) {
                OriginInParent.TOPLEFT ->     offsetInParent.value.topLeft
                OriginInParent.TOPRIGHT ->    offsetInParent.value.topRight
                OriginInParent.BOTTOMLEFT ->  offsetInParent.value.bottomLeft
                OriginInParent.BOTTOMRIGHT -> offsetInParent.value.bottomRight
            }
            val maxWidthAvailableInWindow = appState.appWindowSize.value.width.dpToPx()  - totalOffset.x // ADWindowState.mainWindowInsetWidth()   - totalOffset.x
            val maxHeightAvailableInWindow= appState.appWindowSize.value.height.dpToPx() - totalOffset.y // ADWindowState.mainWindowInsetsHeight() - totalOffset.y

            val sizedRect = rectSize.value.sizedRect()
            val maxOffsetPossible = Offset(
                appState.appWindowSize.value.width.dpToPx() - sizedRect.width, // ADWindowState.mainWindowWidth() - sizedRect.width - ADWindowState.mainWindowInsetsLeft(),
                appState.appWindowSize.value.height.dpToPx() - sizedRect.height  // ADWindowState.mainWindowHeight() - sizedRect.height - ADWindowState.mainWindowInsetsTop()
            )
            val finalOffset: Offset
            val finalMaxWidth: Float
            val finalMaxHeight: Float
            when (popupTooBig) {
                PopupTooBig.COERCE_MAX_OFFSET -> {
                    finalOffset = Offset(
                        totalOffset.x.coerceAtMost(maxOffsetPossible.x),
                        totalOffset.y.coerceAtMost(maxOffsetPossible.y)
                    )
                    finalMaxWidth = sizedRect.width
                    finalMaxHeight = sizedRect.height
                }
                PopupTooBig.SHRINK_SIZE -> {
                    finalOffset = totalOffset
                    finalMaxWidth = sizedRect.width.coerceAtMost(maxWidthAvailableInWindow)
                    finalMaxHeight = sizedRect.height.coerceAtMost(maxHeightAvailableInWindow)
                }
                PopupTooBig.REPOSITION -> {
                    TODO("not implemented yet") // TODO implement me!
                }
            }
            val sizeConstraints = SizeConstraints(
                minWidth = POPUP_MIN_SIZE.width.toFloat().coerceAtMost(finalMaxWidth),
                minHeight = POPUP_MIN_SIZE.height.toFloat().coerceAtMost(finalMaxHeight),
                maxWidth = if(finalMaxWidth == 0f || finalMaxWidth.isInfinite()) maxWidthAvailableInWindow else finalMaxWidth,
                maxHeight = if(finalMaxHeight == 0f || finalMaxHeight.isInfinite()) maxHeightAvailableInWindow else finalMaxHeight
            )
            rectSize.value.adjustEventualSizeConstraints(sizeConstraints)
            println("PopupContent : ${rectSize.value}")


            Box(
                modifier = Modifier
                    .offset { (finalOffset).round() }
                    .background(MaterialTheme.colors.surface)
                    //.border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.75f))
                    .border(1.dp, Color.Magenta)

                    //.constrainToSizeConstraints(rectSize.value.eventualSizeConstraints.coerceHeightAtMost(rectSize.value.eventualSizeConstraints.maxHeight.absoluteValue))
                    .constrainToSizeConstraints(rectSize.value)

            ) {
                content()
            }
        }
    }

    @Composable
    fun size() : Size = rectSize.value.sizedSize()
}
