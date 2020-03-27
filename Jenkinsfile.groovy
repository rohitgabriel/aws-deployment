pipeline {
    agent any
    environment {
        AWS_CRED=credentials('AWSNTT-Account-Credentials')
        AWS_ACCESS_KEY_ID="${AWS_CRED_USR}"
        AWS_SECRET_ACCESS_KEY="${AWS_CRED_PSW}"
        AWS_DEFAULT_REGION="ap-southeast-2"
    }
    stages {
        stage('Checkout repo') {
            steps {
                git branch: 'master',
                credentialsId: 'mygitcredid',
                url: 'https://github.com/rohitgabriel/aws-deployment.git'
            }
        }
        stage("Get Instance IP") {
            steps {
                withAWS(credentials: 'TerraformAWSCreds', region: 'ap-southeast-2') {
                sh './get-instance-id.sh'
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