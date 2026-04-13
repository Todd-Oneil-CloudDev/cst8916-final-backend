package com.cst8916.backend.config

import com.azure.cosmos.ConsistencyLevel
import com.azure.cosmos.CosmosClientBuilder
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCosmosRepositories(basePackages = ["com.cst8916.backend.repository"])
@ConfigurationProperties(prefix = "spring.data.cosmos")
class CosmosDbConfig: AbstractCosmosConfiguration() {
    var uri: String? = ""
    var key: String? = ""
    var database: String? = ""
    var consistencyLevel: String? = null

    @Bean
    fun cosmosClientBuilder(): CosmosClientBuilder {
        return CosmosClientBuilder()
            .endpoint(uri)
            .key(key)
            .consistencyLevel(ConsistencyLevel.valueOf(consistencyLevel?.uppercase() ?: "EVENTUAL"))
            .contentResponseOnWriteEnabled(true)
    }

    @Bean
    override fun cosmosConfig(): com.azure.spring.data.cosmos.config.CosmosConfig? {
        return com.azure.spring.data.cosmos.config.CosmosConfig.builder()
            .enableQueryMetrics(true)
            .build()
    }

    override fun getDatabaseName(): String? = database
}