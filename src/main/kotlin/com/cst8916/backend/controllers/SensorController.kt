package com.cst8916.backend.controllers

import com.cst8916.backend.model.SensorEntity
import com.cst8916.backend.service.SensorAggregationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SensorController(private val sensorAggregationService: SensorAggregationService) {
    /**
     * Endpoint to retrieve the latest aggregated sensor data for all sensors.
     *
     * @return A list of SensorEntity objects representing the latest readings for each sensor.
     */
    @GetMapping("/api/sensors/latest")
    fun getLatestSensorData(): List<SensorEntity> {
        return sensorAggregationService.getLatestAggregations()
    }
}