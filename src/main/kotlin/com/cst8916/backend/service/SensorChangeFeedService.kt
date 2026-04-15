package com.cst8916.backend.service

import com.cst8916.backend.model.SensorEntity
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

/**
 * Service that receives new documents from the Cosmos DB Change Feed
 * and pushes updates to all connected WebSocket clients.
 *
 * This class is invoked automatically by the Change Feed Processor
 * whenever Stream Analytics writes new aggregated sensor rows.
 */
@Service
class SensorChangeFeedService(private val webSocketHandler: SensorWebSocketHandler
, private val objectMapper: ObjectMapper) {
    /**
     * Stores the latest reading for each sensor.
     * Key: sensorId
     * Value: SensorReading
     */
    private val latestSensorMap = mutableMapOf<String, SensorEntity>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Called by the Change Feed Processor whenever new documents
     * are written to the monitored Cosmos DB container.
     *
     * @param docs List of JsonNode objects representing new documents.
     */
    fun onChange(changes: List<JsonNode>) {

        println("ChangeFeed triggered with ${changes.size} docs")
        if(changes.isEmpty()) {
            println("No new documents. Skipping broadcast.")
            return
        }

        for (change in changes) {
            val sensorId = change.get("DeviceId").asText() ?: continue // Skip if DeviceId is missing
            println("Processing change for DeviceId=$sensorId")

            // Convert the change to a SensorEntity object
            val reading = objectMapper.readValue(change.toString(), SensorEntity::class.java)
            println("Parsed SensorEntity: $reading")
            latestSensorMap[sensorId] = reading
        }
        println("Broadcasting ${latestSensorMap.size} sensors to WebSocket clients")
        // Convert the latest sensor map to JSON and broadcast it to WebSocket clients
        val jsonPayload = objectMapper.writeValueAsString(latestSensorMap)

        println("JSON payload to broadcast: $jsonPayload")

        // Broadcast the JSON payload to WebSocket clients
        scope.launch {
            webSocketHandler.broadcast(jsonPayload)
        }
    }
}