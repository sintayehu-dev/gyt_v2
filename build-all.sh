#!/bin/bash

# Build all microservices
echo "Building all microservices..."

services=("auth-service" "user-service" "movie-service" "schedule-service" "ticket-service" "api-gateway" "config-server" "discovery-server")

for service in "${services[@]}"
do
    echo "Building $service..."
    cd $service
    ./mvnw clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✓ $service built successfully"
    else
        echo "✗ $service build failed"
        exit 1
    fi
    cd ..
done

echo ""
echo "All services built successfully!"
echo "You can now run: docker-compose up -d"
