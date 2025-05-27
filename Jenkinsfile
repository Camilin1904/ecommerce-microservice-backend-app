pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-docker-registry'
        PROJECT_VERSION = '0.1.0'
        COMPOSE_FILE = 'compose.yml'
        API_GATEWAY_PORT = '8080'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Clean and Test with Java 11') {
            steps {
                script {
                    echo 'Running tests in Java 11 Docker container...'
                    sh '''
                        # Run tests using a Java 11 Docker container
                        docker run --rm \
                            -v "$(pwd)":/workspace \
                            -w /workspace \
                            openjdk:11-jdk-slim \
                            bash -c "
                                echo 'Java version in container:'
                                java -version
                                
                                echo 'Making mvnw executable...'
                                chmod +x ./mvnw
                                
                                echo 'Running Maven tests...'
                                ./mvnw clean test
                            "
                    '''
                }
            }
        }
        
        stage('Build and Deploy Services') {
            steps {
                script {
                    echo 'Building and deploying services using Docker Compose...'
                    
                    // Build all services
                    sh 'docker-compose -f ${COMPOSE_FILE} build'
                    
                    // Deploy services in the right order
                    echo 'Starting Zipkin...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d zipkin'
                    
                    // Wait for Zipkin to be ready
                    timeout(time: 2, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:9411/health); do
                                printf 'Waiting for Zipkin...'
                                sleep 5
                            done
                        '''
                    }
                    echo 'Zipkin is ready!'
                    
                    echo 'Starting Service Discovery...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d service-discovery'
                    
                    // Wait for Service Discovery to be ready
                    timeout(time: 3, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:8761/actuator/health); do
                                printf 'Waiting for Service Discovery...'
                                sleep 10
                            done
                        '''
                    }
                    echo 'Service Discovery is ready!'
                    
                    echo 'Starting all other services...'
                    sh 'docker-compose -f ${COMPOSE_FILE} up -d'
                    
                    // Wait for services to be ready
                    echo 'Waiting for all services to be ready...'
                    sleep 120 // 2 minutes
                    
                    // Check if containers are running
                    sh 'docker-compose -f ${COMPOSE_FILE} ps'
                }
            }
        }
        
        stage('Verify API Gateway') {
            steps {
                script {
                    echo 'Verifying API Gateway is accessible on port 8080...'
                    // The API Gateway should already be running on port 8080 from docker-compose
                    timeout(time: 3, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:8080/actuator/health); do
                                printf 'Waiting for API Gateway to be available on port 8080...'
                                sleep 10
                            done
                        '''
                    }
                    echo 'API Gateway is accessible on port 8080!'
                }
            }
        }
        
        
        stage('Run E2E Tests') {
            steps {
                script {
                    echo 'Installing E2E test dependencies...'
                    dir('e2e') {
                        sh 'npm install'
                        
                        echo 'Running E2E tests...'
                        sh 'npm run test:html-report'
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    echo 'Verifying all services are running...'
                    sh 'docker compose -f ${COMPOSE_FILE} ps'
                    sh 'docker ps --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"'
                    
                    // Verify API Gateway is accessible
                    sh 'curl -s http://localhost:8080/actuator/health'
                    
                    echo 'Deployment verification completed successfully!'
                }
            }
        }
    }
    
    post {
        always {
            // Archive E2E test reports
            archiveArtifacts artifacts: 'e2e/reports/**', allowEmptyArchive: true
        }
        success {
            echo 'Deployment and E2E tests completed successfully!'
            // You can add notifications here (email, Slack, etc.)
        }
        failure {
            echo 'Pipeline failed!'
            script {
                // Cleanup on failure - stop all Docker Compose services
                sh '''
                    echo "Cleaning up Docker Compose services..."
                    docker compose -f ${COMPOSE_FILE} down || true
                '''
            }
            // You can add notifications here (email, Slack, etc.)
        }
    }
}
