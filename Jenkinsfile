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
        //IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_ID}" // For example, 'main-91'
        IMAGE_NAME = "${ECR_REPO}:${IMAGE_TAG}" // Full image name with tag
        CLUSTER_NAME = 'apllication-cluster' // EKS cluster name
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
                    // Get the latest git tag for semantic versioning
                    //def latestTag = sh(script: "git describe --tags --abbrev=0", returnStdout: true).trim()
                    //env.IMAGE_TAG = "${latestTag}-${env.BUILD_ID}" // Combine the latest tag with the build ID
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

        stage('Login to AWS ECR') {
            steps {
                script {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}"
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
                    // Tag the Docker image
                    sh "docker tag ${IMAGE_NAME} ${ECR_REPO}:${IMAGE_TAG}"
                    // Push the Docker image to ECR
                    sh "docker push ${ECR_REPO}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                
                script {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        // Update kubeconfig for the EKS cluster
                        sh "aws eks update-kubeconfig --name ${CLUSTER_NAME} --region ${AWS_REGION}"

                        // Use 'sed' or a similar tool to update the image in your deployment file
                        sh "sed -i 's|image: .*|image: ${IMAGE_NAME}|' java-app-deployment.yaml"
                        sh 'kubectl apply -f jenkins-service-account.yaml'
                        sh 'kubectl apply -f jenkins-role.yaml'
                        sh 'kubectl apply -f jenkins-role-binding.yaml'
                        // Apply the Kubernetes deployment configuration
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
