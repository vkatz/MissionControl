import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.vkatz.missioncontrol.client.debug.*
import kotlinx.coroutines.delay

fun main() = application {
    MissionControlClient.setConnectionName("MyPC")
    Window(
        title = "Mission Control Client",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        MaterialTheme {
            Column {
                var block by remember { mutableStateOf(true) }
                Column {
                    Button({ block = !block }) {
                        Text("Switch")
                    }
                    if (block) {
                        var dataString by rememberMissionControlState("String", "String")
                        Text(dataString)
                        Button({ dataString = "Default Data" }) { Text("Reset") }
                    } else {
                        var dataInt by rememberMissionControlState(1, "Int")
                        Text("Int: $dataInt")
                        Button({ dataInt = 0 }) { Text("Reset") }
                    }
                }


                var dataInt by rememberMissionControlState(1, "Int")
                var dataIntN by rememberMissionControlState(1 as Int?, "Int?")
                var dataIntR by rememberMissionControlState(10, "Int..", 10..100)
                var dataIntNR by rememberMissionControlState(1 as Int?, "Int..", 0..10)
                var dataFloat by rememberMissionControlState(1f, "Float")
                var dataFloatN by rememberMissionControlState(1f as Float?, "Float?")
                var dataFloatR by rememberMissionControlState(1f, "Float", 0f..10f)
                var dataFloatNR by rememberMissionControlState(1f as Float?, "Float", 0f..10f)
                var dataBool by rememberMissionControlState(false, "Boolean", "BoolGroup")
                var dataBoolN by rememberMissionControlState(false as Boolean?, "Boolean?", "BoolGroup")
                var dataString by rememberMissionControlState("String", "String")
                var dataColor by rememberMissionControlState(Color.Red, "Color")
                var dataOption by rememberMissionControlOptionState(0, "Options", listOf("Op1", "Op2", "Op3"))
                val dataAction1 by rememberMissionControlActionState("Action 1", "Description", "run") {
                    println("Action1 run")
                }
                val dataAction2 by rememberMissionControlActionState("Action 2", "", "run")
                LaunchedEffect(dataAction2) {
                    println("Action2 run")
                }

                var dataInfo by rememberMissionControlInfoState("SomeInfo", "Info")
                LaunchedEffect(Unit) {
                    var tick = 0
                    while (true) {
                        tick++
                        delay(1000)
                        dataInfo = "Tick $tick"
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataInt = $dataInt")
                            Button(onClick = { dataInt++ }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataIntN = $dataIntN")
                            Button(onClick = { dataIntN = (dataIntN ?: -1) + 1 }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataIntR = $dataIntR")
                            Button(onClick = { dataIntR++ }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataIntNR = $dataIntNR")
                            Button(onClick = { dataIntNR = (dataIntNR ?: -1) + 1 }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataFloat = $dataFloat")
                            Button(onClick = { dataFloat += 1f }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataFloatN = $dataFloatN")
                            Button(onClick = { dataFloatN = (dataFloatN ?: -1f) + 1f }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataFloatR = $dataFloatR")
                            Button(onClick = { dataFloatR += 1f }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataFloatNR = $dataFloatNR")
                            Button(onClick = { dataFloatNR = (dataFloatNR ?: -1f) + 1f }, content = { Text("++") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataBool = $dataBool")
                            Checkbox(dataBool, { dataBool = it })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataBoolN = $dataBoolN")
                            Button(onClick = {
                                dataBoolN = when (dataBoolN) {
                                    false -> true
                                    true -> null
                                    null -> false
                                }
                            }, content = {
                                Text(
                                    when (dataBoolN) {
                                        false -> true
                                        true -> null
                                        null -> false
                                    }.toString()
                                )
                            })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataString = ")
                            OutlinedTextField(dataString, { dataString = it })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataColor", color = dataColor, fontSize = dataIntR.sp)
                            Button(onClick = { dataColor = Color.Red }, content = { Text("Red") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataOption = $dataOption")
                            Button(onClick = { dataOption = (dataOption + 1) % 3 }, content = { Text("+>") })
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataAction = $dataAction1")
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("dataAction = $dataAction2")
                        }
                    }
                }
            }
        }
    }
}