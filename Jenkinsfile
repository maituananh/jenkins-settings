pipeline {
  agent any

  environment {
    BRANCH             = 'main'
    AWS_DEFAULT_REGION = 'ap-southeast-7'
    BUCKET             = 's3://devops-jenkins-anhmt'
  }

  triggers {
    cron('H/90 * * * *')
    GenericTrigger(
      genericVariables: [
        [key: 'GITHUB_PR_ACTION',   value: '$.action',                defaultValue: ''],
        [key: 'GITHUB_PR_MERGED',   value: '$.pull_request.merged',   defaultValue: 'false'],
        [key: 'GITHUB_PR_BASE_REF', value: '$.pull_request.base.ref', defaultValue: ''],
        [key: 'GITHUB_REPO_NAME',   value: '$.repository.full_name',  defaultValue: ''],
      ],
      causeString: 'Triggered by GitHub webhook',
      tokenCredentialId: 'SECRET_TOKEN_JENKINS_SETTINGS',
      token: 'SECRET_TOKEN_JENKINS_SETTINGS',
      printContributedVariables: true,
      printPostContent: true
    )
  }

  stages {
    stage('Backup Jenkins to S3') {
      steps {
        withCredentials([
          [
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 'aws-creds'
          ]
        ]) {
          sh '''
            tar -czf - \
                --exclude='workspace' \
                --exclude='logs' \
                --exclude='.cache' \
                --exclude='caches' \
                --exclude='tools' \
                --exclude='*.log' \
                --exclude='*.tmp' \
                -C /var/jenkins_home . \
                2>/dev/null \
            | aws s3 cp - ${BUCKET}/jenkins-backup.tar.gz
          '''
        }
      }
    }
  }

  post {
    success { echo "Backup success!" }
    failure { echo "Backup failed!" }
  }
}