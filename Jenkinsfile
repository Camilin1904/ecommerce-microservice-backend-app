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
        
        stage('Clean and Test') {
            steps {
                echo 'Cleaning and running tests...'
                sh 'chmod +x ./mvnw'
                sh './mvnw clean test'
            }
        }
        
        stage('Deploy Services') {
            steps {
                script {
                    echo 'Deploying services using Kubernetes...'
                    sh 'chmod +x ./k8s/deploy-services.sh'
                    sh './k8s/deploy-services.sh'
                    
                    // Wait for services to be ready
                    echo 'Waiting for services to be ready...'
                    sleep 180 // 3 minutes
                    
                    // Check if pods are running
                    sh 'kubectl get pods'
                }
            }
        }
        
        stage('Port Forward API Gateway') {
            steps {
                script {
                    echo 'Setting up port forwarding for API Gateway on port 8080...'
                    // Kill any existing port-forward processes on port 8080
                    sh '''
                        pkill -f "kubectl port-forward.*8080" || true
                        sleep 5
                    '''
                    
                    // Start port forwarding in background
                    sh '''
                        nohup kubectl port-forward service/api-gateway 8080:8080 > port-forward.log 2>&1 &
                        echo $! > port-forward.pid
                        sleep 10
                    '''
                    
                    // Verify port forwarding is working
                    timeout(time: 2, unit: 'MINUTES') {
                        sh '''
                            until $(curl --output /dev/null --silent --head --fail http://localhost:8080/actuator/health); do
                                printf 'Waiting for API Gateway to be available on port 8080...'
                                sleep 10
                            done
                        '''
                    }
                    echo 'API Gateway is now accessible on port 8080!'
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
                    sh 'kubectl get pods -o wide'
                    sh 'kubectl get services'
                    
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
            
            // Cleanup port forwarding
            script {
                sh '''
                    if [ -f port-forward.pid ]; then
                        kill $(cat port-forward.pid) || true
                        rm port-forward.pid
                    fi
                    pkill -f "kubectl port-forward.*8080" || true
                '''
            }
        }
        success {
            echo 'Deployment and E2E tests completed successfully!'
            // You can add notifications here (email, Slack, etc.)
        }
        failure {
            echo 'Pipeline failed!'
            script {
                // Cleanup on failure
                sh '''
                    # Stop port forwarding
                    if [ -f port-forward.pid ]; then
                        kill $(cat port-forward.pid) || true
                        rm port-forward.pid
                    fi
                    pkill -f "kubectl port-forward.*8080" || true
                    
                    # Optional: Cleanup Kubernetes deployments on failure
                    # kubectl delete -f k8s/ || true
                '''
            }
            // You can add notifications here (email, Slack, etc.)
        }
    }
}
