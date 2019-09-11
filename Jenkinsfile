#!groovy
pipeline {
    //Agent running steps if not specified in individual stage
    agent {
        label 'helm-kubectl'
    }

    environment {
        DOCKER_IMAGE_NAME = 'brreg/template-image-name'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('Build') {
            agent {
                label 'jenkins-maven'
            }
            steps {
                container('cloud-sdk') {
                    withMaven(maven: 'M3') {
                        echo "Build"
                        sh "mvn clean install -B -T 2C"
                    }
                }
            }
            post {
                failure {
                    script {
                        slackSend   channel: '#jenkins',
                                color: 'danger',
                                message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName} by <TODO user> has test failures. <${currentBuild.absoluteUrl}|Link>"
                    }
                }
            }
        } //end stage build


        stage("Push to Docker registry") {
            agent {
                label 'jenkins-maven'
            }
            steps {
                container('cloud-sdk') {
                    withCredentials([file(credentialsId: 'fdk-infra-file', variable: 'SA')]) {
                        sh returnStatus: true, script: 'gcloud auth activate-service-account --key-file $SA'
                    }
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:build_${env.BUILD_NUMBER}"
                    sh "docker push eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:build_${env.BUILD_NUMBER}"
                }
            }
        } //end stage push to docker registry
    }


    post {
        always {
            script {
                def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']
                slackSend   channel: '#jenkins',
                        color: COLOR_MAP[currentBuild.currentResult],
                        message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName} by <TODO user> finished with status ${currentBuild.result}. <${currentBuild.absoluteUrl}|Link>"
            }
        }
    }
}