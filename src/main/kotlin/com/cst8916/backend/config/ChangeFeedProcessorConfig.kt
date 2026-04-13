package com.cst8916.backend.config

import com.azure.cosmos.ChangeFeedProcessor
import com.azure.cosmos.ChangeFeedProcessorBuilder
import com.cst8916.backend.service.SensorChangeFeedService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This configuration class sets up a Change Feed Processor to monitor changes in the "SensorAggregations" container
 * in Azure Cosmos DB. It uses the CosmosDbConfig to build the Cosmos client and the SensorChangeFeedService to handle
 * changes when they occur.
 */
@Configuration
class ChangeFeedProcessorConfig(private val cosmosBuilder: CosmosDbConfig
, private val changeFeedService: SensorChangeFeedService) {

    /**
     * This bean initializes and starts the Change Feed Processor. It connects to the specified Cosmos DB database
     * and containers, and sets up a handler to process changes using the SensorChangeFeedService.
     *
     * @return The initialized ChangeFeedProcessor instance.
     */
    @Bean
    fun changeFeedProcessor(): ChangeFeedProcessor {
        val client = cosmosBuilder.cosmosClientBuilder().buildAsyncClient()

        val database = client.getDatabase("cst8916-rideaucanaldb") // database name
        val monitored = database.getContainer("SensorAggregations") // The container to monitor for changes
        val leases = database.getContainer("SensorLeases") // A container to store leases for the change feed processor

        // Build and start the Change Feed Processor
        val processor = ChangeFeedProcessorBuilder()
            .hostName("cst8916-change-feed-processor") // Unique host name for this processor instance
            .feedContainer(monitored)
            .leaseContainer(leases)
            .handleChanges { changes ->
                changeFeedService.onChange(changes)
            }
            .buildChangeFeedProcessor()
        processor.start()
        return processor
    }
}