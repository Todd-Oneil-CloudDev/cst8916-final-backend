# CST8916 Final — IoT Sensor Monitoring Backend

A real-time backend service for an IoT sensor monitoring platform, built with **Kotlin** and **Spring Boot**. The service ingests aggregated sensor telemetry from **Azure Cosmos DB**, delivers live updates to connected dashboard clients over **WebSocket**, and exposes a REST API for on-demand data retrieval.

> **Course:** CST8916 - Algonquin College &nbsp;|&nbsp; **Language:** Kotlin 100%

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Azure Cosmos DB Setup](#azure-cosmos-db-setup)
- [Local Development](#local-development)
- [Build & Run](#build--run)
- [API Reference](#api-reference)
- [WebSocket Integration](#websocket-integration)
- [Deployment](#deployment)
- [License](#license)

---

## Features

- **Real-Time Data Push** - Cosmos DB Change Feed Processor detects new aggregated sensor rows and broadcasts updates to all connected WebSocket clients instantly.
- **REST API** - `GET /api/sensors/latest` returns the most recent aggregation window per sensor location.
- **Azure Cosmos DB Integration** - Spring Data Cosmos repository with direct-mode connectivity and query metrics.
- **Coroutine-Powered Broadcasting** - Kotlin coroutines handle concurrent WebSocket message delivery without blocking.
- **Spring Security** - Pre-configured security filter chain with CSRF disabled for WebSocket compatibility.
- **Health & Metrics** - Spring Actuator endpoints (`/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/loggers`) enabled out of the box.
- **CORS Support** - Configurable allowed origins for local development and Azure Static Web Apps–hosted frontends.

---


**Data flow:**

1. IoT sensors (or a simulator) emit telemetry to Azure Stream Analytics.
2. Stream Analytics aggregates readings into time windows and writes results to the `SensorAggregations` container in Cosmos DB.
3. This backend's **Change Feed Processor** detects new documents, deserializes them into `SensorEntity` objects, and maintains an in-memory map of the latest reading per device.
4. Updated sensor data is broadcast as JSON to all connected **WebSocket** clients.
5. The frontend dashboard also has the option to poll via the **REST API** for the latest snapshot.

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.2.21 |
| Framework | Spring Boot | 4.0.5 |
| JDK | Java | 21 |
| Database | Azure Cosmos DB (NoSQL) | — |
| Cosmos SDK | Azure Spring Data Cosmos | 7.1.0 |
| Cloud BOM | Spring Cloud Azure | 7.1.0 |
| Real-Time | Spring WebSocket | 4.0.5 |
| Async | Kotlin Coroutines | 1.10.2 |
| Serialization | Jackson + Kotlin Module | — |
| Security | Spring Security | — |
| Monitoring | Spring Actuator | — |
| Build Tool | Apache Maven (Wrapper) | — |

---


---

## Prerequisites

- **Java 21** (or later)
- **Apache Maven 3.9+** (or use the included Maven Wrapper)
- **Azure Cosmos DB** account with the NoSQL API
- **Git**

---

## Environment Variables

The application requires three environment variables to connect to Azure Cosmos DB. These are referenced in `application.yaml` and bound via Spring's `@ConfigurationProperties`.

| Variable | Description | Example |
|----------|-------------|---------|
| `COSMOS_URI` | Cosmos DB account endpoint | `https://<account>.documents.azure.com:443/` |
| `COSMOS_KEY` | Cosmos DB primary or secondary key | `your-base64-key==` |
| `COSMOS_DATABASE` | Name of the target database | `SensorDB` |

**Set them in your shell before running:**
### Linux/macOS (Bash):
```bash
export COSMOS_URI="https://<your-account>.documents.azure.com:443/"
export COSMOS_KEY="<your-primary-key>"
export COSMOS_DATABASE="<your-database-name>"
```

### On Windows (PowerShell):
```powershell
$env:COSMOS_URI = "https://<your-account>.documents.azure.com:443$env:COSMOS_URI = "https://<your-account>.documents.azure.com:443/"
$env:COSMOS_KEY = "<your-primary-key>"
$env:COSMOS_DATABASE = "<your-database-name>"
```
Security Note: Never commit credentials to version control. Use environment variables, Azure Key Vault, or a .env file excluded via .gitignore.

## Azure Cosmos DB Setup
Create the following two containers in your Cosmos DB database before starting the application (auto-creation is disabled):

| Container          | Partition Key	 | Purpose |
|--------------------|-----|---------|
| SensorAggregations | /location	 | Stores aggregated sensor readings from Stream Analytics |
| SensorLeases       | /id | Internal lease tracking for the Change Feed Processor |

---
## Sensor Document Structure
Each document in the `SensorAggregations` container follows this structure:

```json
{
  "id": "unique-document-id",
  "DeviceId": "sensor-001",
  "location": "Ottawa-Canal",
  "AvgIceThickness": 12.5,
  "MinIceThickness": 10.2,
  "MaxIceThickness": 14.8,
  "AvgSurfaceTemp": -5.3,
  "MinSurfaceTemp": -8.1,
  "MaxSurfaceTemp": -2.5,
  "AvgExternalTemp": -10.0,
  "MaxSnow": 3.2,
  "ReadingCount": 60,
  "WindowStart": "2026-01-15T10:00:00Z",
  "WindowEnd": "2026-01-15T10:05:00Z"
}
```
---
## Local Development
1. Clone the repository:
    ```bash
        git clone https://github.com/Todd-Oneil-CloudDev/cst8916-final-backend.git
        cd cst8916-final-backend
    ```
2. Set the required environment variables for Cosmos DB connection (see above).
3. Build the project using Maven:
    - Linux/macOS:
   ```bash
        mvn spring-boot:run
   ```
    - Windows:
   ```powershell
        mvnw.cmd spring-boot:run
   ```
The application starts on port 8080 by default.

4. Verify
   - Health check: http://localhost:8080/actuator/health
   - Latest sensor data: http://localhost:8080/api/sensors/latest
   - WebSocket: Connect to ws://localhost:8080/ws/sensors
---
## Build & Run
To build the project into a JAR file:
```bash
mvn clean package
```
or if you want to skip the tests:
```bash
mvn clean package -DskipTests
```
This generates `target/cst8916-final-backend-0.0.1-SNAPSHOT.jar

Run the .jar file:
```bash
java -jar target/cst8916-final-backend-0.0.1-SNAPSHOT.jar
```
Make sure the required environment variables are set before running the JAR.

---
## API Reference
### GET /api/sensors/
Returns the latest aggregation window for each sensor location.

Response: 200 OK
```json
[
  {
    "deviceId": "sensor-001",
    "location": "Ottawa-Canal",
    "avgIceThickness": 12.5,
    "minIceThickness": 10.2,
    "maxIceThickness": 14.8,
    "avgSurfaceTemp": -5.3,
    "minSurfaceTemp": -8.1,
    "maxSurfaceTemp": -2.5,
    "avgExternalTemp": -10.0,
    "maxSnow": 3.2,
    "readingCount": 60,
    "windowStart": "2026-01-15T10:00:00Z",
    "windowEnd": "2026-01-15T10:05:00Z"
  },
  ...
]
```

### Actuator Endpoints
| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application info (version, build) |
| `/actuator/metrics` | Application metrics (request counts, response times) |
| `/actuator/loggers` | View and modify logging levels at runtime |

---
## WebSocket Integration
- WebSocket endpoint: `ws://localhost:8080/ws/sensors`

### Behavior
1. Connect to /ws/sensors from your frontend client.
2. The server automatically pushes a JSON payload whenever the Change Feed Processor detects new data in Cosmos DB.
3. The payload is a map of DeviceId → SensorEntity, representing the latest reading for each sensor.

```javascript
// Example WebSocket client in JavaScript
const ws = new WebSocket("ws://localhost:8080/ws/sensors");

ws.onopen = () => console.log("Connected to sensor feed");

ws.onmessage = (event) => {
  const sensorData = JSON.parse(event.data);
  console.log("Live sensor update:", sensorData);
  // Update dashboard charts, gauges, etc.
};

ws.onclose = () => console.log("Disconnected from sensor feed");
ws.onerror = (error) => console.error("WebSocket error:", error);
```

### CORS / Allowed Origins
The WebSocket and REST endpoints accept connections from:
- http://127.0.0.1:5500 — local development (Live Server)
- http://localhost:5500 — local development (Live Server)
- https://your-custom-static-apps-url.net/ — Azure Static Web Apps frontend

To add additional origins, update CorsConfig.kt and WebSocketConfig.kt.

---
## Deployment
### Azure App Service (Recommended for simplicity)
1. Create a new Azure App Service instance (Linux, Java 21).
2. Configure application settings (environment variables) in the Azure Portal.
3. Deploy the JAR file using FTP, Azure CLI, or GitHub Actions.
4. Set up monitoring and scaling as needed.
5. Update your frontend to point to the new backend URL.
6. Test the live data feed and REST API in production.