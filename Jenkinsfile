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
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                    sh "docker push eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:git_${gitCommit}"
                    sh "docker push eu.gcr.io/fdk-infra/${DOCKER_IMAGE_NAME}:branch_${dockerBranchNameTag}_build_${env.BUILD_NUMBER}"
                }
            }
        } //end stage push to docker registry


        stage('Deploy to UT1') {
            agent {
                label 'helm-kubectl'
            }
            //todo step med git tag etter vellykket deploy
            //finn bruk docker tag fra git commit hash. Finn den og putt den i en fil,
            //deretter apply den med helm. Konstruer navnet med å bruke miljøvariablene
            //todo: finne ut av verifyDeployments - det funket ikke ut av boksen...
            steps {
                container('helm-gcloud-kubectl') {
                    //temporary: create a mongodb instance to test the deployment.
                    //remove when jenkinsfile is working correctly

                    //fetch from Helm template repository - currently not using Tiller
                    sh 'helm repo add fdk https://informasjonsforvaltning.github.io/helm-chart/'
                    //sh "helm fetch --untar --untardir ./helm 'fdk/a-back-end-service'"
                    sh 'helm install fdk/a-back-end-service'
                    sh 'ls -l'
                    sh 'helm template -f tmp_values.yaml helm/a-back-end-service/ > kubectlapply.yaml'
                    //todo: prøve helm instsall

                    //sh 'helm template -f tmp_values.yaml -f tmp_mongo_values.yaml helm/ > kubectlapply.yaml'
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
                        slackSend   channel: '#jenkins',
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Deploy: ${currentBuild.fullDisplayName}, with Git commit hash: ${gitCommit} by ${changeAuthors} deployed to UT1"
                    }
                }
            }
        } //end stage deploy to UT1

        //TODO: legg til stages for verifisering og prod-setting her
    }
}