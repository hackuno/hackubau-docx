pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
    }
  }
  stages {
    stage('Init') {
      steps {
        echo 'Inizio a buildare Hackubau Docx: ${BUILD_TAG} --name jdk8-mvn-node-fly-ans'
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
        nexusUrl: 'localhost:8081/repository/hck',
        groupId: 'it.hackubau',
        version: '${BUILD_NUMBER}', repository: 'hck',
        credentialsId: '4dfa3a50-c33c-4539-bc7f-b4e5558c056d',
        artifacts: [
                    [   artifactId: 'hackubau-docs',
                        classifier: '',
                        file: "target/hackubau-docs-1.0-RELEASE.jar",
                        type: 'jar'],
                        [   artifactId: 'hackubau-docs_sql_migration',
                            classifier: '',
                            file: "sql.zip",
                            type: 'zip'],
                        [   artifactId: 'hackubau_docs_DevOps',
                            classifier: '',
                            file: "DevOps.zip",
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
      FLYWAY_URL = 'jdbc:sqlserver://172.17.0.1:1433;DatabaseName=bau'
      ENV_QUALIFIER = 'dev'
    }
  }