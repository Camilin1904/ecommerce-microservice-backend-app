# Patrones de Diseño Cloud en E-Commerce Microservices

Este documento identifica y describe todos los patrones de diseño cloud implementados en el proyecto de microservicios de e-commerce.

## 1. Patrones de Arquitectura de Microservicios

### 1.1 Service Registry & Discovery Pattern
**Implementación:** Netflix Eureka
- **Archivo:** `service-discovery/`
- **Funcionalidad:** Registro automático y descubrimiento de servicios
- **Ventajas:** Gestión dinámica de instancias de servicios, balanceo de carga automático
- **Evidencia en código:**
  ```java
  @SpringBootApplication
  @EnableEurekaServer
  public class ServiceDiscoveryApplication
  ```

### 1.2 API Gateway Pattern
**Implementación:** Spring Cloud Gateway
- **Archivo:** `api-gateway/`
- **Funcionalidad:** Punto único de entrada para todos los servicios
- **Ventajas:** Enrutamiento, autenticación, rate limiting, monitoreo centralizado
- **Puerto:** 8080
- **Evidencia en código:**
  ```java
  @SpringBootApplication
  @EnableEurekaClient
  public class ApiGatewayApplication
  ```

### 1.3 Proxy/BFF (Backend for Frontend) Pattern
**Implementación:** Proxy Client
- **Archivo:** `proxy-client/`
- **Funcionalidad:** Agregación de servicios y exposición de APIs unificadas
- **Puerto:** 8900
- **Ventajas:** Simplifica la comunicación cliente-servidor
- **Evidencia en código:**
  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @EnableFeignClients
  public class ProxyClientApplication
  ```

## 2. Patrones de Comunicación

### 2.1 Client-Side Service Discovery
**Implementación:** Eureka Client en todos los servicios
- **Funcionalidad:** Los servicios se registran automáticamente en Eureka
- **Evidencia en código:**
  ```java
  @EnableEurekaClient
  ```

### 2.2 Load Balancer Pattern
**Implementación:** Spring Cloud LoadBalancer
- **Funcionalidad:** Distribución automática de cargas entre instancias
- **Evidencia en código:**
  ```java
  @LoadBalanced
  @Bean
  public RestTemplate restTemplateBean() {
      return new RestTemplate();
  }
  ```

### 2.3 Circuit Breaker Pattern
**Implementación:** Resilience4j
- **Funcionalidad:** Prevención de fallos en cascada
- **Evidencia en métricas:** `resilience4j_circuitbreaker_state`
- **Estados:** CLOSED, OPEN, HALF_OPEN

### 2.4 Service-to-Service Communication via HTTP/REST
**Implementación:** OpenFeign
- **Funcionalidad:** Comunicación declarativa entre servicios
- **Evidencia en código:**
  ```java
  @FeignClient(name = "USER-SERVICE", contextId = "userClientService", 
               path = "/user-service/api/users")
  public interface UserClientService
  ```

## 3. Patrones de Configuración

### 3.1 Externalized Configuration Pattern
**Implementación:** Spring Cloud Config Server
- **Archivo:** `cloud-config/`
- **Funcionalidad:** Configuración centralizada y externalizada
- **Puerto:** 9296
- **Evidencia en código:**
  ```java
  @EnableConfigServer
  @EnableEurekaClient
  public class CloudConfigApplication
  ```

### 3.2 Configuration Injection Pattern
**Implementación:** Environment variables y Spring Profiles
- **Evidencia:** Variables de entorno en Docker Compose y Kubernetes
- **Ejemplos:**
  - `SPRING_PROFILES_ACTIVE=dev`
  - `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`

## 4. Patrones de Observabilidad

### 4.1 Distributed Tracing Pattern
**Implementación:** Zipkin
- **Funcionalidad:** Trazabilidad de requests a través de múltiples servicios
- **Puerto:** 9411
- **Configuración:** `SPRING_ZIPKIN_BASE_URL=http://zipkin:9411/`

### 4.2 Health Check Pattern
**Implementación:** Spring Boot Actuator
- **Endpoints:** `/actuator/health`, `/actuator/metrics`
- **Funcionalidad:** Monitoreo del estado de los servicios
- **Evidencia:** Health checks en scripts de despliegue

### 4.3 Metrics Collection Pattern
**Implementación:** Micrometer + Prometheus
- **Endpoint:** `/actuator/prometheus`
- **Métricas:** JVM, HTTP requests, Circuit Breaker, etc.

### 4.4 Centralized Logging Pattern
**Implementación:** Logback + Spring Boot Logging
- **Funcionalidad:** Logs estructurados y centralizados
- **Integración:** Con Zipkin para correlación de trazas

## 5. Patrones de Despliegue

### 5.1 Containerization Pattern
**Implementación:** Docker
- **Evidencia:** Dockerfile en cada servicio
- **Configuración:** Multi-stage builds, environment variables
- **Ejemplo:**
  ```dockerfile
  FROM openjdk:11
  ARG PROJECT_VERSION=0.1.0
  EXPOSE 8761
  ENTRYPOINT ["java", "-jar", "service-discovery.jar"]
  ```

### 5.2 Container Orchestration Pattern
**Implementación:** Docker Compose + Kubernetes
- **Docker Compose:** `compose.yml` para desarrollo local
- **Kubernetes:** Archivos YAML en directorio `k8s/`
- **Evidencia:** Deployments, Services, ConfigMaps

