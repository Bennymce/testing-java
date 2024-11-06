pipeline {
    agent any
    tools {
        maven 'app-maven'
        dockerTool 'app-docker'
    }

    environment {
        AWS_REGION = 'us-east-2'
        ECR_REPO = '010438494949.dkr.ecr.us-east-2.amazonaws.com/jenkins-repo'
        IMAGE_TAG = "${env.VERSION}-${env.BUILD_ID}" // Use the fetched version for the image tag
        IMAGE_NAME = "${ECR_REPO}:${IMAGE_TAG}" // Full image name with tag
        CLUSTER_NAME = 'test-cluster' // EKS cluster name
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/Bennymce/Deploying-to-eks-using-jenkins.git',
                    branch: 'main',
                    credentialsId: 'github-credentials'
            }
        }

        stage('Get Version') {
            steps {
                script {
                    def tags = sh(script: "git tag", returnStdout: true).trim()
                    if (tags) {
                        env.VERSION = sh(script: "git describe --tags --abbrev=0", returnStdout: true).trim()
                    } else {
                        echo 'No tags found. Using default version.'
                        env.VERSION = "default-version" // Set a default version if no tags are present
                    }
                }
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify JAR File') {
            steps {
                sh 'ls -la target/myapp-1.0-SNAPSHOT.jar'
            }
        }

        stage('Login to ECR') {
            steps {
                script {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        echo "Logging in to AWS ECR..."
                        def loginCommand = "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}"
                        try {
                            sh loginCommand
                            echo "Login successful!"
                        } catch (Exception e) {
                            error "Login to ECR failed: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Tag and Push Docker Image to ECR') {
            steps {
                script {
                    echo 'Pushing Docker image to ECR...'
                    try {
                        sh "docker push ${IMAGE_NAME}"
                        echo "Docker image pushed successfully!"
                    } catch (Exception e) {
                        error "Failed to push Docker image: ${e.getMessage()}"
                    }
                }
            }
        }

       
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        // Update kubeconfig for the EKS cluster
                        sh "aws eks update-kubeconfig --name ${CLUSTER_NAME} --region ${AWS_REGION}"

                        // Update image in the Kubernetes deployment file
                        sh "sed -i 's|image: .*|image: ${IMAGE_NAME}|' java-app-deployment.yaml"

                        // Apply Kubernetes configurations
                        // sh 'kubectl create namespace testing || true'
                        sh 'kubectl apply -f jenkins-service-account.yaml'
                        sh 'kubectl apply -f jenkins-role.yaml'
                        sh 'kubectl apply -f jenkins-role-binding.yaml'
                        sh 'kubectl apply -f storage-class.yaml'
                        sh 'kubectl apply -f pvc.yaml'
                        sh 'kubectl apply -f java-app-deployment.yaml'

                        // Check the status of the pods
                        sh 'kubectl get pods --namespace=testing'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Clean the workspace after the build
        }
    }
  }
