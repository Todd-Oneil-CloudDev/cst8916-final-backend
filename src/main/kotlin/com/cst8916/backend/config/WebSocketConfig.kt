package com.cst8916.backend.config

import com.cst8916.backend.service.SensorWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(sensorWebSocketHandler(), "/ws/sensors")
            .setAllowedOrigins("*", "https://cst8916final.z9.web.core.windows.net/")
            .addInterceptors(HttpSessionHandshakeInterceptor())
    }
    @Bean
    fun sensorWebSocketHandler() = SensorWebSocketHandler()
}