pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
      args '-u root:sudo --name jdk8-mvn-node-fly-ans'
    }

  }
  stages {
    stage('Init') {
      steps {
        echo 'Inizio a buildare Hackubau Docx jdk8-mvn-node-fly-ans'
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

    stage('Nexus Upload') {
      steps {
        nexusArtifactUploader(nexusVersion: 'nexus3', protocol: 'http', nexusUrl: 'localhost:8081/repository/maven-releases', groupId: 'it.hackubau', version: '1', repository: 'hck', credentialsId: '4dfa3a50-c33c-4539-bc7f-b4e5558c056d', artifacts: [
                                          [artifactId: 'hackubau-docs',
                                           classifier: '',
                                           file: "target/hackubau-docs-1.0-RELEASE.jar",
                                           type: 'jar']
                                      ])
        }
      }

      stage('Database migration') {
        steps {
          sh 'sudo flyway -configFiles=DevOps/flyway.conf -locations=filesystem:sql migrate'
        }
      }

      stage('Deploy with ansible') {
        steps {
          ansiblePlaybook(playbook: 'DevOps/playbook.yml', inventory: 'DevOps/hosts')
        }
      }

    }
    environment {
      DB_URL = '172.17.0.1'
      ENV_QUALIFIER = 'dev'
    }
  }