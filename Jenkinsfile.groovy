pipeline {
    agent any
    environment {
        AWS_CRED=credentials('AWSNTT-Account-Credentials')
        AWS_ACCESS_KEY_ID="${AWS_CRED_USR}"
        AWS_SECRET_ACCESS_KEY="${AWS_CRED_PSW}"
        AWS_DEFAULT_REGION="ap-southeast-2"
    }
    stages {
        // stage('Checkout repo') {
        //     steps {
        //         git branch: 'master',
        //         credentialsId: 'mygitcredid',
        //         url: 'https://github.com/rohitgabriel/aws-deployment.git'
        //     }
        // }
        stage ('Deploy') {
            steps{
                sshagent(credentials : ['awskey']) {
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@13.54.226.2 uptime'
                sh 'ssh -v ubuntu@13.54.226.2'
                sh 'scp ./get-instance-id.sh ubuntu@13.54.226.2:/tmp/target'
                }
            }
        }
        stage("Get Instance IP") {
            steps {
                withAWS(credentials: 'TerraformAWSCreds', region: 'ap-southeast-2') {
                sh './get-instance-id.sh'
                }
                sshagent(credentials : ['awskey']) {
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@13.54.226.2 uptime'
                sh 'scp ./deploycode.sh ubuntu@13.54.226.2:/tmp/deploycode.sh'
                sh 'ssh ubuntu@13.54.226.2 chmod 755 /tmp/deploycode.sh'
                sh 'ssh ubuntu@13.54.226.2 /tmp/deploycode.sh'
                }
            }
        }
        stage("Approval required") {
            input {
            message "Deploy App or Abort?"
            }
            steps {
                echo 'This will deploy app code changes'
            }
        }
        stage("clean up") {
            steps {
                echo 'clean up'
            }
        }
    }
}