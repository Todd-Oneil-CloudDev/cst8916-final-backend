package com.cst8916.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    @Value("\${spring.security.cors.allowed-origin}")
    var frontendUrl: String? = ""

    override fun addCorsMappings(registry: CorsRegistry) {
        println("CORS allowed origin: $frontendUrl")

        registry.addMapping("/**")
            .allowedOrigins("http://127.0.0.1:5500",
                "http://localhost:5500", frontendUrl!!)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}