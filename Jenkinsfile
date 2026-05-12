pipeline {
    agent { label 'mac-mini'}

    options{
        disableResume()
        disableConcurrentBuilds abortPrevious: true
        buildDiscarder(logRotator(
            numToKeepStr: '5',          
            daysToKeepStr: '10',         
            artifactNumToKeepStr: '5',  
            artifactDaysToKeepStr: '10'  
        ))
    }

    tools {
        maven '3.6.3'
        jdk '17.0.19+10'
    }

    environment {
        NEXUS_REPOSITORY = "ia-qgent-docker"
        DOCKER_IMAGE_NAME = "ia-agent-backemd"
        DOCKER_IMAGE_VERSION = "0.0.1-${env.BRANCH_NAME}"
        NEXUS_URL = "nexus.atlas-labs.org"
        NEXUS_CREDS_ID = "nexus"
        PATH = "/usr/local/bin:/opt/homebrew/bin:${env.PATH}"
        DOCKER_CONFIG = "/tmp/docker-config-${BUILD_NUMBER}"
        DOCKER_HOST = "unix:///Users/jenkins/.colima/amd64/docker.sock"
    }

    stages {
        stage('Build JAR file') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        // stage('OWASP Dependency Scan') {
        //     when {
        //         branch 'PR-*'
        //     }
        //     steps {
        //         script {
        //             sh "mkdir -p reports/owasp"
        //             withCredentials([string(credentialsId: 'NVD_API_KEY', variable: 'NVD_API_KEY')]) {
        //                 def additionalArguments = """
        //                     --scan ./
        //                     --out reports/owasp 
        //                     --format HTML --format XML
        //                     --data /Users/jenkins/dependency-check-data
        //                     --disableKnownExploited
        //                     --disableRubygems
        //                     --disableBundleAudit
        //                     --disableYarnAudit
        //                     --disableAssembly
        //                     --nvdApiKey ${NVD_API_KEY}
        //                     --failOnCVSS 11
        //                     --prettyPrint
        //                 """

        //                 dependencyCheck additionalArguments: additionalArguments,
        //                     odcInstallation: 'owasp-12.1.1'

        //                 dependencyCheckPublisher failedTotalCritical: 1,
        //                     failedTotalHigh: 4,
        //                     failedTotalLow: 90,
        //                     failedTotalMedium: 8,
        //                     pattern: 'reports/owasp/dependency-check-report.xml',
        //                     stopBuild: true
        //                 }
        //         }
        //     }
        // }

        stage('Quality Analysis') {
            when {
                branch 'PR-*'
            }
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=ai-agent-backend \
                            -Dsonar.projectName=ai-agent-backend \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('Quality Gate') {
            when {
                branch 'PR-*'
            }
            steps{
                waitForQualityGate abortPipeline: true
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def timestamp = sh(script: "date '+%Y-%m-%d_%H-%M-%S'", returnStdout: true).trim()
                    env.DOCKER_IMAGE_TAG = "${DOCKER_IMAGE_VERSION}_${timestamp}"
                }
                withCredentials([usernamePassword(credentialsId: NEXUS_CREDS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        mkdir -p ${DOCKER_CONFIG}
                        AUTH=\$(printf '%s:%s' "\$DOCKER_USER" "\$DOCKER_PASS" | base64 | tr -d '\\n')
                        printf '{"auths":{"%s":{"auth":"%s"}}}' "${NEXUS_URL}" "\$AUTH" > ${DOCKER_CONFIG}/config.json
                        docker build -t ${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/${DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} .
                    """
                }
            }
        }

        // stage ('Image Security Scan') {
        //     when {
        //         branch 'PR-*'
        //     }
        //     steps{
        //         sh """
        //             mkdir -p reports/grype
        //             syft ${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/${DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} \
        //                 -o cyclonedx-json > sbom.json
        //             grype sbom:./sbom.json \
        //                 --output "template=reports/grype/index.html" \
        //                 --template ci/grype-report.html.tmpl
        //             grype sbom:./sbom.json \
        //                 --output table \
        //                 --fail-on critical
        //         """
        //     }
        //     post {
        //         always {
        //             publishHTML(target: [
        //                 allowMissing         : false,
        //                 alwaysLinkToLastBuild: true,
        //                 keepAll              : true,
        //                 reportDir            : 'reports/grype',
        //                 reportFiles          : 'index.html',
        //                 reportName           : 'Security Scan Report'
        //             ])
        //         }
        //     }
        // }

        stage('Push Docker Image') {
            when {
                branch 'develop'
            }
            steps {
                sh "docker push ${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/${DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}"
            }
        }
    }

    post {
        always {
            sh "rm -rf $DOCKER_CONFIG || true"
            cleanWs()
        }
    }
}