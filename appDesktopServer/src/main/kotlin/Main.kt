import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.vkatz.missioncontrol.server.base.MissionControlServer
import com.vkatz.missioncontrol.server.base.ui.ServerUI
import kotlinx.coroutines.launch

fun main() = application {
    Window(
        title = "Mission Control Server",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        val server = remember { MissionControlServer.createServer() }
        LaunchedEffect(Unit) { launch { server.startServer() } }
        ServerUI(server, Modifier.fillMaxSize())
    }
}