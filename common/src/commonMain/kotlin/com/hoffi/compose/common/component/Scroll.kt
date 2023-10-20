package com.hoffi.compose.common.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

/** if contents should also have horizontal scrolling, use ScrollBox instead */
@Composable
fun ScrollColumn(
    boxAndColumnModifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val verticalScrollState = rememberScrollState(0)
    Box(boxAndColumnModifier.composed { boxModifier.nestedScroll(nestedScrollConnection) }) {
        Column(boxAndColumnModifier.composed { columnModifier.verticalScroll(verticalScrollState) }, verticalArrangement, horizontalAlignment) {
            content()
        }
        VerticalScrollbar(verticalScrollState)
    }
}
/** if contents should also have vertical scrolling, use ScrollBox instead */
@Composable
fun ScrollRow(
    boxAndRowModifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val horizontalScrollState = rememberScrollState(0)
    Box(boxAndRowModifier.composed { boxModifier.nestedScroll(nestedScrollConnection) }) {
        Row(boxAndRowModifier.composed { rowModifier.horizontalScroll(horizontalScrollState) }, horizontalArrangement, verticalAlignment) {
            content()
        }
        HorizontalScrollbar(horizontalScrollState)
    }
}
/** if only width or height should be fix, use ScrollColumn or ScrollRow instead */
@Composable
fun ScrollBox(
    boxColumnAndRowModifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val verticalScrollState = rememberScrollState(0)
    val horizontalScrollState = rememberScrollState(0)
    Box(boxColumnAndRowModifier.composed { boxModifier.nestedScroll(nestedScrollConnection) }) {
        Column(boxColumnAndRowModifier.composed { Modifier.verticalScroll(verticalScrollState) }) {
            Row(boxColumnAndRowModifier.composed { Modifier.horizontalScroll(horizontalScrollState) }) {
                content()
            }
        }
        VerticalScrollbar(verticalScrollState)
        HorizontalScrollbar(horizontalScrollState)
    }
}
