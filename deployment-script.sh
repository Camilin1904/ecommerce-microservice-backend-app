#!/bin/bash

# Deployment script that respects service dependency order
echo "Starting microservices deployment..."

# Step 1: Deploy Zipkin
echo "Deploying Zipkin..."
docker-compose up -d zipkin-container

# Wait for Zipkin to be healthy
echo "Waiting for Zipkin to be healthy..."
while ! curl -s http://localhost:9411/health > /dev/null; do
  echo "Zipkin is not ready yet... waiting 5 seconds"
  sleep 5
done
echo "Zipkin is running!"

# Step 2: Deploy Service Discovery
echo "Deploying Service Discovery..."
docker-compose up -d service-discovery

# Wait for Service Discovery to be healthy
echo "Waiting for Service Discovery to be healthy..."
while ! curl -s http://localhost:8761/actuator/health > /dev/null; do
  echo "Service Discovery is not ready yet... waiting 10 seconds"
  sleep 10
done
echo "Service Discovery is running!"

# Step 3: Deploy all other services in parallel
echo "Deploying all other microservices..."
docker-compose up -d api-gateway-container proxy-client-container order-service-container payment-service-container product-service-container shipping-service-container user-service-container favourite-service-container

echo "All services deployed! Checking status..."
docker-compose ps

echo "Deployment complete!"
