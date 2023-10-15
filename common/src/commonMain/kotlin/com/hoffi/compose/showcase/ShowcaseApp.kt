package com.hoffi.compose.showcase

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.hoffi.compose.common.component.ExampleContentHorizontal
import com.hoffi.compose.common.layout.*
import com.hoffi.compose.common.theme.HoffiMaterialTheme
import com.hoffi.compose.showcase.glasslayer.GlassLayers
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
                    Logger.i { "This is a log ${Clock.System.now()}" }
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
                        }
                        // val borderLayout = BorderLayout.TOPBOTTOMSTRETCHED, //LEFTRIGHTSTRETCHED,
                        val borderLayout = BorderLayout(
                            topleft = BorderLayout.BORDER.LEFT,
                            topright = BorderLayout.BORDER.TOP,
                            bottomleft = BorderLayout.BORDER.LEFT,
                            bottomright = BorderLayout.BORDER.RIGHT,
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
                            var showSheetSmall by rememberSaveable { mutableStateOf(false) }
                            var showSheetLarge by rememberSaveable { mutableStateOf(true) }
                            var showBottomSheet by rememberSaveable { mutableStateOf(false) }

                            Box(modifier = Modifier.fillMaxSize().border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Surface() {
                                    Text("CENTER", textAlign = TextAlign.Center)
                                }
                                Column(Modifier.fillMaxSize()) {
                                    //ExampleContentVertical(40, Modifier.align(Alignment.TopStart))
                                    //ExampleContentVerticalSimple(40, Modifier.align(Alignment.TopStart))
                                    ExampleContentHorizontal(40)

                                    SwipableBorders(BorderLayout.BORDER.BOTTOM) { //Modifier.fillMaxSize()) {
                                        Box(
                                            Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
                                            contentAlignment = Alignment.BottomStart
                                        ) {
                                            Text("Surface of SwipableBorders")
                                        }
                                    }
                                    ////if (showBottomSheet) {
                                    //    @OptIn(ExperimentalMaterialApi::class)
                                    //    BottomSheetSwipeable(
                                    //        onPopupDismissRequest = { showBottomSheet = false },
                                    //        borderLayout.eventualMainSize.padding, // TODO at this point borderLayout only has its initial values
                                    //        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                    //        initialSheetPosition = SheetPosition.PARTIALLY_EXPANDED
                                    //    ) {
                                    //        ExampleContentVerticalSimple(items = 70)
                                    //    }
                                    ////}

                                    //@OptIn(ExperimentalMaterialApi::class)
                                    //FullHeightBottomSheet(
                                    //    header = { Surface() { Text("HEADER", textAlign = TextAlign.Center) } }
                                    //) {
                                    //    ExampleContentVertical(20)
                                    //}


                                    //if (showSheetSmall) {
                                    //    @OptIn(ExperimentalMaterialApi::class)
                                    //    ModalBottomSheet(
                                    //        onDismissRequest = { showSheetSmall = false },
                                    //        sheetState = rememberSheetState(),
                                    //        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                    //    ) {
                                    //        ExampleColumnContent(items = 6)
                                    //    }
                                    //}
                                    //if (showSheetLarge) {
                                    //    @OptIn(ExperimentalMaterialApi::class)
                                    //    ModalBottomSheet(
                                    //        onDismissRequest = { showSheetLarge = false },
                                    //        sheetState = rememberSheetState(),
                                    //        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                    //    ) {
                                    //        ExampleColumnContent(items = 101)
                                    //    }
                                    //}
                                } // Column
                            } // Box
                        } // BorderedContent
                    } // Column
                } // Scaffold
            } // AppWithGlassLayers
        } // HoffiMaterialTheme
    } // CompositionLocalProvider
} // ShowcaseApp
