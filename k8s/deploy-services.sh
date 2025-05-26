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
echo "Zipkin deployed. Waiting 1 minute before deploying Service Discovery..."

# Wait 1 minute
sleep 60

# Step 2: Deploy Service Discovery
echo "Deploying Service Discovery..."
kubectl apply -f service-discovery.yaml
echo "Service Discovery deployed. Waiting 2 minutes before deploying remaining services..."

# Wait 5 minutes
sleep 120

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

echo "All services have been deployed and exposed successfully!"
echo "You can check the status of your deployments with: kubectl get pods"
echo "You can check the exposed services with: kubectl get services"
