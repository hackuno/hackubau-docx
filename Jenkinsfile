pipeline {
  agent any
  stages {
    stage('Init') {
      steps {
        echo 'Inizio a buildare Hackubau Docx'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean package -DskipTests'
        archiveArtifacts(artifacts: 'target\\*.jar', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'pom.xml', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'src\\main\\resources\\*', onlyIfSuccessful: true)
      }
    }

    stage('Test') {
      steps {
        warnError(message: 'Errore nei test - release instabile') {
          sh 'mvn surefire-report:report'
          junit 'target/surefire-reports/*.xml'
        }

      }
    }

    stage('Database migration') {
      steps {
        flywayrunner(installationName: 'flyway', flywayCommand: 'migrate', url: 'jdbc:sqlserver://localhost:1433;DatabaseName=bau', locations: 'sql', commandLineArgs: '-configFiles=DevOps/flyway.conf', credentialsId: '6f90de55-d2dd-41e1-87bc-5a3af1e99de4')
      }
    }

    stage('Deploy with ansible') {
                steps {
                  ansiblePlaybook 'playbook.yml'
                }
              }


  }
}