# Product Server

A Micronaut-based REST API server built with Kotlin for managing products. This application provides endpoints for retrieving, updating product information.

## Features

- RESTful API for product management
- Built with Micronaut framework and Kotlin
- OpenAPI/Swagger documentation
- Docker support
- High-performance with Chronicle Map for in-memory storage

## Prerequisites

- Docker (for containerized deployment)
- Java 21
- Gradle (for local development)

## Running with Docker

### Build and Run

1. Build the Docker image:
   ```bash
   ./gradlew dockerBuild
   ```

2. Run the container:
   ```bash
   docker run --rm -p 8080:8080 product-server:latest
   ```

The application will be available at `http://localhost:8080`

### Docker Compose (Optional)

You can also use Docker Compose for easier management:

```yaml
version: '3.8'
services:
  product-server:
    image: product-server:latest
    ports:
      - "8080:8080"
    environment:
      - MICRONAUT_ENVIRONMENTS=docker
```

## API Documentation

### OpenAPI Schema
The complete OpenAPI specification is available at:
- **OpenAPI Schema**: http://localhost:8080/swagger/streaem-products-api-1.0.yml

### Swagger UI
Interactive API documentation and testing interface:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
