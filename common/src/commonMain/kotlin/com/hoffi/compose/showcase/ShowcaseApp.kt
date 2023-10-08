package com.hoffi.compose.showcase

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.hoffi.compose.common.layout.BorderLayout
import com.hoffi.compose.common.layout.BorderedContent
import com.hoffi.compose.common.theme.HoffiMaterialTheme
import kotlinx.datetime.Clock

val globalCompositionLocalString = compositionLocalOf<String> { error("appState of ShowcaseApp() not set at top level") } // intId to String
var appState: MppShowcaseAppState = NULL_appState

@Composable
fun ShowcaseApp() {
    CompositionLocalProvider(
        globalCompositionLocalString provides "TopLevelString"
    ) {
        appState = rememberMppShowcaseAppState()

        HoffiMaterialTheme(appState.darkTheme) {

            //val appState: MppShowcaseAppState = rememberMppShowcaseAppState()
            Scaffold(
                topBar = {
                    Card(modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Blue).padding(5.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ScaffoldTopBar")
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
            ) { paddingValues ->
                //ShowcaseScaffoldContent(innerPaddingModifier, appState)
                Logger.i { "This is a log ${Clock.System.now()}" }
                Column(modifier = Modifier.fillMaxSize().border(width = 2.dp, color = Color.DarkGray).padding(5.dp)) {
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("welcome to ")
                        Text("Hoffi's Compose Showcase (in ${if (appState.isDarkTheme) "dark theme" else "light theme"}) with '${globalCompositionLocalString.current}'",
                            modifier = @OptIn(ExperimentalFoundationApi::class) Modifier
                                .onClick { appState.toggleTheme() },
                            style = TextStyle(fontWeight = FontWeight.W900, color = Color(0xFF4552B8))
                        )
                    }
                    BorderedContent(
                        Modifier.border(1.dp, Color.Red),
                        //BorderLayout.TOPBOTTOMSTRETCHED, //LEFTRIGHTSTRETCHED,
                        BorderLayout(
                            topleft = BorderLayout.BORDER.LEFT,
                            topright = BorderLayout.BORDER.TOP,
                            bottomleft = BorderLayout.BORDER.LEFT,
                            bottomright = BorderLayout.BORDER.RIGHT,
                        ),
                        paddingValues,
                        topComposable = { Row(Modifier.fillMaxWidth().border(1.dp, Color.Gray)) {
                            Text(" TOP ", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text(" TOP ", textAlign = TextAlign.Center)
                            Text(" TOP ", textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }},
                        bottomComposable = { Row(Modifier.fillMaxWidth().border(1.dp, Color.Gray)) {
                            Text(" BOTTOM ", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text(" BOTTOM ", textAlign = TextAlign.Center)
                            Text(" BOTTOM ", textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }},
                        leftComposable = { Column(modifier = Modifier.fillMaxHeight().border(1.dp, Color.Gray)) {
                            Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
                            Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
                            Text(" LEFT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
                        }},
                        rightComposable = { Column(modifier = Modifier.fillMaxHeight().border(1.dp, Color.Gray)) {
                            Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Top))
                            Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight())
                            Text(" RIGHT ", textAlign = TextAlign.Center, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.Bottom))
                        }},
                    ) {
                        Box(modifier = Modifier.fillMaxSize().border(2.dp, Color.Green), contentAlignment = Alignment.Center) { Surface() {
                            Text("CENTER", textAlign = TextAlign.Center)
                        }}
                    }
                }
            } // Scaffold

        } // HoffiMaterialTheme
    } // CompositionLocalProvider
}
