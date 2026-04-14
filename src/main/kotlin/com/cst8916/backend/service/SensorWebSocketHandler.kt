package com.cst8916.backend.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


class SensorWebSocketHandler: TextWebSocketHandler() {
    // A set to keep track of active WebSocket sessions
    private val activeSessions: MutableSet<WebSocketSession> = mutableSetOf()
    private val sessionsToRemove: MutableSet<WebSocketSession> = mutableSetOf()

    // This class can be expanded to handle WebSocket events such as connection, disconnection, and message reception.
    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("WebSocket client connected: ${session.id}")
        activeSessions.add(session)
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: org.springframework.web.socket.CloseStatus
    ) {
        // Handle WebSocket disconnection
        activeSessions.remove(session)
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: org.springframework.web.socket.TextMessage
    ) {
        // Handle incoming text messages from clients
    }
    suspend fun broadcast(message: String) {
        // Implement logic to broadcast messages to all connected WebSocket clients
        val textMessage = TextMessage(message)

        coroutineScope {
            activeSessions.forEach { session ->
                launch {
                    if (session.isOpen) {
                        try {
                            session.sendMessage(textMessage)
                        } catch (e: Exception) {
                            // Handle exceptions, such as logging the error
                            e.printStackTrace()
                        }
                    } else {
                        sessionsToRemove.add(session)
                    }
                }
            }
        }

        // Remove closed sessions from the active sessions set
        activeSessions.removeAll(sessionsToRemove)
        sessionsToRemove.clear()
    }
}