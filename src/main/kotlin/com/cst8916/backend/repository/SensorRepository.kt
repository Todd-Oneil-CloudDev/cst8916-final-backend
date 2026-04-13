package com.cst8916.backend.repository

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.cst8916.backend.model.SensorEntity
import org.springframework.stereotype.Repository

@Repository
interface SensorRepository: CosmosRepository<SensorEntity, String> {
}