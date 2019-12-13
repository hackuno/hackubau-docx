pipeline {
  agent any
  stages {
    stage('Init') {
      steps {
        echo 'Inizio a buildare Hackubau Docx'
        sh 'mvn clean -U'
      }
    }

    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            sh 'mvn clean package -DskipTests'
            archiveArtifacts(artifacts: 'target\\*.jar', onlyIfSuccessful: true)
            archiveArtifacts(artifacts: 'pom.xml', onlyIfSuccessful: true)
            archiveArtifacts(artifacts: 'src\\main\\resources\\*', onlyIfSuccessful: true)
          }
        }

        stage('Test1') {
          steps {
            sh 'mvn test'
          }
        }

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

  }
}