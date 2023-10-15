package com.hoffi.compose.showcase

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.jetsnack.ui.NavigationStack
import com.example.jetsnack.ui.home.HomeSections
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

actual class MppShowcaseAppState(
    actual val scaffoldState: ScaffoldState,
    //actual val showcaseManager: ShowcaseManager,
    actual val coroutineScope: CoroutineScope,
    actual val appWindowSize: MutableState<AppWindowSize>,
    actual val appWindowTitle: MutableState<String>
) {
    actual val darkTheme: MutableState<Boolean> = mutableStateOf(true)
    actual val isDarkTheme: Boolean
        get() = darkTheme.value
    actual fun toggleTheme() { darkTheme.value = !darkTheme.value }
    private val navigationStack = NavigationStack(HomeSections.FEED.route)

    actual val currentRoute: String
        get() = navigationStack.lastWithIndex().value

    actual fun themeBasedAccentColor() = if (isDarkTheme) Color.Black else Color.White
    actual fun themeBasedAccentColorOpposite() = if (isDarkTheme) Color.White else Color.Black

//    @Composable
//    actual fun shouldShowBottomBar(): Boolean {
//        return currentRoute?.startsWith(MainDestinations.SNACK_DETAIL_ROUTE) != true
//    }
//
//    actual fun navigateToBottomBarRoute(route: String) {
//        navigationStack.replaceBy(route)
//    }

    // ----------------------------------------------------------
    // BottomBar state source of truth
    // ----------------------------------------------------------
    // removed
}

actual val NULL_appState: MppShowcaseAppState = MppShowcaseAppState(
    ScaffoldState(DrawerState(DrawerValue.Closed, { true }), SnackbarHostState()),
    CoroutineScope(EmptyCoroutineContext),
    mutableStateOf(AppWindowSize(Dp(-1f), Dp(-1f), Dp(-1f), Dp(-1f))),
    mutableStateOf("<NULL>")
)

@Composable
actual fun rememberMppShowcaseAppState(appWindowSize: MutableState<AppWindowSize>, appWindowTitle: MutableState<String>): MppShowcaseAppState {
    val scaffoldState = rememberScaffoldState()
    //val showcaseManager = ShowcaseManager
    val coroutineScope = rememberCoroutineScope()

    return remember(scaffoldState, /* snackbarManager,*/ coroutineScope) {
        MppShowcaseAppState(scaffoldState, /* showcaseManager,*/ coroutineScope, appWindowSize, appWindowTitle)
    }
}
