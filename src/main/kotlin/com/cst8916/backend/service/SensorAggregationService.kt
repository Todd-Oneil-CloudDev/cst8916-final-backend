package com.cst8916.backend.service

import com.cst8916.backend.model.SensorEntity
import com.cst8916.backend.repository.SensorRepository
import org.springframework.stereotype.Service

@Service
class SensorAggregationService(private val sensorRepo: SensorRepository) {
    /**
     * Retrieves the latest aggregated sensor data for all sensors.
     *
     * @return A map where the key is the sensor ID and the value is the corresponding SensorEntity.
     */
    fun getLatestAggregations(): List<SensorEntity> {
        return sensorRepo.findAll()
            .groupBy { it.location }
            .mapNotNull { entry ->
                // For each location, find the SensorEntity with the latest timestamp
                entry.value.maxByOrNull { it.windowEnd!! } ?: throw IllegalStateException("No sensor data for location: ${entry.key}")
            }
    }
}