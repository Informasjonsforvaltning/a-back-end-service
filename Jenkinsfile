#!groovy
/*
Build pipeline for Felles Datakatalog template service
This pipeline does not include deploy steps
 */


/*
Helper methods
*/

//get list of Git commit users in branch being built
def getChangeAuthors() {
    return currentBuild.changeSets.collect { set ->
        set.collect { entry -> entry.author.fullName }
    }.unique().flatten()
}


//colors for Slack messages
def SLACK_COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']



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
                //Post message to Slack if build fails
                failure {
                    script {
                        changeAuthors = getChangeAuthors()
                        slackSend   channel: '#jenkins',
                                color: 'danger',
                                message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName} by ${changeAuthors} has test failures. <${currentBuild.absoluteUrl}|Link>"
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
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                    sh "docker push eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker push eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                }
            }
        } //end stage push to docker registry
    }


    post {
        always {
            script {
                changeAuthors = getChangeAuthors()
                gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                slackSend   channel: '#jenkins',
                        color: SLACK_COLOR_MAP[currentBuild.currentResult],
                        message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName}, with Git commit hash: ${gitCommit} by ${changeAuthors} finished with status ${currentBuild.result}. <${currentBuild.absoluteUrl}|Link>"
            }
        }
    }
}