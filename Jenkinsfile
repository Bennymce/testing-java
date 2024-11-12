pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-2'
        CLUSTER_NAME = 'dev-cluster'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/Bennymce/testing-java.git',
                    branch: 'main',
                    credentialsId: 'github-credentials'
            }            
        }

        stage('Configure AWS CLI') {
            steps {
                // Set up AWS CLI and ensure eksctl is available
                sh '''
                    aws configure set region $AWS_REGION
                    aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME
                '''
            }
        }

        stage('Deploy Nginx to EKS') {
            steps {
                script {
                    // Apply the deployment and service YAML files to the EKS cluster
                    sh '''
                        kubectl apply -f nginx-deployment.yaml
                        
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    // Check the status of the Nginx deployment and service
                    sh '''
                        kubectl rollout status deployment/nginx-deployment -n nginx-namespace
                        kubectl get svc nginx-loadbalancer-service -n nginx-namespace
                    '''
                }
            }
        }
    }
    
    post {
        always {
            script {
                // Clean up or print resource status
                sh 'kubectl get all -n nginx-namespace'
            }
        }
        success {
            echo 'Nginx successfully deployed to EKS!'
        }
        failure {
            echo 'Failed to deploy Nginx to EKS.'
        }
    }
}
