#!groovy
/*
Build pipeline for Felles Datakatalog template service
This pipeline does not include deploy steps
 */

import java.text.SimpleDateFormat

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

//get formatted timestamp
def getTimestamp() {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    date = new Date()
    return dateFormat.format(date)
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
        PROD_DEPLOY_APPROVERS = 'bjorn_grova,ssa'

        SLACK_BUILD_NOTIFICATION_CHANNEL = '#jenkins'
        SLACK_DEPLOY_NOTIFICATION_CHANNEL = '#jenkins'
        SLACK_APPROVAL_NOTIFICATION_CHANNEL = '#jenkins-godkjenning'

        STAGING_GCP_ZONE = 'europe-north1-a'
        STAGING_GCP_PROJECT = 'fdk-dev'
        STAGING_K8S_CLUSTER = 'fdk-dev'
        STAGING_K8S_NAMESPACE = 'ut1'

        PRODUCTION_GCP_ZONE = 'europe-north1-a'
        PRODUCTION_GCP_PROJECT = 'fdk-prod'
        PRODUCTION_K8S_CLUSTER = 'fdk-prod'
        PRODUCTION_K8S_NAMESPACE = 'prod'

        //these need to be changed for each application
        HELM_TEMPLATE_NAME = 'a-back-end-service'
        DOCKER_IMAGE_NAME = 'brreg/template-image-name'
        DOCKER_IMAGE_TAG = 'latest'

        //identity of user Jenkins uses to push tags to git repository
        //TODO: sjekk om disse kan erstattes av scm variabelen
        GITHUB_ORGANIZATION = 'Informasjonsforvaltning'
        GITHUB_REPOSITORY = 'a-backend-service'
        GITHUB_USER_NAME = 'Jenkins system user'
        GITHUB_USER_EMAIL = 'systemjenkins@fellesdatakatalog.brreg.no'
    }

    stages {
        stage('Build') {
            agent {
                label 'jenkins-maven'
            }
            steps {
                script {
                    scmVars = checkout(scm)
                    echo "scmVars: ${scmVars}"
                }
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
                        slackSend   channel: "${SLACK_BUILD_NOTIFICATION_CHANNEL}",
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName}, with Git commit hash: ${gitCommit} by ${changeAuthors} built with status ${currentBuild.result}. <${currentBuild.absoluteUrl}|Link>"
                    }
                }
            }
        } //end stage build


        stage("Push to Docker registry") {
            when {
                beforeAgent true
                //only push docker images for builds that are actually deployed
                anyOf {
                    changeRequest()
                    branch 'master'
                }
            }
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


        stage('Deploy to staging') {
            when {
                beforeAgent true
                //TODO: Nå kjører den for alle Pull requests. Ønsker kun å kjøre når pull request er approved.
                changeRequest()
            }

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
                            " --set NAMESPACE=${STAGING_K8S_NAMESPACE} " +
                            "-f ${HELM_WORKING_DIR}/${HELM_TEMPLATE_NAME}/values/staging.yaml ${HELM_WORKING_DIR}/${HELM_TEMPLATE_NAME}/ " +
                            "> kubectlapply.yaml"

                    sh 'cat kubectlapply.yaml'
                    sh 'chmod o+w kubectlapply.yaml'
                    step([$class: 'KubernetesEngineBuilder',
                          projectId: "${STAGING_GCP_PROJECT}",
                          clusterName: "${STAGING_K8S_CLUSTER}",
                          zone: "${STAGING_GCP_ZONE}",
                          manifestPattern: 'kubectlapply.yaml',
                          credentialsId: "${STAGING_GCP_PROJECT}",
                          verifyDeployments: false])

                    withCredentials([usernamePassword(credentialsId: 'systemjenkins', passwordVariable: 'githubPassword', usernameVariable: 'githubUsername')]) {
                        sh("git config user.name '${GITHUB_USER_NAME}'" )
                        sh("git config user.email '${GITHUB_USER_EMAIL}'")
                        sh("git tag -a -m'Deployed to staging at: ${getTimestamp()}' deploy_staging_${env.BUILD_TAG}")
                        sh("git tag -f -a -m'Deployed to staging at: ${getTimestamp()}' deploy_staging_latest")
                        sh("git push -f https://${githubUsername}:${githubPassword}@github.com/${GITHUB_ORGANIZATION}/${GITHUB_REPOSITORY}.git --tags")
                    }
                }
                container('cloud-sdk') {
                    withCredentials([file(credentialsId: 'fdk-infra-file', variable: 'SA')]) {
                        sh returnStatus: true, script: 'gcloud auth activate-service-account --key-file $SA'
                    }

                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_staging_${env.BUILD_TAG}"
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_staging_latest"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_staging_${env.BUILD_TAG}"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_staging_latest"
                }
            }
            post {
                success {
                    script {
                        changeAuthors = getChangeAuthors()
                        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

                        slackSend channel: "${SLACK_DEPLOY_NOTIFICATION_CHANNEL}",
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Deploy: ${currentBuild.fullDisplayName}, " +
                                        "with Git commit hash: ${gitCommit} " +
                                        "and tag ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_staging_${env.BUILD_TAG}" +
                                        "by ${changeAuthors} deployed to Staging"
                    }
                }
            }
        } //end stage deploy to UT1


        stage('Wait for Approval') {
            when {
                branch 'master'
                //todo: kanskje dette blir overflødig.
            }
            steps{
                slackSend channel: "${SLACK_APPROVAL_NOTIFICATION_CHANNEL}",
                        color: SLACK_COLOR_MAP[currentBuild.currentResult],
                        message: " (${DOCKER_IMAGE_NAME}) Build: ${currentBuild.fullDisplayName} ready for approval for deploy to production. <${currentBuild.absoluteUrl}|Link>"
                timeout(time:12, unit:'HOURS') {
                    input message:'Approve deployment to PROD?', submitter: "${PROD_DEPLOY_APPROVERS}"
                }
            }
        }


        stage('Deploy to Production') {
            when {
                branch 'master'
                //todo: den skal egentlig kjøre før merge. Først deploy til prod, så merge til master.
            }

            //TODO: tag

            steps{
                container('helm-gcloud-kubectl') {


                    //Apply Helm template. Fetch from Helm template repository - currently not using Tiller
                    sh "helm repo add ${HELM_REPOSITORY_NAME} ${HELM_REPOSITORY_URL}"
                    sh "helm fetch --untar --untardir ./helm '${HELM_REPOSITORY_NAME}/${HELM_TEMPLATE_NAME}'"
                    sh 'ls -l'
                    sh "helm template --set DOCKER_IMAGE_NAME=${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:git_${gitCommit} " +
                            " --set NAMESPACE=${PRODUCTION_K8S_NAMESPACE} " +
                            "-f ${HELM_WORKING_DIR}/${HELM_TEMPLATE_NAME}/values/production.yaml ${HELM_WORKING_DIR}/${HELM_TEMPLATE_NAME}/ " +
                            "> kubectlapply.yaml"

                    sh 'cat kubectlapply.yaml'
                    sh 'chmod o+w kubectlapply.yaml'
                    step([$class: 'KubernetesEngineBuilder',
                          projectId: "${PRODUCTION_GCP_PROJECT}",
                          clusterName: "${PRODUCTION_K8S_CLUSTER}",
                          zone: "${PRODUCTION_GCP_ZONE}",
                          manifestPattern: 'kubectlapply.yaml',
                          credentialsId: "${PRODUCTION_GCP_PROJECT}",
                          verifyDeployments: false])

                    withCredentials([usernamePassword(credentialsId: 'systemjenkins', passwordVariable: 'githubPassword', usernameVariable: 'githubUsername')]) {
                        sh("git config user.name '${GITHUB_USER_NAME}'" )
                        sh("git config user.email '${GITHUB_USER_EMAIL}'")
                        sh("git tag -a -m'Deployed to production at: ${getTimestamp()}' deploy_production_${env.BUILD_TAG}")
                        sh("git tag -f -a -m'Deployed to production at: ${getTimestamp()}' deploy_production_latest")
                        sh("git push -f https://${githubUsername}:${githubPassword}@github.com/${GITHUB_ORGANIZATION}/${GITHUB_REPOSITORY}.git --tags")
                    }
                }
                container('cloud-sdk') {
                    withCredentials([file(credentialsId: 'fdk-infra-file', variable: 'SA')]) {
                        sh returnStatus: true, script: 'gcloud auth activate-service-account --key-file $SA'
                    }

                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_production_${env.BUILD_TAG}"
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_production_latest"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_production_${env.BUILD_TAG}"
                    sh "docker push ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_production_latest"
                }
            }
            post {
                success {
                    script {
                        changeAuthors = getChangeAuthors()
                        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

                        slackSend channel: "${SLACK_DEPLOY_NOTIFICATION_CHANNEL}",
                                color: SLACK_COLOR_MAP[currentBuild.currentResult],
                                message: " (${DOCKER_IMAGE_NAME}) Deploy: ${currentBuild.fullDisplayName}, " +
                                        "with Git commit hash: ${gitCommit} " +
                                        "and tag ${DOCKER_REGISTRY_URL}${DOCKER_IMAGE_NAME}:deploy_production_${env.BUILD_TAG}" +
                                        "by ${changeAuthors} deployed to Production"
                    }
                }
            }
        } //end stage deploy to prod
    }
}