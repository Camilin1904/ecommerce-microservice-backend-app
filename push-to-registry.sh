#!/bin/bash

# Set version for tagging
REGISTRY="camilin19"

# Run Maven build once for all services
./mvnw clean package -DskipTests

# Build all services using Docker Compose
echo "Building all services using Docker Compose..."
docker compose build --no-cache

# Verify images were created correctly
echo "Verifying built images..."
docker images | grep ${REGISTRY}

docker login 

# Push latest tags
echo "Pushing latest tags to registry..."
docker push ${REGISTRY}/api-gateway:latest
docker push ${REGISTRY}/favourite-service:latest
docker push ${REGISTRY}/order-service:latest
docker push ${REGISTRY}/payment-service:latest
docker push ${REGISTRY}/product-service:latest
docker push ${REGISTRY}/user-service:latest
docker push ${REGISTRY}/service-discovery:latest
docker push ${REGISTRY}/shipping-service:latest
docker push ${REGISTRY}/proxy-client:latest

echo "All services built and pushed successfully!"


