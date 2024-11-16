pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-2'
        CLUSTER_NAME = 'staging-cluster'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/Bennymce/testing-java.git',
                    branch: 'main',
                    credentialsId: 'github-credentials'
            }            
        }

        // Uncomment the following stages when you're ready to deploy
        // stage('Deploy Nginx to EKS') {
        //     steps {
        //         withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
        //             sh '''
        //                 aws configure set region $AWS_REGION
        //                 aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME --role-arn arn:aws:iam::010438494949:role/testing-user
        //                 kubectl apply -f nginx-deployment.yaml
        //             '''
        //         }
        //     }
        // }

        // stage('Verify Deployment') {
        //     steps {
        //         script {
        //             sh '''
        //                 kubectl rollout status deployment/nginx-deployment -n nginx-namespace
        //                 kubectl get svc nginx-loadbalancer-service -n nginx-namespace
        //             '''
        //         }
        //     }
        // }
    }

    post {
        always {
            script {
                // Display all resources in the nginx-namespace
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
