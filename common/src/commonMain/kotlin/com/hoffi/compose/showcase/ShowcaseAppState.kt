package com.hoffi.compose.showcase

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope

/**
 * Destinations used in the [ShowcaseApp].
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val SNACK_DETAIL_ROUTE = "snack"
    const val SNACK_ID_KEY = "snackId"
}


expect val NULL_appState: MppShowcaseAppState

@Composable
expect fun rememberMppShowcaseAppState(appWindowSize: MutableState<AppWindowSize>, appWindowTitle: MutableState<String>): MppShowcaseAppState

@Stable
expect class MppShowcaseAppState {
    val appWindowSize: MutableState<AppWindowSize>
    val appWindowTitle: MutableState<String>
    val scaffoldState: ScaffoldState
    //val showcaseManager: ShowcaseManager
    val coroutineScope: CoroutineScope
    val darkTheme: MutableState<Boolean>
    val isDarkTheme: Boolean
    fun toggleTheme()
    val currentRoute: String
    fun themeBasedAccentColor(): Color
    fun themeBasedAccentColorOpposite(): Color
}

data class AppWindowSize(val xPos: Dp, val yPos: Dp, val width: Dp, val height: Dp)


///**
// * Responsible for holding state related to [ShowcaseApp] and containing UI-related logic.
// */
//@Stable
//class ShowcaseAppState(
//    val scaffoldState: ScaffoldState,
//    //private val showcaseManager: ShowcaseManager,
//    coroutineScope: CoroutineScope
//) {
//    // Process data coming from SnackbarManager
//    init {
//        coroutineScope.launch {
////            showcaseManager.messages.collect { currentMessages ->
////                if (currentMessages.isNotEmpty()) {
////                    val message = currentMessages[0]
////                    // TODO: implement
////                    val text = "TODO: resources.getText(message.messageId)"
////
////                    // Display the snackbar on the screen. `showSnackbar` is a function
////                    // that suspends until the snackbar disappears from the screen
////                    scaffoldState.snackbarHostState.showSnackbar(text.toString())
////                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
////                    snackbarManager.setMessageShown(message.id)
////                }
////            }
//        }
//    }
//
//    // ----------------------------------------------------------
//    // BottomBar state source of truth
//    // ----------------------------------------------------------
//    // removed
//}
