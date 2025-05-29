#!/bin/bash
set -e

# Helper script for Jenkins deployment

# Function to check if service is healthy
check_health() {
  local service_name=$1
  local url=$2
  local max_attempts=$3
  local wait_time=$4
  
  echo "Checking health of $service_name at $url..."
  local attempt=1
  
  while [ $attempt -le $max_attempts ]; do
    echo "Health check attempt $attempt/$max_attempts..."
    if curl -s -f $url > /dev/null 2>&1; then
      echo "$service_name is healthy!"
      return 0
    else
      echo "$service_name not healthy yet, waiting..."
      sleep $wait_time
      attempt=$((attempt + 1))
    fi
  done
  
  echo "Error: $service_name failed health check after $max_attempts attempts"
  return 1
}

# Deploy Zipkin
echo "Deploying Zipkin..."
docker-compose -f compose.yml up -d zipkin-container
check_health "Zipkin" "http://localhost:9411/health" 12 5

# Deploy Service Discovery
echo "Deploying Service Discovery..."
docker-compose -f compose.yml up -d service-discovery-container
check_health "Service Discovery" "http://localhost:8761/actuator/health" 18 10

# Deploy all other services
echo "Deploying remaining services..."
docker-compose -f compose.yml up -d api-gateway-container proxy-client-container order-service-container payment-service-container product-service-container shipping-service-container user-service-container favourite-service-container

# Final verification
echo "Deployment complete! Current service status:"
docker-compose -f compose.yml ps
