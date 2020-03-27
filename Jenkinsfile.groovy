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
        // stage ('Deploy') {
        //     steps{
        //         sshagent(credentials : ['awskey']) {
        //         sh 'ssh -o StrictHostKeyChecking=no ubuntu@13.54.226.2 uptime'
        //         sh 'ssh -v ubuntu@13.54.226.2'
        //         sh 'scp ./get-instance-id.sh ubuntu@13.54.226.2:/tmp/target'
        //         }
        //     }
        // }
        stage("Get Instance IP and setup script") {
            steps {
                withAWS(credentials: 'TerraformAWSCreds', region: 'ap-southeast-2') {
                
                script {
                    instanceIP = sh(script: './get-instance-id.sh', returnStdout: true).trim()
                }
                echo "${instanceIP}"
                }
                sshagent(credentials : ['awskey']) {
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@${instanceIP} uptime"
                    sh "scp ./deploycode.sh ubuntu@${instanceIP}:/tmp/deploycode.sh"
                    sh "ssh ubuntu@${instanceIP} chmod 755 /tmp/deploycode.sh"
                }
            }
        }
        stage("Deploy WeatherApp") {
            steps {
                sshagent(credentials : ['awskey']) {
                    sh "ssh ubuntu@${instanceIP} /tmp/deploycode.sh"
                }
            }
        }
    }
}