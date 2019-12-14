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

    stage('Manual Approvation') {
      steps {
        input 'Procedi al deploy in quality?'
      }
    }

    stage('Nexus Upload') {
              steps {
                nexusArtifactUploader(nexusVersion: 'nexus3',
                 protocol: 'http',
                 nexusUrl: 'localhost:8081/repository/maven-releases',
                 groupId: '',
                 version: '1',
                 repository: 'it.hackubau',
                 credentialsId: '4dfa3a50-c33c-4539-bc7f-b4e5558c056d',
                 artifacts: [
                                [artifactId: 'hackubau-docs-1.0-RELEASE',
                                 classifier: '',
                                 file: "target/hackubau-docs-1.0-RELEASE.jar",
                                 type: 'jar']
                            ])
                }
              }

    stage('Database migration') {
      steps {
        sh 'sudo flyway -configFiles=DevOps/flyway.conf migrate'
      }
    }

    stage('Deploy with ansible') {
      steps {
        ansiblePlaybook(playbook: 'DevOps/playbook.yml', inventory: 'DevOps/hosts')
      }
    }

  }
}