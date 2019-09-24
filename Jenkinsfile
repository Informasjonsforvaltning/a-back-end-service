#!groovy
/*
Build pipeline for Felles Datakatalog template service
This pipeline does not include deploy steps
 */

//colors for Slack messages
def SLACK_COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']




/*
Helper methods
*/

//get list of Git commit users in branch being built
def getChangeAuthors() {
    return currentBuild.changeSets.collect { set ->
        set.collect { entry -> entry.author.fullName }
    }.unique().flatten()
}






pipeline {
    //Agent running steps if not specified in individual stage
    agent {
        label 'helm-kubectl'
    }

    environment {
        HELM_REPOSITORY_NAME = 'fdk'
        HELM_REPOSITORY_URL = 'https://informasjonsforvaltning.github.io/helm-chart/'
        DOCKER_REGISTRY_URL = 'eu.gcr.io/fdk-infra/'
        HELM_WORKING_DIR = 'helm'

        //these need to be changed for each application
        HELM_TEMPLATE_NAME = 'a-back-end-service'
        DOCKER_IMAGE_NAME = 'brreg/template-image-name'
        DOCKER_IMAGE_TAG = 'latest'
        HELM_ENVIRONMENT_VALUE_FILE = 'tmp_values.yaml' //todo: finne ut hvordan dette skal håndteres
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
                always {
                    script {
                        changeAuthors = getChangeAuthors()
                        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                        slackSend   channel: '#jenkins',
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName}, with Git commit hash: ${gitCommit} by ${changeAuthors} built with status ${currentBuild.result}. <${currentBuild.absoluteUrl}|Link>"
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
                    script {
                        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                        gitBranchName = env.BRANCH_NAME
                        dockerBranchNameTag = gitBranchName.replaceAll('/', '_')
                    }
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                }
            }
        } //end stage push to docker registry


        stage('Deploy to UT1') {
            agent {
                label 'helm-kubectl'
            }
            //todo step med git tag etter vellykket deploy
            //todo: finne ut av verifyDeployments - det funket ikke ut av boksen...
            steps {
                container('helm-gcloud-kubectl') {

                    //Apply Helm template. Fetch from Helm template repository - currently not using Tiller
                    sh "helm repo add ${HELM_REPOSITORY_NAME} ${HELM_REPOSITORY_URL}"
                    sh "helm fetch --untar --untardir ./helm '${HELM_REPOSITORY_NAME}/${HELM_TEMPLATE_NAME}'"
                    sh 'ls -l'
                    sh "helm template --set DOCKER_IMAGE_NAME=${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:git_${gitCommit} " +
                            "-f ${HELM_ENVIRONMENT_VALUE_FILE} ${HELM_WORKING_DIR}/${HELM_TEMPLATE_NAME}/ " +
                            "> kubectlapply.yaml"

                    sh 'cat kubectlapply.yaml'
                    sh 'chmod o+w kubectlapply.yaml'
                    step([$class: 'KubernetesEngineBuilder',
                          projectId: "fdk-dev",
                          clusterName: "fdk-dev",
                          zone: "europe-north1-a",
                          manifestPattern: 'kubectlapply.yaml',
                          credentialsId: "fdk-dev",
                          verifyDeployments: false])
                }
            }
            post {
                success {
                    script {
                        //git tag hvis suksessfult. Vis git tag i slack melding
                        //docker tag deployed også
                        changeAuthors = getChangeAuthors()
                        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

                        slackSend channel: '#jenkins',
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Deploy: ${currentBuild.fullDisplayName}, with Git commit hash: ${gitCommit} by ${changeAuthors} deployed to UT1"
                    }
                }
            }
        } //end stage deploy to UT1

        //TODO: legg til stages for verifisering og prod-setting her
    }
}