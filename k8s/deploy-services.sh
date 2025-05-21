#!/bin/bash

# Set working directory to the k8s directory
cd "$(dirname "$0")"

echo "Starting deployment in sequence..."

# Step 1: Deploy Zipkin
echo "Deploying Zipkin..."
kubectl apply -f zipkin.yaml
echo "Zipkin deployed. Waiting 30 seconds before deploying Service Discovery..."

# Wait 1 minute
sleep 30

# Step 2: Deploy Service Discovery
echo "Deploying Service Discovery..."
kubectl apply -f service-discovery.yaml
echo "Service Discovery deployed. Waiting 1 minutes before deploying remaining services..."

# Wait 5 minutes
sleep 60

# Step 3: Deploy all remaining services
echo "Deploying all remaining services..."
kubectl apply -f api-gateway.yaml
kubectl apply -f proxy-client.yaml
kubectl apply -f order-service.yaml
kubectl apply -f payment-service.yaml
kubectl apply -f product-service.yaml
kubectl apply -f shipping-service.yaml
kubectl apply -f user-service.yaml
kubectl apply -f favourite-service.yaml

echo "All services have been deployed successfully!"
echo "You can check the status of your deployments with: kubectl get pods"
