# Script to build and push all microservice images to a Docker registry
# Usage: .\push-to-registry.ps1 -RegistryUrl <registry-url> -Tag <tag>
# Example: .\push-to-registry.ps1 -RegistryUrl docker.io/username -Tag 1.0.0

param (
    [Parameter(Mandatory=$true)]
    [string]$RegistryUrl,
    
    [Parameter(Mandatory=$false)]
    [string]$Tag = "latest"
)

$ProjectVersion = "0.1.0"

# List of all services
$Services = @(
    "service-discovery",
    "api-gateway",
    "favourite-service",
    "shipping-service",
    "product-service",
    "payment-service",
    "order-service",
    "user-service",
    "proxy-client",
    "cloud-config"
)

Write-Host "🚀 Starting to build and push images to $RegistryUrl with tag $Tag" -ForegroundColor Green

# Check if Docker is installed
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Docker is not installed. Please install Docker first." -ForegroundColor Red
    exit 1
}

# Check if user is logged in to Docker registry
Write-Host "🔑 Please ensure you're logged in to the Docker registry" -ForegroundColor Yellow
Write-Host "    If not, run: docker login $RegistryUrl" -ForegroundColor Yellow

# Function to build and push a service
function Build-And-Push {
    param (
        [string]$Service
    )
    
    Write-Host "📦 Processing $Service..." -ForegroundColor Cyan
    
    # Check if directory exists
    if (-not (Test-Path "./$Service")) {
        Write-Host "⚠️ Warning: Directory ./$Service not found. Skipping..." -ForegroundColor Yellow
        return
    }
    
    # Check if Dockerfile exists
    if (-not (Test-Path "./$Service/Dockerfile")) {
        Write-Host "⚠️ Warning: Dockerfile not found in ./$Service. Skipping..." -ForegroundColor Yellow
        return
    }
    
    # Build the Docker image
    Write-Host "🔨 Building image for $Service..." -ForegroundColor Blue
    docker build -t "$Service`:$Tag" `
        --build-arg PROJECT_VERSION=$ProjectVersion `
        -f "./$Service/Dockerfile" "./$Service"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to build image for $Service" -ForegroundColor Red
        return
    }
    
    # Tag the image with registry URL
    Write-Host "🏷️ Tagging $Service as $RegistryUrl/$Service`:$Tag" -ForegroundColor Blue
    docker tag "$Service`:$Tag" "$RegistryUrl/$Service`:$Tag"
    
    # Push to registry
    Write-Host "☁️ Pushing $RegistryUrl/$Service`:$Tag" -ForegroundColor Blue
    docker push "$RegistryUrl/$Service`:$Tag"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to push image for $Service" -ForegroundColor Red
        return
    }
    
    Write-Host "✅ Successfully pushed $Service" -ForegroundColor Green
}

# Loop through all services and process them
foreach ($Service in $Services) {
    try {
        Build-And-Push -Service $Service
    } catch {
        Write-Host "❌ Error processing $Service`: $_" -ForegroundColor Red
        # Continue with next service
        continue
    }
}

Write-Host "🎉 All images have been built and pushed to $RegistryUrl" -ForegroundColor Green
Write-Host "📋 Summary:" -ForegroundColor Cyan
foreach ($Service in $Services) {
    Write-Host "  - $RegistryUrl/$Service`:$Tag"
}
