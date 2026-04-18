package com.cst8916.backend.config

import com.cst8916.backend.service.SensorWebSocketHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    @Value("\${spring.security.cors.allowed-origin}")
    var frontendUrl: String? = ""

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        println("Registering WebSocket handler with allowed origin: $frontendUrl")
        registry.addHandler(sensorWebSocketHandler(), "/ws/sensors")
            .setAllowedOrigins("*", frontendUrl!!)
            .addInterceptors(HttpSessionHandshakeInterceptor())
    }
    @Bean
    fun sensorWebSocketHandler() = SensorWebSocketHandler()
}