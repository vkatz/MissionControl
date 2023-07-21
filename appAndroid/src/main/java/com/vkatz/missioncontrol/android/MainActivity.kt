package com.vkatz.missioncontrol.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.client.debug.MissionControlClient
import com.vkatz.missioncontrol.client.debug.rememberMissionControlState
import com.vkatz.missioncontrol.server.base.MissionControlServer
import com.vkatz.missioncontrol.server.base.ui.ServerUI
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val server = remember { MissionControlServer.createServer() }
            LaunchedEffect(Unit) { launch { server.startServer() } }
            ServerUI(server, Modifier.fillMaxSize())

//            var data1 by rememberMissionControlState(1, "counter1")
//            var data2 by rememberMissionControlState(1, "counter2")
//            var data3 by rememberMissionControlState(1, "counter3")
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("Client")
//                Spacer(modifier = Modifier.size(16.dp))
//                Counter(data1) { data1 = it }
//                Spacer(modifier = Modifier.size(16.dp))
//                Counter(data2) { data2 = it }
//                Spacer(modifier = Modifier.size(16.dp))
//                Counter(data3) { data3 = it }
//            }
        }
    }
}

@Composable
fun Counter(value: Int, onChanged: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onChanged(value - 1) }) {
            Text("-")
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text("$value")
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = { onChanged(value + 1) }) {
            Text("+")
        }
    }
}