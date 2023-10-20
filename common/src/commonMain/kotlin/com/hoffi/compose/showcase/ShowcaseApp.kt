package com.hoffi.compose.showcase

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.snackdetail.jetSnackSystemBarsPadding
import com.hoffi.compose.common.component.BoxWithTextInCorners
import com.hoffi.compose.common.component.ExampleContentHorizontal
import com.hoffi.compose.common.glasslayer.GlassLayerSheetClass
import com.hoffi.compose.common.glasslayer.GlassLayers
import com.hoffi.compose.common.layout.BORDER
import com.hoffi.compose.common.layout.BorderLayout
import com.hoffi.compose.common.layout.BorderedContent
import com.hoffi.compose.common.layout.SwipeableBorders
import com.hoffi.compose.common.theme.HoffiMaterialTheme
import kotlinx.datetime.Clock

val globalCompositionLocalString = compositionLocalOf<String> { error("appState of ShowcaseApp() not set at top level") } // intId to String
var appState: MppShowcaseAppState = NULL_appState

@Composable
fun AppWithGlassLayers(appContent: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) { // glass pane components paint over the whole window
        appContent()
        for (glassLayer in GlassLayers.glassPaneLayers) {
            glassLayer.glassLayerContent()
            glassLayer.glassLayerPopupContent()
        }
    }
}

