#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Define registry and username
REGISTRY_USER="camilin19"
VERSION_TAG=$(date +"%Y%m%d-%H%M%S")
PROJECT_NAME="ecommerce-microservice-backend-app"

echo -e "${YELLOW}Logging in to Docker Hub...${NC}"
docker login

echo -e "${YELLOW}Building all images without using cache...${NC}"
# Build all images using docker compose with no-cache to ensure fresh builds
docker compose build --no-cache

# Define services and their container names
declare -A SERVICES=(
  ["api-gateway"]="api-gateway-container"
  ["favourite-service"]="favourite-service-container"
  ["order-service"]="order-service-container"
  ["payment-service"]="payment-service-container"
  ["product-service"]="product-service-container"
  ["user-service"]="user-service-container"
  ["service-discovery"]="service-discovery"
  ["shipping-service"]="shipping-service-container"
  ["proxy-client"]="proxy-client-container"
)

# Tag and push all images
for service in "${!SERVICES[@]}"; do
  container="${SERVICES[$service]}"
  
  # Determine the Docker Compose generated image name
  compose_image="${PROJECT_NAME}-${container}"
  
  echo -e "${YELLOW}Tagging ${compose_image} as ${REGISTRY_USER}/${service}:latest${NC}"
  
  # First tag with registry username
  docker tag "${compose_image}" "${REGISTRY_USER}/${service}:latest" || {
    echo -e "${RED}Failed to tag ${compose_image} - trying alternate naming...${NC}"
    # Try alternate naming scheme
    docker tag "${container}" "${REGISTRY_USER}/${service}:latest" || {
      echo -e "${RED}Failed to tag ${service} - skipping...${NC}"
      continue
    }
  }
  
  # Then tag with version
  echo -e "${YELLOW}Tagging ${REGISTRY_USER}/${service} with version ${VERSION_TAG}...${NC}"
  docker tag "${REGISTRY_USER}/${service}:latest" "${REGISTRY_USER}/${service}:${VERSION_TAG}" || {
    echo -e "${RED}Failed to create version tag for ${service} - skipping...${NC}"
    continue
  }
  
  echo -e "${YELLOW}Pushing ${service}:latest...${NC}"
  docker push "${REGISTRY_USER}/${service}:latest" || { 
    echo -e "${RED}Failed to push ${service}:latest${NC}"
    continue
  }
  
  echo -e "${YELLOW}Pushing ${service}:${VERSION_TAG}...${NC}"
  docker push "${REGISTRY_USER}/${service}:${VERSION_TAG}" || { 
    echo -e "${RED}Failed to push ${service}:${VERSION_TAG}${NC}"
    continue
  }
  
  echo -e "${GREEN}Successfully pushed ${service}${NC}"
done

echo -e "${GREEN}All images have been processed!${NC}"
echo -e "${GREEN}Version tag: ${VERSION_TAG}${NC}"

# Create a version file for reference
echo "${VERSION_TAG}" > ./latest-version.txt
echo -e "${YELLOW}Version recorded in latest-version.txt${NC}"