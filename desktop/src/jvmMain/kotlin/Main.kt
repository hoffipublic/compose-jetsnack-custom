import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.example.jetsnack.JetSnackAppEntryPoint
import com.hoffi.compose.showcase.ShowcaseAppEntryPoint


fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center),
        isMinimized = false,
        width = 1200.dp,
        height = 960.dp
    )
    var state: String by remember { mutableStateOf("Hoffi's Compose Showcase") }
    var windowTitle: MutableState<String> = remember { mutableStateOf("Hoffi's Compose Showcase") }
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = windowTitle.value
    ) {
        if (false) {
            JetSnackAppEntryPoint()
        } else {
            ShowcaseAppEntryPoint()
        }
    }
}