@Composable
fun ShowcaseApp(appWindowSize: MutableState<AppWindowSize>, appWindowTitle: MutableState<String>) {
    CompositionLocalProvider(
        globalCompositionLocalString provides "TopLevelString"
    ) {
        appState = rememberMppShowcaseAppState(appWindowSize, appWindowTitle)

        HoffiMaterialTheme(appState.darkTheme) {
            AppWithGlassLayers {
                Scaffold(
                    topBar = {
                        Card(modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Blue).padding(5.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("ScaffoldTopBar ${appState.appWindowSize}")
                            }
                        }
                    },
                    drawerGesturesEnabled = false,
                    drawerContent = {
                        Card(modifier = Modifier.border(width = 2.dp, color = Color.Red).padding(5.dp)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("ScaffoldDrawerContent")
                            }
                        }
                    },
                    scaffoldState = appState.scaffoldState,
                    bottomBar = {
//                if (appState.shouldShowBottomBar()) {
//                    JetsnackBottomBar(
//                        tabs = appState.bottomBarTabs,
//                        currentRoute = appState.currentRoute!!,
//                        navigateToRoute = appState::navigateToBottomBarRoute
//                    )
//                }
                        //Card(modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Green).padding(5.dp)) {
                        Box(Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Green).padding(5.dp), contentAlignment = Alignment.Center) {
                            Text("ScaffoldBottomBar")
                        }
                        //}
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = it,
                            modifier = Modifier.jetSnackSystemBarsPadding(),
                            snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
                        )
                    }
                ) { scaffoldPaddingValues ->
                    //ShowcaseScaffoldContent(innerPaddingModifier, appState)
                    Logger.i { "This is a log in Scaffold ${Clock.System.now()}" }
                    Column(modifier = Modifier.fillMaxSize().border(width = 2.dp, color = Color.DarkGray).padding(5.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("welcome to ")
                            Text(
                                "Hoffi's Compose Showcase (in ${if (appState.isDarkTheme) "dark theme" else "light theme"}) with '${globalCompositionLocalString.current}'",
                                modifier = @OptIn(ExperimentalFoundationApi::class) Modifier
                                    .onClick { appState.toggleTheme() },
                                style = TextStyle(fontWeight = FontWeight.W900, color = Color(0xFF4552B8))
                            )
                        } // Row
                        // val borderLayout = BorderLayout.TOPBOTTOMSTRETCHED, //LEFTRIGHTSTRETCHED,
                        val borderLayout = BorderLayout(
                            topleft = BORDER.LEFT,
                            topright = BORDER.TOP,
                            bottomleft = BORDER.LEFT,
                            bottomright = BORDER.RIGHT,
                        )
                        BorderedContent(
                            Modifier.border(1.dp, Color.Red),
                            borderLayout,
                            scaffoldPaddingValues,
                            topComposable = {
                                Row(Modifier.fillMaxWidth().border(1.dp, Color.Gray)) {
                                    Text(" TOP ", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                                    Text(" TOP ", textAlign = TextAlign.Center)
                                    Text(" TOP ", textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                                }
                            },
                            bottomComposable = {
                                Row(Modifier.fillMaxWidth().border(1.dp, Color.Gray)) {
                                    Text(" BOTTOM ", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                                    Text(" BOTTOM ", textAlign = TextAlign.Center)
                                    Text(" BOTTOM ", textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                                }
                            },
                            leftComposable = {
                                Column(modifier = Modifier.fillMaxHeight().border(1.dp, Color.Gray)) {
                                    Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
                                    Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
                                    Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
                                }
                            },
                            rightComposable = {
                                Column(modifier = Modifier.fillMaxHeight().border(1.dp, Color.Gray)) {
                                    Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
                                    Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
                                    Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
                                }
                            },
                        ) { borderedContentPaddingValues ->

                            val boxBorderDp = 2.dp

                            Box(modifier = Modifier.fillMaxSize().border(boxBorderDp, Color.Green), contentAlignment = Alignment.Center) {
                                Surface() {
                                    Text("CENTER", textAlign = TextAlign.Center)
                                }
                                Column(Modifier.fillMaxSize()) {
                                    //ExampleContentVertical(40, Modifier.align(Alignment.TopStart))
                                    //ExampleContentVerticalSimple(40, Modifier.align(Alignment.TopStart))
                                    ExampleContentHorizontal(40)

                                    //Box(Modifier.size(50.dp, 50.dp).background(Color.Green).clipToBounds()) {
                                    //    Box(Modifier.size(50.dp, 50.dp).offset(10.dp, 10.dp).background(Color.DarkGray)) {
                                    //        Text("Text")
                                    //    }
                                    //}
                                    Column(Modifier.height(100.dp)) {
                                        Box(Modifier.size(30.dp, 10.dp).background(Color.Black))
                                        Box(Modifier.fillMaxWidth().weight(1f).background(Color.Blue)) // yellow box OK
                                        //Box(Modifier.fillMaxSize().background(Color.Blue))           // no yellow box visible with THIS line
                                        Box(Modifier.size(30.dp, 10.dp).background(Color.Yellow))
                                    }

                                    val partialSwipe = 0.25f
                                    SwipeableBorders(Modifier.fillMaxSize().padding(PaddingValues(boxBorderDp, boxBorderDp, boxBorderDp, boxBorderDp)), partiallyExpandedAt = partialSwipe,
                                        topSheetContent = GlassLayerSheetClass(BORDER.TOP, "hardcoded SwipeableBorders topSheet") {
                                            BoxWithTextInCorners("TopSwipeable", Modifier.fillMaxSize()                .padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↑ topSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //BoxWithTextInCorners("TopSwipeable ", Modifier.height(150.dp).fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↑ topSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //Box(Modifier.height(150.dp).fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) {}
                                            //Box(Modifier.fillMaxSize()                .padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("topSheetContent", Modifier.offset(50.dp, 50.dp)) }
                                        },
                                        bottomSheetContent = GlassLayerSheetClass(BORDER.BOTTOM, "hardcoded SwipeableBorders bottomSheet") {
                                            //BoxWithTextInCorners("BottomSwipeable", Modifier.fillMaxSize()                .padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↓ bottomSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            BoxWithTextInCorners("BottomSwipeable ", Modifier.height(150.dp).fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↓ bottomSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //ScrollColumn(boxAndColumnModifier = Modifier) {
                                            //    BoxWithTextInCorners("BottomSwipeable ", Modifier.height(1500.dp).fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↓ scroll bottomSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //    //BoxWithTextInCorners("BottomSwipeable ", Modifier.fillMaxHeight().fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↓ scroll bottomSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //    //// inside a Scrollable, if height less than main content height, you need to set the height on ScrollColumn also!!!
                                            //    //BoxWithTextInCorners("BottomSwipeable ", Modifier.height(300.dp).fillMaxWidth().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("↓ scroll bottomSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //}
                                        },
                                        leftSheetContent = GlassLayerSheetClass(BORDER.LEFT, "hardcoded SwipeableBorders leftSheet") {
                                            BoxWithTextInCorners("LeftSwipeable", Modifier.fillMaxSize()                .padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("← leftSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //BoxWithTextInCorners("LeftSwipeable ", Modifier.width(150.dp).fillMaxHeight().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("← leftSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //ScrollRow(boxAndRowModifier = Modifier.width(500.dp)) {
                                            //    BoxWithTextInCorners("LeftSwipeable ", Modifier.width(500.dp).fillMaxHeight().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("← scroll leftSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //}
                                            //ScrollRow(boxAndRowModifier = Modifier) {
                                            //    BoxWithTextInCorners("LeftSwipeable ", Modifier.width(2000.dp).fillMaxHeight().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("← scroll leftSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //    //// inside a Scrollable, if height less than main content height, you need to set the height on ScrollColumn also!!!
                                            //    //BoxWithTextInCorners("LeftSwipeable ", Modifier.width(500.dp).fillMaxHeight().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("← scroll leftSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //}
                                        },
                                        rightSheetContent = GlassLayerSheetClass(BORDER.RIGHT, "hardcoded SwipeableBorders rightSheet") {
                                            BoxWithTextInCorners("RightSwipeable", Modifier.fillMaxSize()                .padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("→ rightSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                            //BoxWithTextInCorners("RightSwipeable ", Modifier.width(150.dp).fillMaxHeight().padding(2.dp, 2.dp).background(Color.Red).border(3.dp, Color.White)) { Text("→ rightSheetContent", Modifier.offset(30.dp, 30.dp)) }
                                        },
                                    ) {
                                        Box(
                                            Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
                                            contentAlignment = Alignment.BottomStart
                                        ) {
                                            Text("Surface of SwipeableBorders")
                                        }
                                    }
                                } // Column inner
                            } // Box
                        } // BorderedContent
                    } // Column of main Scaffold
                } // Scaffold
            } // AppWithGlassLayers
        } // HoffiMaterialTheme
    } // CompositionLocalProvider
} // ShowcaseApp
