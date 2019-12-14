pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
      args '-u root:sudo'
    }

  }
  stages {
    stage('Init') {
      steps {
        echo 'Inizio a buildare Hackubau Docx: ${BUILD_TAG} --name jdk8-mvn-node-fly-ans'
      }
    }
    stage('delete files from workspace') {
      steps {
        sh 'ls -l'
        sh 'sudo rm -rf ./*'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean package -DskipTests'
        sh 'zip -r sql.zip sql'
        sh 'zip -r DevOps.zip DevOps'
        archiveArtifacts(artifacts: 'target\\*.jar', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'pom.xml', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'src\\main\\resources\\*', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'sql.zip', onlyIfSuccessful: true)
        archiveArtifacts(artifacts: 'DevOps.zip', onlyIfSuccessful: true)
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
        nexusArtifactUploader(nexusVersion: 'nexus3', protocol: 'http',
        nexusUrl: 'localhost:8081/repository/maven-releases',
        groupId: 'it.hackubau',
        version: '${BUILD_NUMBER}', repository: 'hck',
        credentialsId: '4dfa3a50-c33c-4539-bc7f-b4e5558c056d',
        artifacts: [
                    [   artifactId: 'hackubau-docs',
                        classifier: '',
                        file: "target/hackubau-docs-1.0-RELEASE.jar",
                        type: 'jar'],
                        [   artifactId: 'sql',
                            classifier: '',
                            file: "sql.zip",
                            type: 'zip']
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
      NEXUS_URL='172.17.0.1:8081'
      DB_URL = '172.17.0.1:1433'
      ENV_QUALIFIER = 'dev'
    }
  }