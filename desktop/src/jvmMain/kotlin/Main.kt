import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.example.jetsnack.JetSnackAppEntryPoint
import com.hoffi.compose.common.pxToDp
import com.hoffi.compose.showcase.AppWindowSize
import com.hoffi.compose.showcase.ShowcaseAppEntryPoint
import java.awt.Dimension
import java.awt.Toolkit


fun main() = application {
    val screenDimension: Dimension = Toolkit.getDefaultToolkit().screenSize
    val screenSize = DpSize(screenDimension.width.dp, screenDimension.height.dp)
    println("screenSize: $screenSize")
    val windowWidth = 1200.dp
    val windowHeight = 860.dp // 960.dp
    val windowState: WindowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Absolute(screenSize.width - windowWidth - 10.dp, 60.dp), // WindowPosition(Alignment.Center),
        isMinimized = false,
        width = windowWidth,
        height = windowHeight
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
