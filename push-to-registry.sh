#!/bin/bash
set -e

# Build all images using docker compose to ensure consistency with compose.yml
docker compose build



# Push images to the registry
docker push camilin19/api-gateway:latest
docker push camilin19/favourite-service:latest
docker push camilin19/order-service:latest
docker push camilin19/payment-service:latest
docker push camilin19/product-service:latest
docker push camilin19/user-service:latest
docker push camilin19/service-discovery:latest
docker push camilin19/shipping-service:latest
docker push camilin19/proxy-client:latest