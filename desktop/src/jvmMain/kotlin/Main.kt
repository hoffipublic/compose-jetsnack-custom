import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.example.jetsnack.JetSnackAppEntryPoint
import com.hoffi.compose.showcase.AppWindowSize
import com.hoffi.compose.showcase.ShowcaseAppEntryPoint


fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center),
        isMinimized = false,
        width = 1200.dp,
        height = 960.dp
    )
    val appWindowTitle: MutableState<String> = remember { mutableStateOf("Hoffi's Compose Showcase") }

    val appWindowSize = remember { mutableStateOf(AppWindowSize(windowState.position.x, windowState.position.y, windowState.size.width, windowState.size.height)) }
    LaunchedEffect(windowState.position, windowState.size) {
        appWindowSize.value = AppWindowSize(windowState.position.x, windowState.position.y, windowState.size.width, windowState.size.height)
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = appWindowTitle.value
    ) {
        if (false) {
            JetSnackAppEntryPoint()
        } else {
            ShowcaseAppEntryPoint(appWindowSize, appWindowTitle)
        }
    }
}
