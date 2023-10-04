package com.hoffi.compose.showcase

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.snackdetail.jetSnackSystemBarsPadding
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
                    Card(modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Green).padding(5.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ScaffoldBottomBar")
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.jetSnackSystemBarsPadding(),
                        snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
                    )
                }
            ) { innerPaddingModifier ->
                //ShowcaseScaffoldContent(innerPaddingModifier, appState)
                Logger.i { "This is a log ${Clock.System.now()}" }
                Card(modifier = Modifier.fillMaxSize().border(width = 2.dp, color = Color.DarkGray).padding(5.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            buildAnnotatedString {
                                append("welcome to ")
                                withStyle(
                                    style = SpanStyle(fontWeight = FontWeight.W900, color = Color(0xFF4552B8))
                                ) {
                                    append("Hoffi's Compose Showcase (in ${if (appState.isDarkTheme) "dark theme" else "light theme"}) with '${globalCompositionLocalString.current}'")
                                }
                            },
                            @OptIn(ExperimentalFoundationApi::class)
                            Modifier.onClick { appState.toggleTheme() }
                        )
                    }
                }
            } // Scaffold

        } // HoffiMaterialTheme
    } // CompositionLocalProvider
}
