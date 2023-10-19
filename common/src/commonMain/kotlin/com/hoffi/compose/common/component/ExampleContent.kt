package com.hoffi.compose.common.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ExampleContent(items: Int, modifier: Modifier = Modifier) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val horizontalScrollState = rememberScrollState(0)
    val verticalScrollState = rememberScrollState(0)
    Box(modifier
        .fillMaxSize()
        .nestedScroll(nestedScrollConnection)
        .padding(4.dp, 4.dp, 4.dp, 4.dp)
        .border(1.dp, Color.Yellow)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .padding(16.dp)
        ) {
            repeat(times = items) { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                        .padding(16.dp)
                ) {
                    repeat(times = items) { col -> Text("ExampleItem${row+1}:${col+1}") }
                }
            }
        }
        VerticalScrollbar(verticalScrollState)
        HorizontalScrollbar(horizontalScrollState)
    }
}

@Composable
fun ExampleContentVerticalSimple(items: Int, modifier: Modifier = Modifier) {
    Box(Modifier.size(50.dp, 10.dp).background(Color.Yellow))
    ScrollColumn(modifier
        .fillMaxHeight()
        .padding(4.dp, 4.dp, 4.dp, 4.dp)
        .border(1.dp, Color.Yellow),
        columnModifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(times = items) { Text("Example item number ${it + 1}") }
    }
}

@Composable
fun ExampleContentVertical(items: Int, modifier: Modifier = Modifier) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val verticalScrollState = rememberScrollState(0)
    Box(modifier
        .fillMaxHeight()
        .nestedScroll(nestedScrollConnection)
        .padding(4.dp, 4.dp, 4.dp, 4.dp)
        .border(1.dp, Color.Yellow)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .padding(16.dp)
        ) {
            repeat(times = items) { Text("Example item number ${it + 1}") }
        }
        VerticalScrollbar(verticalScrollState)
    }
}

@Composable
fun ExampleContentHorizontalSimple(items: Int, modifier: Modifier = Modifier) {
    ScrollRow(modifier
        .fillMaxWidth()
        .padding(4.dp, 4.dp, 4.dp, 4.dp)
        .border(1.dp, Color.Yellow),
        rowModifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(times = items) { Text("Example item${it + 1}") }
    }
}
@Composable
fun ExampleContentHorizontal(items: Int, modifier: Modifier = Modifier) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
        }
    }
    val horizontalScrollState = rememberScrollState(0)
    Box(modifier
        .fillMaxWidth()
        .nestedScroll(nestedScrollConnection)
        .padding(4.dp, 4.dp, 4.dp, 4.dp)
        .border(1.dp, Color.Yellow)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .padding(16.dp)
        ) {
            repeat(times = items) { Text("Example item${it + 1}") }
        }
        HorizontalScrollbar(horizontalScrollState)
    }
}
// .weight(1f, fill = false)
@Composable
fun BoxWithTextInCorners(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier) {
        Text(" $text TOPLEFT ",     Modifier.align(Alignment.TopStart)    .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text TOP ",         Modifier.align(Alignment.TopCenter)   .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text TOPRIGHT ",    Modifier.align(Alignment.TopEnd)      .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text LEFT ",        Modifier.align(Alignment.CenterStart) .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text CENTER ",      Modifier.align(Alignment.Center)      .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text RIGHT ",       Modifier.align(Alignment.CenterEnd)   .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text BOTTOMLEFT ",  Modifier.align(Alignment.BottomStart) .alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text BOTTOM ",      Modifier.align(Alignment.BottomCenter).alpha(0.6f), overflow = TextOverflow.Ellipsis)
        Text(" $text BOTTOMRIGHT ", Modifier.align(Alignment.BottomEnd)   .alpha(0.6f), overflow = TextOverflow.Ellipsis)
//        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Top) {
//            Text(" $text TOPLEFT " , textAlign = TextAlign.Start, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
//            Text(" $text TOP "     , textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
//            Text(" $text TOPRIGHT ", textAlign = TextAlign.End, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
//        }
//        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
//            Text(" $text LEFT " , textAlign = TextAlign.Start, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
//            Text(" $text CENTER "     , textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
//            Text(" $text RIGHT ", textAlign = TextAlign.End, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
//        }
//        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
//            Text(" $text BOTTOMLEFT " , textAlign = TextAlign.Start, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
//            Text(" $text BOTTOM "     , textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
//            Text(" $text BOTTOMRIGHT ", textAlign = TextAlign.End, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
//        }
        content()
    }
}
