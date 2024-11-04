# deploying-EKS-App-using-jenkins
Deploying an EKS app from Jenkins pipeline 
DEPLOY AN APP TO EKS EKS CLUSTER FROM JENKINS PIPELINE


For this task, i'm running jenkins as a docker container using 
docker pull jenkins/jenkins:2.414.3-jdk17

I already have an empty running EKS cluster 

run jenkins as a root user to have root priviledges using docker exec -u 0 -it
containerid or name> /bin/bash
# Deploying-to-eks-using-jenkins




ssh -i jenkins-server.pem ubuntu@18.188.23.85
sh 'sudo docker build -t my-java-app:test ./simple-java-app'


eksctl create cluster --name deploy-cluster --region us-east-2 --nodes 2
aws eks update-kubeconfig --name demo-cluster --region us-east-2 --role-arn arn:aws:iam::010438494949:role/jenkins-eks-role


To update my kube config file 




kubectl create namespace <namespace-name>

scp /path/to/your/local/file username@jenkins-server:/var/lib/jenkins/workspace/my-job/
cp /home/ubuntu/Deploying-to-eks-using-jenkins/kubeconfig /var/lib/jenkins/workspace/my-job/
ls -l /var/lib/jenkins/workspace/



mkdir -p /var/lib/jenkins/workspace/my-job/


Set permissions
Ensure that the jenkins user has permission to access and modify this directory:
sudo chown -R jenkins:jenkins /var/lib/jenkins/workspace/my-job


Verify the copy
Check if the kubeconfig file is now in the my-job directory:
ls -l /var/lib/jenkins/workspace/my-job/




        which aws-iam-authenticator
/usr/local/bin/aws-iam-authenticator


export KUBECONFIG=~/.kube/kubecconfig-java
kubectl get nodes
export KUBECONFIG=$KUBECONFIG:~/.kube/kubecconfig-java

https://oidc.eks.us-east-2.amazonaws.com/id/16852A7F964C0526BB485F76C211CC41


kubectl annotate serviceaccount jenkins-service-account \
  -n jenkins \
  eks.amazonaws.com/role-arn=arn:aws:iam::010438494949:role/jenkins-ecr-access-role


secret
 kubectl get ServiceAccount jenkins-service-account -n jenkins

 to generate a secret/token name jenkins-service-account-token or create secret using a yaml file 
 kubectl create secret generic jenkins-service-account-token --from-literal=token=$(openssl rand -hex 32) --namespace jenkins

 bind secret to the serviceaccount
kubectl patch serviceaccount jenkins-service-account -n jenkins -p '{"secrets": [{"name": "jenkins-service-account-token"}]}'

retrieve token 
kubectl get secret jenkins-service-account-token -n jenkins -o jsonpath='{.data.token}' | base64 --decode
kubectl get secret $(kubectl get serviceaccount jenkins-service-account -n jenkins -o jsonpath='{.secrets[0].name}') -n jenkins -o jsonpath='{.data.token}' | base64 --decode
 then use the decoded token value in the token part for the kubeconfig file 

verify secret is attcahed to servcie account 
kubectl get serviceaccount jenkins-service-account -n jenkins -o yaml








      


    
       aws eks describe-cluster --name benny-java-cluster --query "cluster.identity.oidc.issuer" --output text
    aws iam create-policy --policy-name JenkinsEKSRolePolicy --policy-document file://eks-policy.json
aws iam create-role --role-name JenkinsEKSRole --assume-role-policy-document file://trust-policy.json
aws iam attach-role-policy --role-name JenkinsEKSRole --policy-arn arn:aws:iam::010438494949:policy/JenkinsEKSRolePolicy


 

    

    jenkins-role
    jenkins-service-account


aws eks --region us-east-2 describe-cluster --name demo-cluster
kubectl config delete-context jenkins-context
cat ~/.kube/config
cp /home/ubuntu/Deploying-to-eks-using-jenkins/kubeconfig /var/jenkins_home/kubeconfig
sudo chown jenkins:jenkins /var/jenkins_home/kubeconfig
sudo chmod 600 /var/jenkins_home/kubeconfig

pipeline {
    agent any
    tools {
        maven 'app-maven'
        dockerTool 'app-docker'
    }

    environment {
        AWS_REGION = 'us-east-2'
        ECR_REPO = '010438494949.dkr.ecr.us-east-2.amazonaws.com/jenkins-repo'
        BRANCH_NAME = "${env.GIT_BRANCH}".replaceAll('/', '-') // Replace slashes with dashes in branch name
        IMAGE_TAG = "${BRANCH_NAME}-${env.BUILD_ID}" // Use branch name and build ID for image tag
        IMAGE_NAME = "${ECR_REPO}:${IMAGE_TAG}" // Full image name with tag
        CLUSTER_NAME = 'tester-cluster' // EKS cluster name
        //SERVER_URL = 'https://100E584D809B03C2057ADE0FC1AD625E.gr7.us-east-2.eks.amazonaws.com'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/Bennymce/Deploying-to-eks-using-jenkins.git', 
                    branch: 'main',
                    credentialsId: 'github-credentials'
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

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    sh "docker build -t ${IMAGE_NAME} ." // Build Docker image with tag
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                sh "trivy image ${IMAGE_NAME}" // Scan the Docker image for vulnerabilities
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

        stage('Tag and Push Docker Image to ECR') {
            steps {
                script {
                    sh "docker tag ${IMAGE_NAME} ${ECR_REPO}:${IMAGE_TAG}"
                    sh "docker push ${ECR_REPO}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') { // Corrected stage definition
            steps {
                script {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) { 
                        sh 'aws eks update-kubeconfig --name tester-cluster --region us-east-2'
                        // Apply the deployment and service YAMLs
                        sh 'kubectl apply -f java-app-deployment.yaml --namespace jenkins' 
                        // Ensure that deployment.yaml exists in the Jenkins workspace
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


{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "eks:DescribeCluster",
                "eks:ListClusters"
            ],
            "Resource": "arn:aws:eks:us-east-2:010438494949:cluster/deploy-cluster"
        },
        {
            "Effect": "Allow",
            "Action": "sts:AssumeRole",
            "Resource": "*"
        }
    ]
}


vi ~/.aws/credentials
curl -s http://169.254.169.254/latest/meta-data/iam/info | jq '.arn:aws:sts::010438494949:assumed-role/demotry-role/i-042a1a2bee1fb2a6e'
eksctl create cluster --name apllication-cluster --region us-east-2 --nodes 2

Create a Tag: If your repository doesn’t have any tags, you can create one. For example:
git tag
This command creates a tag named v1.0.0 and pushes it to the remote repository.
git tag v1.0.0
git push origin v1.0.0


It looks like the AWS EBS CSI driver is available as an EKS-managed add-on, which simplifies the installation and management process. Since you've checked the available add-on versions, you can proceed to install or update the EBS CSI driver using the following steps:
aws eks create-addon --cluster-name  apllication-cluster--addon-name aws-ebs-csi-driver --addon-version v1.36.0-eksbuild.1
aws eks describe-addon --cluster-name apllication-cluster --addon-name aws-ebs-csi-driver
kubectl get pods -n kube-system -l app.kubernetes.io/name=aws-ebs-csi-driver
kubectl get events -n testing


