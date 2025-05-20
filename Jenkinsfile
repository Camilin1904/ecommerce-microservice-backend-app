pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-docker-registry'
        PROJECT_VERSION = '0.1.0'
        COMPOSE_FILE = 'compose.yml'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Services') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    // Build all service images
                    sh 'docker-compose -f ${COMPOSE_FILE} build'
                }
            }
        }
        
        stage('Deploy Zipkin') {
            steps {
                script {
                    echo 'Deploying Zipkin...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d zipkin-container'
                    
                    // Wait for Zipkin to be healthy
                    timeout(time: 2, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:9411/health); do
                                printf '.'
                                sleep 5
                            done
                        '''
                    }
                    echo 'Zipkin is up and running!'
                }
            }
        }
        
        stage('Deploy Service Discovery') {
            steps {
                script {
                    echo 'Deploying Service Discovery...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d service-discovery-container'
                    
                    // Wait for Service Discovery to be healthy
                    timeout(time: 3, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:8761/actuator/health); do
                                printf '.'
                                sleep 10
                            done
                        '''
                    }
                    echo 'Service Discovery is up and running!'
                }
            }
        }
        
        stage('Deploy Other Services') {
            steps {
                script {
                    echo 'Deploying API Gateway...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d api-gateway-container'
                    
                    echo 'Deploying Proxy Client...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d proxy-client-container'
                    
                    echo 'Deploying Order Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d order-service-container'
                    
                    echo 'Deploying Payment Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d payment-service-container'
                    
                    echo 'Deploying Product Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d product-service-container'
                    
                    echo 'Deploying Shipping Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d shipping-service-container'
                    
                    echo 'Deploying User Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d user-service-container'
                    
                    echo 'Deploying Favourite Service...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d favourite-service-container'
                }
            }
        }
        
        stage('Verify All Services') {
            steps {
                script {
                    echo 'Verifying all services...'
                    sh 'docker-compose -f ${COMPOSE_FILE} ps'
                    
                    // Check health endpoints for all services
                    timeout(time: 5, unit: 'MINUTES') {
                        sh '''
                            # Check service endpoints
                            curl -s http://localhost:8081/actuator/health
                            curl -s http://localhost:8900/actuator/health
                            curl -s http://localhost:8300/actuator/health
                            curl -s http://localhost:8400/actuator/health
                            curl -s http://localhost:8500/actuator/health
                            curl -s http://localhost:8600/actuator/health
                            curl -s http://localhost:8700/actuator/health
                            curl -s http://localhost:8800/actuator/health
                            echo "All services verified!"
                        '''
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
            // You can add notifications here (email, Slack, etc.)
        }
        failure {
            echo 'Deployment failed!'
            // Cleanup on failure
            sh 'docker-compose -f ${COMPOSE_FILE} down'
            // You can add notifications here (email, Slack, etc.)
        }
    }
}
