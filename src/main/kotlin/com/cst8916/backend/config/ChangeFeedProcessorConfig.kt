package com.cst8916.backend.config

import com.azure.cosmos.ChangeFeedProcessor
import com.azure.cosmos.ChangeFeedProcessorBuilder
import com.cst8916.backend.service.SensorChangeFeedService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

/**
 * This configuration class sets up a Change Feed Processor to monitor changes in the "SensorAggregations" container
 * in Azure Cosmos DB. It uses the CosmosDbConfig to build the Cosmos client and the SensorChangeFeedService to handle
 * changes when they occur.
 */
@Configuration
class ChangeFeedProcessorConfig(private val cosmosBuilder: CosmosDbConfig
, private val changeFeedService: SensorChangeFeedService) {
    private lateinit var processor: ChangeFeedProcessor
    /**
     * This bean initializes and starts the Change Feed Processor. It connects to the specified Cosmos DB database
     * and containers, and sets up a handler to process changes using the SensorChangeFeedService.
     *
     * @return The initialized ChangeFeedProcessor instance.
     */

    @EventListener(ApplicationReadyEvent::class)
    fun startChangeFeedProcessor() {
        val monitoredContainer: String = "SensorAggregations"
        val leaseContainer: String = "SensorLeases"
        println("Starting Change Feed Processor...")

        val client = cosmosBuilder.cosmosClientBuilder().buildAsyncClient()
        val database = client.getDatabase(cosmosBuilder.database) // database name
        val monitored = database.getContainer(monitoredContainer) // The container to monitor for changes
        val leases = database.getContainer(leaseContainer) // A container to store leases for the change feed processor

        // Build and start the Change Feed Processor
        this.processor = ChangeFeedProcessorBuilder()
            .hostName("cst8916-change-feed-processor") // Unique host name for this processor instance
            .feedContainer(monitored)
            .leaseContainer(leases)
            .handleChanges { changes ->
                changeFeedService.onChange(changes)
            }
            .buildChangeFeedProcessor()

        this.processor.start()
            .doOnSuccess { println("Change Feed Processor started.") }
            .doOnError { error -> println("Failed to start Change Feed Processor: ${error.message}") }
            .subscribe()
    }
}