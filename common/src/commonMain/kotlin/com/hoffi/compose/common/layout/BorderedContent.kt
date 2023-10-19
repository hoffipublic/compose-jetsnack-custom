package com.hoffi.compose.common.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.*
import co.touchlab.kermit.Logger
import com.hoffi.compose.common.NOCONTENT
import com.hoffi.compose.common.NOCONTENTwithPadding

enum class BORDER { TOP, RIGHT, BOTTOM, LEFT }

data class BorderLayout(
    val topleft: BORDER = BORDER.TOP,
    val topright: BORDER = BORDER.TOP,
    val bottomleft: BORDER = BORDER.BOTTOM,
    val bottomright: BORDER = BORDER.BOTTOM,
    val leftGuide: Float = 0.2f, val rightGuide: Float = 0.2f, val topGuide: Float = 0.2f, val bottomGuide: Float = 0.2f
) {
    data class StretchSize(val border: BORDER, val offset: Int, val stretch: Int)
    data class EventualSize(var size: DpSize, var padding: PaddingValues)
    val eventualMainSize: EventualSize   = EventualSize(DpSize(0.dp, 0.dp), PaddingValues())
    val eventualTopSize: EventualSize    = EventualSize(DpSize(0.dp, 0.dp), PaddingValues())
    val eventualRightSize: EventualSize  = EventualSize(DpSize(0.dp, 0.dp), PaddingValues())
    val eventualBottomSize: EventualSize = EventualSize(DpSize(0.dp, 0.dp), PaddingValues())
    val eventualLeftSize: EventualSize   = EventualSize(DpSize(0.dp, 0.dp), PaddingValues())

    companion object {
        val TOPBOTTOMSTRETCHED = BorderLayout()
        val LEFTRIGHTSTRETCHED = BorderLayout(
            topleft = BORDER.LEFT,
            topright = BORDER.RIGHT,
            bottomleft = BORDER.LEFT,
            bottomright = BORDER.RIGHT
        )
        val EMPTYPLACEABLE: Placeable = object : Placeable() {
            override fun get(alignmentLine: AlignmentLine) = -1
            override fun placeAt(position: IntOffset, zIndex: Float, layerBlock: (GraphicsLayerScope.() -> Unit)?) {}
        }
        val EMPTYPLACEABLES: List<Placeable> = emptyList()
    }

    val measureOrder: MutableList<StretchSize> = mutableListOf()
    init {
        if (setOf(topleft, topright, bottomleft, bottomright).size == 4) {
            /** @startuml
            title
            1. TOP stretched
            |<#lightgreen> |=<#lightgreen>    TOP |<#lightblue> |
            |<#khaki> LEFT |<#white>           |<#lightblue> RIGHT |
            |<#khaki>      |<#lightgrey> BOTTOM  |<#lightgrey> |
            end title
            @enduml */
            throw Exception("Disallowed BorderLayout: cannot measure ANY BORDER without size knowledge of other BORDERs if ALL(!) CORNERs belong to different BORDERs. At least one BORDER must be stretched over BOTH its CORNERS!")
        }
        val assertMessages: MutableList<String> = mutableListOf()
        if (topleft != BORDER.TOP && topleft != BORDER.LEFT )             assertMessages.add("topleft neither TOP nor LEFT but '$topleft'")
        if (topright != BORDER.TOP && topright != BORDER.RIGHT )          assertMessages.add("topright neither TOP nor RIGHT but '$topright'")
        if (bottomleft != BORDER.BOTTOM && bottomleft != BORDER.LEFT )    assertMessages.add("bottomleft neither BOTTOM nor LEFT but '${bottomleft}'")
        if (bottomright != BORDER.BOTTOM && bottomright != BORDER.RIGHT ) assertMessages.add("bottomright neither BOTTOM nor RIGHT but '${bottomright}'")
        if (assertMessages.isNotEmpty()) throw Exception("BorderLayout: ${assertMessages.joinToString()}")

        /* measuring order is important !!!
            1. the fully stretched BORDER<br/>
            2. the stretched neighbouring BORDER (NOT!!! the opposite boarder<br>/
            3. <br/>
            4. the not at all stretched BORDER
        */

        if (topleft == BORDER.TOP && topright == BORDER.TOP) {
            /** @startuml
            title
            1. TOP stretched
            |<#lightgreen> |=<#lightgreen>    TOP |<#lightgreen> |
            |<#white> LEFT |<#white>           |<#white> RIGHT |
            |<#white>      |<#white> BOTTOM  |<#white> |
            end title
            @enduml */
            measureOrder.add(StretchSize(BORDER.TOP, 0, 3))
            if (bottomleft == BORDER.BOTTOM && bottomright == BORDER.BOTTOM) {
                /** @startuml
                title
                2. BOTTOM stretched
                |<#lightgreen> |=<#lightgreen>    TOP |<#lightgreen> |
                |<#white> LEFT |<#white>           |<#white> RIGHT |
                |<#khaki>      |<#khaki> BOTTOM  |<#khaki> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.BOTTOM, 0, 3))
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 1))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 1))
            } else if (bottomleft != BORDER.BOTTOM && bottomright != BORDER.BOTTOM) {
                /** @startuml
                title
                2. BOTTOM minimal
                |<#lightgreen> |=<#lightgreen>    TOP |<#lightgreen> |
                |<#white> LEFT |<#white>           |<#white> RIGHT |
                |<#white>      |<#khaki> BOTTOM  |<#white> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 1))
            } else if (bottomleft == BORDER.LEFT) {
                /** @startuml
                title
                2. LEFT stretched
                |<#lightgreen> |=<#lightgreen>    TOP |<#lightgreen> |
                |<#khaki> LEFT |<#white>           |<#white> RIGHT |
                |<#khaki>      |<#lightgrey> BOTTOM  |<#lightgrey> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 1))
            } else if (bottomright == BORDER.RIGHT) {
                /** @startuml
                title
                2. RIGHT stretched
                |<#lightgreen> |=<#lightgreen>    TOP |<#lightgreen> |
                |<#white> LEFT |<#white>           |<#khaki> RIGHT |
                |<#lightgrey>      |<#lightgrey> BOTTOM  |<#khaki> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 0, 2))
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 1))
            } else {
                throw Exception("BorderLayout measureOrder algorithm error")
            }


        } else if (bottomleft == BORDER.BOTTOM && bottomright == BORDER.BOTTOM) {
            /** @startuml
            title
            1. BOTTOM stretched
            |<#white> |=<#white>    TOP |<#white> |
            |<#white> LEFT |<#white>           |<#white> RIGHT |
            |<#lightgreen>      |<#lightgreen> BOTTOM  |<#lightgreen> |
            end title
            @enduml */
            measureOrder.add(StretchSize(BORDER.BOTTOM, 0, 3))
            // if (topleft == BORDER.TOP && topright == BORDER.TOP) {
            // already dealt with above
            /** @startuml
            title
            2. TOP stretched
            |<#khaki> |=<#khaki>    TOP |<#khaki> |
            |<#white> LEFT |<#white>           |<#white> RIGHT |
            |<#lightgreen>      |<#lightgreen> BOTTOM  |<#lightgreen> |
            end title
            @enduml */
            //    measureOrder.add(StretchSize(BORDER.TOP, 0, 3))
            //    measureOrder.add(StretchSize(BORDER.LEFT, 1, 1))
            //    measureOrder.add(StretchSize(BORDER.RIGHT, 1, 1))
            if (topleft != BORDER.TOP && topright != BORDER.TOP) {
                /** @startuml
                title
                2. TOP minimal
                |<#white> |=<#khaki>    TOP |<#white> |
                |<#white> LEFT |<#white>           |<#white> RIGHT |
                |<#lightgreen>      |<#lightgreen> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.LEFT, 0, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 0, 2))
                measureOrder.add(StretchSize(BORDER.TOP, 1, 1))
            } else if (topleft == BORDER.LEFT) {
                /** @startuml
                title
                2. LEFT stretched
                |<#khaki> |=<#lightgrey>    TOP |<#lightgrey> |
                |<#khaki> LEFT |<#white>           |<#white> RIGHT |
                |<#lightgreen>      |<#lightgreen> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.LEFT, 0, 2))
                measureOrder.add(StretchSize(BORDER.TOP, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 1))
            } else if (topright == BORDER.RIGHT) {
                /** @startuml
                title
                2. RIGHT stretched
                |<#lightgrey> |=<#lightgrey>    TOP |<#khaki> |
                |<#white> LEFT |<#white>           |<#khaki> RIGHT |
                |<#lightgreen>      |<#lightgreen> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.RIGHT, 0, 2))
                measureOrder.add(StretchSize(BORDER.TOP, 0, 2))
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 1))
            } else {
                throw Exception("BorderLayout measureOrder algorithm error")
            }


        } else if (topleft == BORDER.LEFT && bottomleft == BORDER.LEFT) {
            /** @startuml
            title
            1. LEFT stretched
            |<#lightgreen> |=<#white>    TOP |<#white> |
            |<#lightgreen> LEFT |<#white>           |<#white> RIGHT |
            |<#lightgreen>      |<#white> BOTTOM  |<#white> |
            end title
            @enduml */
            measureOrder.add(StretchSize(BORDER.LEFT, 0, 3))
            if (topright == BORDER.RIGHT && bottomright == BORDER.RIGHT) {
                /** @startuml
                title
                2. RIGHT stretched
                |<#lightgreen> |=<#white>    TOP |<#khaki> |
                |<#lightgreen> LEFT |<#white>           |<#khaki> RIGHT |
                |<#lightgreen>      |<#white> BOTTOM  |<#khaki> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.RIGHT, 0, 3))
                measureOrder.add(StretchSize(BORDER.TOP, 1, 1))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 1))
            } else if (topright != BORDER.RIGHT && bottomright != BORDER.RIGHT) {
                /** @startuml
                title
                2. RIGHT minimal
                |<#lightgreen> |=<#white>    TOP |<#white> |
                |<#lightgreen> LEFT |<#white>           |<#khaki> RIGHT |
                |<#lightgreen>      |<#white> BOTTOM  |<#white> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.TOP, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 1))
            } else if (topright == BORDER.TOP) {
                /** @startuml
                title
                2. LEFT stretched
                |<#lightgreen> |=<#khaki>    TOP |<#khaki> |
                |<#lightgreen> LEFT |<#white>           |<#lightgrey> RIGHT |
                |<#lightgreen>      |<#white> BOTTOM  |<#lightgrey> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.TOP, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 1))
            } else if (topright == BORDER.RIGHT) {
                /** @startuml
                title
                2. RIGHT stretched
                |<#lightgreen> |=<#white>    TOP |<#khaki> |
                |<#lightgreen> LEFT |<#white>           |<#khaki> RIGHT |
                |<#lightgreen>      |<#lightgrey> BOTTOM  |<#lightgrey> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 2))
                measureOrder.add(StretchSize(BORDER.RIGHT, 0, 2))
                measureOrder.add(StretchSize(BORDER.TOP, 1, 1))
            } else {
                throw Exception("BorderLayout measureOrder algorithm error")
            }


        } else if (topright == BORDER.RIGHT && bottomright == BORDER.RIGHT) {
            /** @startuml
            title
            1. LEFT stretched
            |<#white> |=<#white>    TOP |<#lightgreen> |
            |<#white> LEFT |<#white>           |<#lightgreen> RIGHT |
            |<#white>      |<#white> BOTTOM  |<#lightgreen> |
            end title
            @enduml */
            measureOrder.add(StretchSize(BORDER.RIGHT, 0, 3))
            //if (topleft == BORDER.LEFT && bottomleft == BORDER.LEFT) {
            // already dealt with above
            /** @startuml
            title
            2. RIGHT stretched
            |<#khaki> |=<#white>    TOP |<#lightgreen> |
            |<#khaki> LEFT |<#white>           |<#lightgreen> RIGHT |
            |<#khaki>      |<#white> BOTTOM  |<#lightgreen> |
            end title
            @enduml */
            //    measureOrder.add(StretchSize(BORDER.LEFT, 0, 3))
            //    measureOrder.add(StretchSize(BORDER.TOP, 1, 1))
            //    measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 1))
            if (topleft != BORDER.LEFT && bottomleft != BORDER.LEFT) {
                /** @startuml
                title
                2. RIGHT minimal
                |<#white> |=<#white>    TOP |<#lightgreen> |
                |<#khaki> LEFT |<#white>           |<#lightgreen> RIGHT |
                |<#white>      |<#white> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.TOP, 0, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 0, 2))
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 1))
            } else if (topleft == BORDER.LEFT) {
                /** @startuml
                title
                2. RIGHT minimal
                |<#khaki> |=<#white>    TOP |<#lightgreen> |
                |<#khaki> LEFT |<#white>           |<#lightgreen> RIGHT |
                |<#lightgrey>      |<#lightgrey> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.BOTTOM, 0, 2))
                measureOrder.add(StretchSize(BORDER.LEFT, 0, 2))
                measureOrder.add(StretchSize(BORDER.TOP, 1, 1))
            } else if (topleft == BORDER.TOP) {
                /** @startuml
                title
                2. RIGHT minimal
                |<#khaki> |=<#khaki>    TOP |<#lightgreen> |
                |<#lightgrey> LEFT |<#white>           |<#lightgreen> RIGHT |
                |<#lightgrey>      |<#white> BOTTOM  |<#lightgreen> |
                end title
                @enduml */
                measureOrder.add(StretchSize(BORDER.TOP, 0, 2))
                measureOrder.add(StretchSize(BORDER.LEFT, 1, 2))
                measureOrder.add(StretchSize(BORDER.BOTTOM, 1, 1))
            } else {
                throw Exception("BorderLayout measureOrder algorithm error")
            }
        }
    } // init val measureOrder
}

@Composable
fun BorderedContent(
    modifier: Modifier = Modifier,
    borderLayout: BorderLayout = BorderLayout(),
    paddingValues: PaddingValues = PaddingValues(),
    topComposable: @Composable (PaddingValues) -> Unit = NOCONTENTwithPadding,
    bottomComposable: @Composable (PaddingValues) -> Unit = NOCONTENTwithPadding,
    leftComposable: @Composable (PaddingValues) -> Unit = NOCONTENTwithPadding,
    rightComposable: @Composable (PaddingValues) -> Unit = NOCONTENTwithPadding,
    mainComposable: @Composable (PaddingValues) -> Unit
) {
    val child = @Composable { childModifier: Modifier ->
        Surface(modifier = childModifier, color = MaterialTheme.colors.background, contentColor = contentColorFor(MaterialTheme.colors.background)) {
            BorderedContentLayout(
                borderLayout,
                paddingValues,
                topComposable = topComposable,
                bottomComposable = bottomComposable,
                leftComposable = leftComposable,
                rightComposable = rightComposable,
                mainComposable = mainComposable
            )
        }
    }
    child(modifier)
}

@Composable
private fun BorderedContentLayout(
    borderLayout: BorderLayout,
    paddingValues: PaddingValues,
    topComposable: @Composable (PaddingValues) -> Unit,
    bottomComposable: @Composable (PaddingValues) -> Unit,
    leftComposable: @Composable (PaddingValues) -> Unit,
    rightComposable: @Composable (PaddingValues) -> Unit,
    mainComposable: @Composable (PaddingValues) -> Unit
) {
    SubcomposeLayout { layoutConstraints ->
        // the Composable this is in might have PaddingValues (e.g. because of the BottomBar of Scaffold), we have to consider these...
        val constraints: Constraints = layoutConstraints.copy(
            maxWidth = (layoutConstraints.maxWidth - paddingValues.calculateLeftPadding(LayoutDirection.Ltr).roundToPx() - paddingValues.calculateEndPadding(LayoutDirection.Ltr).roundToPx()).coerceAtLeast(layoutConstraints.minWidth),
            maxHeight = (layoutConstraints.maxHeight - paddingValues.calculateTopPadding().roundToPx() - paddingValues.calculateBottomPadding().roundToPx()).coerceAtLeast(layoutConstraints.minHeight)
        )

        // ==========================================================================================================+
        // do some health asserts if BorderedContent is placed inside a Composable with INFINITE width and/or height
        // ==========================================================================================================+
        val assertErrorList = mutableListOf<String>()
        if ( !constraints.hasBoundedWidth && (constraints.maxWidth >= (Int.MAX_VALUE/2) || constraints.maxWidth <= (Int.MIN_VALUE/2)) ) {
            assertErrorList.add("unbounded width and width INFINITE")
        }
        if ( !constraints.hasBoundedHeight && (constraints.maxHeight >= (Int.MAX_VALUE/2) || constraints.maxHeight <= (Int.MIN_VALUE/2)) ) {
            assertErrorList.add("unbounded height and height INFINITE")
        }
        if (assertErrorList.isNotEmpty()) {
            throw Exception("FramedContent ${assertErrorList.joinToString()}")
        }

        // ==========================================================================================================+
        // measure each Panel and get back Placeable for each
        // ==========================================================================================================+

        val dummyPaddingValues = PaddingValues()

        var topPlaceables: List<Placeable> = emptyList()
        var topWidth = 0   ; var topHeight = 0   ; var topOffset = 0    ; var topPadding = dummyPaddingValues
        var bottomPlaceables: List<Placeable> = emptyList()
        var bottomWidth = 0; var bottomHeight = 0; var bottomOffset = 0 ; var bottomPadding = dummyPaddingValues
        var leftPlaceables: List<Placeable> = emptyList()
        var leftWidth = 0  ; var leftHeight = 0  ; var leftOffset = 0   ; var leftPadding = dummyPaddingValues
        var rightPlaceables: List<Placeable> = emptyList()
        var rightWidth = 0 ; var rightHeight = 0 ; var rightOffset = 0  ; var rightPadding = dummyPaddingValues

        for (stretchSize in borderLayout.measureOrder) {
            when (stretchSize.border) {
                BORDER.TOP -> {
                    if (topComposable != NOCONTENT) {
                        topPlaceables = subcompose(BORDER.TOP) {
                            when (stretchSize.offset) {
                                0 -> { topComposable(dummyPaddingValues) }
                                1 -> { topPadding = PaddingValues(start = leftWidth.toDp())  ; topOffset = 1
                                    topComposable(topPadding)
                                }
                                else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                            }
                        }.map { it.measure(
                            when (stretchSize.stretch) {
                                1 -> constraints.copy(maxWidth = (constraints.maxWidth - leftWidth - rightWidth).coerceAtLeast(constraints.minWidth))
                                2 -> when (stretchSize.offset) {
                                    0 -> constraints.copy(maxWidth = (constraints.maxWidth - rightWidth).coerceAtLeast(constraints.minWidth))
                                    1 -> constraints.copy(maxWidth = (constraints.maxWidth - leftWidth).coerceAtLeast(constraints.minWidth))
                                    else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                                }
                                3 -> constraints
                                else -> throw Exception("Illegal BorderLayout.StretchSize.stretch: ${stretchSize.stretch}")
                            }
                        )}
                        topWidth =  topPlaceables.maxByOrNull { it.width }?.width ?: 0
                        topHeight = topPlaceables.maxByOrNull { it.height }?.height ?: 0
                    }
                }
                BORDER.BOTTOM -> {
                    if (bottomComposable != NOCONTENT) {
                        bottomPlaceables = subcompose(BORDER.BOTTOM) {
                            when (stretchSize.offset) {
                                0 -> { bottomComposable(dummyPaddingValues) }
                                1 -> { bottomPadding = PaddingValues(start = leftWidth.toDp()) ; bottomOffset = 1
                                    bottomComposable(bottomPadding)
                                }
                                else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                            }
                        }.map { it.measure(
                            when (stretchSize.stretch) {
                                1 -> constraints.copy(maxWidth = (constraints.maxWidth - leftWidth - rightWidth).coerceAtLeast(constraints.minWidth))
                                2 -> when (stretchSize.offset) {
                                    0 -> constraints.copy(maxWidth = (constraints.maxWidth - rightWidth).coerceAtLeast(constraints.minWidth))
                                    1 -> constraints.copy(maxWidth = (constraints.maxWidth - leftWidth).coerceAtLeast(constraints.minWidth))
                                    else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                                }
                                3 -> constraints
                                else -> throw Exception("Illegal BorderLayout.StretchSize.stretch: ${stretchSize.stretch}")
                            }
                        )}
                        bottomWidth  = bottomPlaceables.maxByOrNull { it.width }?.width ?: 0
                        bottomHeight = bottomPlaceables.maxByOrNull { it.height }?.height ?: 0
                    }
                }
                BORDER.LEFT -> {
                    if (leftComposable != NOCONTENT) {
                        leftPlaceables = subcompose(BORDER.LEFT) {
                            when (stretchSize.offset) {
                                0 -> { leftComposable(dummyPaddingValues) }
                                1 -> { leftPadding = PaddingValues(top = topHeight.toDp()) ; leftOffset = 1
                                    leftComposable(leftPadding)
                                }
                                else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                            }
                        }.map { it.measure(
                            when (stretchSize.stretch) {
                                1 -> constraints.copy(maxHeight = (constraints.maxHeight - topHeight - bottomHeight).coerceAtLeast(constraints.minHeight))
                                2 -> when (stretchSize.offset) {
                                    0 -> constraints.copy(maxHeight = (constraints.maxHeight - bottomHeight).coerceAtLeast(constraints.minHeight))
                                    1 -> constraints.copy(maxHeight = (constraints.maxHeight - topHeight).coerceAtLeast(constraints.minHeight))
                                    else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                                }
                                3 -> constraints
                                else -> throw Exception("Illegal BorderLayout.StretchSize.stretch: ${stretchSize.stretch}")
                            }
                        )}
                        leftWidth  = leftPlaceables.maxByOrNull { it.width }?.width ?: 0
                        leftHeight = leftPlaceables.maxByOrNull { it.height }?.height ?: 0
                    }
                }
                BORDER.RIGHT -> {
                    if (rightComposable != NOCONTENT) {
                        rightPlaceables = subcompose(BORDER.RIGHT) {
                            when (stretchSize.offset) {
                                0 -> { rightComposable(dummyPaddingValues) }
                                1 ->  { rightPadding = PaddingValues(topHeight.toDp()) ; rightOffset = 1
                                    rightComposable(rightPadding)
                                }
                                else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                            }
                        }.map { it.measure(
                            when (stretchSize.stretch) {
                                1 -> constraints.copy(maxHeight = (constraints.maxHeight - topHeight - bottomHeight).coerceAtLeast(constraints.minHeight))
                                2 -> when (stretchSize.offset) {
                                    0 -> constraints.copy(maxHeight = (constraints.maxHeight - bottomHeight).coerceAtLeast(constraints.minHeight))
                                    1 -> constraints.copy(maxHeight = (constraints.maxHeight - topHeight).coerceAtLeast(constraints.minHeight))
                                    else -> throw Exception("Illegal BorderLayout.StretchSize.offset: ${stretchSize.offset}")
                                }
                                3 -> constraints
                                else -> throw Exception("Illegal BorderLayout.StretchSize.stretch: ${stretchSize.stretch}")
                            }
                        )}
                        rightWidth  = rightPlaceables.maxByOrNull { it.width }?.width ?: 0
                        rightHeight = rightPlaceables.maxByOrNull { it.height }?.height ?: 0
                    }
                }
            }
        }

        // mainContent measuring
        val mainPadding = PaddingValues(start = leftWidth.toDp(), bottom = bottomHeight.toDp(), end = rightWidth.toDp())
        val mainContentPlaceables = subcompose(42) {
            mainComposable(mainPadding)
        }.map { it.measure(
            constraints.copy(
                maxHeight = (constraints.maxHeight - topHeight - bottomHeight).coerceAtLeast(0),
                maxWidth  = (constraints.maxWidth - leftWidth - rightWidth).coerceAtLeast(0)
            )
        )}
        val mainContentWidth = mainContentPlaceables.maxByOrNull { it.width }?.width ?: 0
        val mainContentHeight = mainContentPlaceables.maxByOrNull { it.height }?.height ?: 0

        // ==========================================================================================================+
        // placing
        // ==========================================================================================================+

        val actualLayoutWidth = leftWidth + mainContentWidth + rightWidth
        val maxLayoutWidth = maxOf(actualLayoutWidth, topWidth, bottomWidth)
        val actualLayoutHeight = topHeight + mainContentHeight + bottomHeight
        val maxLayoutHeight = maxOf(actualLayoutHeight, leftHeight, rightHeight)


        // ==========================================================================================================+
        // do some health asserts before actual placing
        // ==========================================================================================================+

        if (bottomWidth > actualLayoutWidth) {
            assertErrorList.add("bottomPanel.width <= actualLayoutWidth(=leftWidth+mainWidth+rightWidth)")
        }
        if (topWidth > actualLayoutWidth) {
            assertErrorList.add("topPanel.width <= actualLayoutWidth(=leftWidth+mainWidth+rightWidth)")
        }
        if (leftHeight > actualLayoutHeight) {
            assertErrorList.add("leftPanel.height <= actualLayoutHeight(=topHeight+mainHeight+rightHeight)")
        }
        if (rightHeight > actualLayoutHeight) {
            assertErrorList.add("rightPanel.height <= actualLayoutHeight(=topHeight+mainHeight+rightHeight)")
        }
        if (assertErrorList.isNotEmpty()) {
            Logger.w("Warning: FramedContent asserts failed for: '${assertErrorList.joinToString()}'. (Do you have a Spacer() or .weight(1f) in it?")
        }

        borderLayout.eventualMainSize.size = DpSize(mainContentWidth.toDp(), mainContentHeight.toDp())
        borderLayout.eventualMainSize.padding = mainPadding
        borderLayout.eventualTopSize.size = DpSize(topWidth.toDp(), topHeight.toDp())
        borderLayout.eventualTopSize.padding = topPadding
        borderLayout.eventualRightSize.size = DpSize(rightWidth.toDp(), rightHeight.toDp())
        borderLayout.eventualRightSize.padding = rightPadding
        borderLayout.eventualBottomSize.size = DpSize(bottomWidth.toDp(), bottomHeight.toDp())
        borderLayout.eventualBottomSize.padding = bottomPadding
        borderLayout.eventualLeftSize.size = DpSize(leftWidth.toDp(), leftHeight.toDp())
        borderLayout.eventualLeftSize.padding = leftPadding

        layout(maxLayoutWidth, maxLayoutHeight) {
            mainContentPlaceables.forEach {
                it.place(leftWidth, topHeight)
            }
            leftPlaceables.forEach {
                if (leftOffset == 1) {
                    it.place(0, topHeight)
                } else {
                    it.place(0, 0)
                }
            }
            rightPlaceables.forEach {
                if (rightOffset == 1) {
                    it.place(leftWidth + mainContentWidth, topHeight)
                } else {
                    it.place(leftWidth + mainContentWidth, 0)
                }
            }
            topPlaceables.forEach {
                if (topOffset == 1) {
                    it.place(leftWidth, 0)
                } else {
                    it.place(0, 0)
                }
            }
            bottomPlaceables.forEach {
                if (bottomOffset == 1) {
                    it.place(leftWidth, topHeight + mainContentHeight)
                } else {
                    it.place(0, topHeight + mainContentHeight)
                }
            }
        }
    }
}
