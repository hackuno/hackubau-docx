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

    stage('') {
      steps {
        echo 'Fingo un deploy'
      }
    }

  }
}