### 5.3 Service Mesh Pattern (Preparado)
**Implementación:** Kubernetes ready
- **Evidencia:** Configuraciones de Service y Deployment en K8s
- **Load Balancer types:** NodePort, LoadBalancer

### 5.4 Blue-Green/Rolling Deployment Pattern
**Implementación:** Kubernetes Deployments
- **Configuración:** `replicas: 1` con capacidad de escalamiento
- **Scripts:** `deploy-services.sh`, `delete-services.sh`

## 6. Patrones de Datos

### 6.1 Database per Service Pattern
**Implementación:** Cada servicio tiene su propia base de datos
- **Servicios identificados:**
  - `user-service`: Gestión de usuarios y credenciales
  - `product-service`: Gestión de productos y categorías
  - `order-service`: Gestión de órdenes y carritos
  - `payment-service`: Gestión de pagos
  - `shipping-service`: Gestión de envíos
  - `favourite-service`: Gestión de favoritos

### 6.2 Saga Pattern (Implícito)
**Implementación:** Comunicación entre servicios para transacciones distribuidas
- **Evidencia:** Comunicación entre Order, Payment y Shipping services
- **Coordinación:** A través del Proxy Client

## 7. Patrones de Seguridad

### 7.1 API Gateway Security Pattern
**Implementación:** Seguridad centralizada en API Gateway
- **Funcionalidad:** Authentication y Authorization en el gateway
- **Evidencia:** Configuración de security en el proyecto

### 7.2 Service-to-Service Authentication
**Implementación:** Internal service calls
- **Evidencia:** FeignClient configurations con service names

## 8. Patrones de Testing

### 8.1 Contract Testing Pattern
**Implementación:** Scripts de testing automatizado
- **Archivo:** `test-em-all.sh`
- **Funcionalidad:** Testing end-to-end de todos los servicios

### 8.2 Integration Testing Pattern
**Implementación:** Tests por servicio
- **Evidencia:** Directorios `src/test` en cada servicio
- **Tipos:** Unit tests e integration tests

## 9. Patrones de CI/CD

### 9.1 Pipeline as Code Pattern
**Implementación:** Jenkins Pipeline
- **Archivo:** `Jenkinsfile`
- **Stages:** Build, Test, Deploy, Verify
- **Evidencia:**
  ```groovy
  pipeline {
      agent any
      stages {
          stage('Build Services') { ... }
          stage('Deploy Services') { ... }
      }
  }
  ```

### 9.2 Infrastructure as Code Pattern
**Implementación:** Kubernetes YAML manifests
- **Archivos:** `k8s/*.yaml`
- **Recursos:** Deployments, Services, ConfigMaps
- **Scripts:** Automatización de despliegue

### 9.3 Environment Promotion Pattern
**Implementación:** Múltiples archivos de pipeline
- **Archivos:** `Jenkinsfile.dev`, `Jenkinsfile.stage`, `Jenkinsfile.prod`
- **Configuración:** Diferentes perfiles por ambiente

## 10. Patrones de Resiliencia

### 10.1 Bulkhead Pattern
**Implementación:** Aislamiento de servicios
- **Evidencia:** Servicios independientes con sus propios recursos
- **Configuración:** Contenedores separados por servicio

### 10.2 Retry Pattern
**Implementación:** Resilience4j
- **Configuración:** En las comunicaciones entre servicios
- **Evidencia:** Métricas de circuit breaker

### 10.3 Timeout Pattern
**Implementación:** Configuraciones de timeout en FeignClient
- **Evidencia:** Circuit breaker metrics y health checks

## 11. Patrones de Escalabilidad

### 11.1 Horizontal Scaling Pattern
**Implementación:** Kubernetes replicas
- **Configuración:** `replicas` en Deployment manifests
- **Load Balancing:** Automático via Kubernetes Services

### 11.2 Auto-Scaling Pattern (Preparado)
**Implementación:** Kubernetes ready
- **Evidencia:** Configuraciones de resources en manifests
- **Posibilidad:** HPA (Horizontal Pod Autoscaler)

## 12. Patrones de Cache

### 12.1 Shared Cache Pattern
**Implementación:** Cacheo a nivel de aplicación
- **Evidencia:** Configuraciones de Spring Cache (implícito)

## Resumen de Servicios y Puertos

| Servicio | Puerto | Función |
|----------|--------|---------|
| service-discovery | 8761 | Registry & Discovery |
| api-gateway | 8080 | API Gateway |
| proxy-client | 8900 | BFF/Proxy |
| user-service | 8700 | Gestión de usuarios |
| product-service | 8500 | Gestión de productos |
| order-service | 8300 | Gestión de órdenes |
| payment-service | 8400 | Gestión de pagos |
| shipping-service | 8600 | Gestión de envíos |
| favourite-service | 8800 | Gestión de favoritos |
| zipkin | 9411 | Distributed Tracing |
| cloud-config | 9296 | Configuration Server |

## Conclusión

Este proyecto implementa una arquitectura de microservicios robusta siguiendo las mejores prácticas de cloud-native applications, con un total de **20+ patrones de diseño cloud** implementados, proporcionando alta disponibilidad, escalabilidad, observabilidad y resiliencia.
