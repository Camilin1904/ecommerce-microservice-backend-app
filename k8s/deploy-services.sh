#!/bin/bash

# Set working directory to the k8s directory
cd "$(dirname "$0")"

# Function to expose a service
expose_service() {
    service_name=$1
    echo "Exposing $service_name service..."
    kubectl expose deployment $service_name --type=NodePort
    echo "$service_name service exposed."
}

echo "Starting deployment in sequence..."

# Step 1: Deploy Zipkin
echo "Deploying Zipkin..."
kubectl apply -f zipkin.yaml
expose_service zipkin
echo "Zipkin deployed. Waiting 1 minute before deploying Service Discovery..."

# Wait 1 minute
sleep 60

# Step 2: Deploy Service Discovery
echo "Deploying Service Discovery..."
kubectl apply -f service-discovery.yaml
expose_service service-discovery
echo "Service Discovery deployed. Waiting 2 minutes before deploying remaining services..."

# Wait 5 minutes
sleep 120

# Step 3: Deploy all remaining services
echo "Deploying all remaining services..."
kubectl apply -f api-gateway.yaml
expose_service api-gateway

kubectl apply -f proxy-client.yaml
expose_service proxy-client

kubectl apply -f order-service.yaml
expose_service order-service

kubectl apply -f payment-service.yaml
expose_service payment-service

kubectl apply -f product-service.yaml
expose_service product-service

kubectl apply -f shipping-service.yaml
expose_service shipping-service

kubectl apply -f user-service.yaml
expose_service user-service

kubectl apply -f favourite-service.yaml
expose_service favourite-service

echo "All services have been deployed and exposed successfully!"
echo "You can check the status of your deployments with: kubectl get pods"
echo "You can check the exposed services with: kubectl get services"
