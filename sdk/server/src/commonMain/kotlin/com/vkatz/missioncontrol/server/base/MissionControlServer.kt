package com.vkatz.missioncontrol.server.base

import com.vkatz.missioncontrol.common.ConnectionDefaults
import com.vkatz.missioncontrol.server.base.connection.AbsConnection
import com.vkatz.missioncontrol.server.base.connection.SocketConnection
import com.vkatz.missioncontrol.server.base.connection.TestConnection
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*

class MissionControlServer private constructor() {
    companion object {
        fun createServer() = MissionControlServer()
    }

    private var serverJob: Job? = null
    private val connections = ArrayList<AbsConnection>()
    private var onConnectionsChangedListener: ((List<AbsConnection>) -> Unit)? = null

    init {
        connections.add(TestConnection())
        connections.add(TestConnection())
        connections.add(TestConnection())
    }

    fun setOnConnectionsChangedListener(listener: ((List<AbsConnection>) -> Unit)?) {
        this.onConnectionsChangedListener = listener
        onConnectionsChangedListener?.invoke(connections)
    }

    suspend fun startServer(
        udpPort: Int = ConnectionDefaults.UDP_DEFAULT_PORT,
        tcpPort: Int = ConnectionDefaults.TCP_DEFAULT_PORT
    ) = coroutineScope {
        stopServer()
        serverJob = launch(Dispatchers.IO + Job()) {
            val selector = SelectorManager(Dispatchers.IO)
            selector.use {
                // broadcasts receiver (used to find server in lan)
                launch(Dispatchers.IO) {
                    aSocket(selector)
                        .udp()
                        .bind(InetSocketAddress("0.0.0.0", udpPort))
                        .use { udpServer ->
                            while (true) {
                                runCatching {
                                    val conn = udpServer.receive()
                                    val content = conn.packet.readUTF8Line()
                                    val accepted = content == ConnectionDefaults.UDP_REQUEST
                                    if (accepted) {
                                        val data = BytePacketBuilder().append(ConnectionDefaults.UDP_RESPONSE)
                                        udpServer.send(Datagram(data.build(), conn.address))
                                    }
                                }
                            }
                        }
                }
                // socket worker
                aSocket(selector)
                    .tcp()
                    .bind(InetSocketAddress("0.0.0.0", tcpPort))
                    .use { tcpServer ->
                        while (true) {
                            val conn = tcpServer.accept()
                            launch(Dispatchers.IO) {
                                runMissionControl(conn)
                            }
                        }
                    }
            }
        }
    }

    suspend fun stopServer() = coroutineScope {
        serverJob?.cancelAndJoin()
        serverJob = null
    }

    private suspend fun runMissionControl(socket: Socket) {
        val connection = SocketConnection(socket)
        connections.add(connection)
        onConnectionsChangedListener?.invoke(connections)
        connection.run { onConnectionsChangedListener?.invoke(connections) }
        connections.remove(connection)
        onConnectionsChangedListener?.invoke(connections)
    }
}