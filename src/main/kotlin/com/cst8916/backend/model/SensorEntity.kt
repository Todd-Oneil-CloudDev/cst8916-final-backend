package com.cst8916.backend.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import java.time.Instant


@Container(containerName = "SensorAggregations", autoCreateContainer = false)
data class SensorEntity(
    @Id
    @JsonProperty("id")
    var id: String? = "",
    @JsonProperty("DeviceId")
    var deviceId: String? = "",
    @PartitionKey
    @JsonProperty("location")
    var location: String? = "",
    @JsonProperty("AvgIceThickness")
    var avgIceThickness: Double? = 0.0,
    @JsonProperty("MinIceThickness")
    var minIceThickness: Double? = 0.0,
    @JsonProperty("MaxIceThickness")
    var maxIceThickness: Double? = 0.0,
    @JsonProperty("AvgSurfaceTemp")
    var avgSurfaceTemp: Double? = 0.0,
    @JsonProperty("MinSurfaceTemp")
    var minSurfaceTemp: Double? = 0.0,
    @JsonProperty("MaxSurfaceTemp")
    var maxSurfaceTemp: Double? = 0.0,
    @JsonProperty("AvgExternalTemp")
    var avgExternalTemp: Double? = 0.0,
    @JsonProperty("MaxSnow")
    var maxSnowAccumulation: Double? = 0.0,
    @JsonProperty("ReadingCount")
    var readingCount: Long? = 0,
    @JsonProperty("WindowStart")
    var windowStart: Instant? = Instant.EPOCH,
    @JsonProperty("WindowEnd")
    var windowEnd: Instant? = Instant.EPOCH,
)
