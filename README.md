# deploying-EKS-App-using-jenkins
Deploying an EKS app from Jenkins pipeline 
DEPLOY AN APP TO EKS EKS CLUSTER FROM JENKINS PIPELINE


I already have an empty running EKS cluster 







eksctl create cluster --name deploy-cluster --region us-east-2 --nodes 2
aws eks update-kubeconfig --name demo-cluster --region us-east-2 --role-arn arn:aws:iam::010438494949:role/jenkins-eks-role







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


aws sts get-caller-identity which still shows the user with aws credentials

cat ~/.aws/credentials
mv ~/.aws/credentials ~/.aws/credentials.bak

unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY

{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Effect": "Allow",
			"Principal": {
				"Service": "ec2.amazonaws.com"
			},
			"Action": "sts:AssumeRole"
		}
	]
}



kubectl edit configmap aws-auth -n kube-system
Verify kubectl Authentication in Jenkins
kubectl get nodes --namespace=kube-system
kubectl apply -f configmap.yaml

kubectl get pods -n kube-system -l app.kubernetes.io/name=ebs-csi-controller

