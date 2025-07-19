
# Translation Service

## Overview
This is a Translation Service implemented using Java 21, Spring Boot 3.5.3 with reactive WebFlux and reactive Elasticsearch (9.0.3). The service exposes REST APIs secured by JWT token-based authentication, supporting multiple locales and tags for translations.

## Features
- Store translations for multiple locales (e.g., EN, FR, ES) and support adding new locales.
- Tag translations by context (e.g., mobile, desktop, web).
- CRUD APIs for translations with search by key, tag, locale, and value.
- Performance optimized to return responses in milliseconds (< 200ms for search, < 500ms for large JSON exports).
- Bulk data loader for 100k+ records to test scalability.
- Secure APIs using JWT tokens.
- OpenAPI/Swagger documentation available.
- Profiles (`test`, `dev`) to enable sample data population for testing.
- Dockerized for easy setup and deployment.

## Technology Stack
- Java 21
- Spring Boot 3.5.3
- Spring WebFlux (Reactive REST APIs)
- Spring Data Reactive Elasticsearch 9.0.3
- JWT for API security
- Lombok for boilerplate reduction
- Reactor (Mono, Flux) for reactive programming
- Docker & Docker Compose

## Getting Started

### Prerequisites
- Java 21 JDK installed
- Docker & Docker Compose installed
- Maven installed

### Running with Maven
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
This will start the application with the `dev` profile, which populates sample data automatically.

### Running with Docker Compose
```bash
docker-compose up
```
This will build and start the application along with Elasticsearch.

### Profiles
- `test` and `dev` profiles populate 100k+ dummy translation records on startup for testing scalability.
- Default profile starts with no data.

## API Documentation
Swagger UI is available at:  
[http://localhost:8086/translation-service/swagger-ui/index.html](http://localhost:8086/translation-service/swagger-ui/index.html)

You can explore all REST endpoints here.

## Security
- APIs are secured with JWT token-based authentication.
- Sample JWT token for testing (valid for 1 year):

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3NTI5MzU1OTksImV4cCI6MTg0NzYyOTk5OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsInVzZXJuYW1lIjoiSm9obm55IiwiU3VybmFtZSI6IlJvY2tldCIsIkVtYWlsIjoianJvY2tldEBleGFtcGxlLmNvbSIsIlJvbGUiOlsiTWFuYWdlciIsIlByb2plY3QgQWRtaW5pc3RyYXRvciJdfQ.v107JdeVWs9Dflodri1uQHZY8BJPrRmoPDpXcVuuqt8
```

## Access Actuator ENDPOINTS - To check service health
[http://localhost:9081/actuator](http://localhost:9081/actuator)

## Folder Structure (Important Parts)
```
src/main/java/se/digitaltolk/translation/
├── annotation       # Custom annotations
├── config           # Spring & Elasticsearch configurations
├── rest             # REST controllers and DTOs
│   ├── dto
│   └── rest
├── exception        # Custom exception classes and handlers
├── filter           # Web filters
├── mapper           # DTO to Entity mappers
├── repository       # Elasticsearch repository interfaces and entities
├── security         # JWT security, token introspection, and WebFlux security config
├── service          # Business logic and services
├── validator        # Request validators
└── TranslationTestDataLoader.java  # Data loader for dummy translations (test/dev profiles)
```

## Docker
- `Dockerfile` is provided for the Spring Boot app.
- `docker-compose.yaml` includes Elasticsearch and the Spring Boot service for easy startup.

## Performance & Scalability
- Reactive Elasticsearch and WebFlux ensure non-blocking, fast API responses.
- Bulk loader creates 100k+ dummy translations to test performance.
- JSON export endpoint optimized for large datasets, response < 500ms.

## Notes
- This service follows SOLID principles and PSR-12 standards where applicable.
- JWT tokens are verified and introspected for each request.
- Swagger documentation covers all available APIs.
- Test and dev profiles auto-load dummy data to simulate realistic load.

---

🧠 Why Elasticsearch for Translation Service?

Elasticsearch was chosen as the storage and search engine for this translation service due to its speed, scalability, and flexibility, which are essential for serving localized content in real-time across multiple platforms and locales.
---
✅ Key Advantages:

    🔍 Full-Text and Structured Search
    Translations often require efficient searching by key, value, locale, and tag. Elasticsearch supports rich querying with full-text search, filtering, and aggregations, making it ideal for dynamic translation lookups.

    🧾 Schema Flexibility
    Translation entries vary across languages and platforms. Elasticsearch allows flexible document structures, letting us store translations as flat JSON documents without rigid schemas.

    ⚡ High Performance and Low Latency
    Elasticsearch distributes data into shards, enabling parallel processing. This architecture delivers fast read and write performance, especially important for UI rendering and localization.

    📈 Horizontal Scalability
    By increasing the number of shards and nodes, the system can handle higher data volumes and concurrent requests. This ensures the service remains responsive as usage grows.

    📈 Zero downtime on failures
    If a node crashes, replicas in other nodes automatically serve requests without data loss (thanks to Elasticsearch’s distributed consensus).

    📚 Replica Scaling for High Read Load
    To handle high read throughput, we configure multiple replica shards for each index. These replicas serve read requests in parallel, especially useful when the translation service is deployed behind a load balancer. 
    Under load, the balancer can distribute requests across multiple coordinating nodes and replicas, significantly improving read performance and fault tolerance.

    🔄 Reactive and Non-blocking Integration
    Elasticsearch integrates well with Spring WebFlux, enabling reactive, non-blocking data flows—ideal for high-concurrency environments such as microservices or mobile backends.


## Elasticsearch High Availability Architecture

```mermaid
graph TD
    A[Load Balancer] --> B[Primary Shard]
    A --> C[Replica Shard 1]
    A --> D[Replica Shard 2]
    style B fill:#f9f,stroke:#333
```
    Automatic failover: If Node 1 fails, replicas (Nodes 2-3) take over instantly

    Zero data loss: Transaction logs ensure durability

    Seamless scaling: Add replicas without downtime

Thank you for checking out this Translation Service!

