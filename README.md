# testing-java


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



