cd api-gateway
docker build -t camilin19/api-gateway .
cd ..
cd favourite-service
docker build -t camilin19/favourite-service .
cd ..
cd order-service
docker build -t camilin19/order-service .
cd ..
cd payment-service
docker build -t camilin19/payment-service .
cd ..
cd product-service
docker build -t camilin19/product-service .
cd ..
cd user-service
docker build -t camilin19/user-service .
cd ..
cd service-discovery
docker build -t camilin19/service-discovery .
cd ..
cd shipping-service
docker build -t camilin19/shipping-service .
cd ..
cd proxy-client
docker build -t camilin19/proxy-client .
cd ..

docker push camilin19/api-gateway:latest
docker push camilin19/favourite-service:latest
docker push camilin19/order-service:latest
docker push camilin19/payment-service:latest
docker push camilin19/product-service:latest
docker push camilin19/user-service:latest
docker push camilin19/service-discovery:latest
docker push camilin19/shipping-service:latest
docker push camilin19/proxy-client:latest


