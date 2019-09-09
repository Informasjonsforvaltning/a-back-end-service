#!groovy
pipeline {
    //Agent running steps if not specified in individual stage
    agent {
        label 'helm-kubectl'
    }

    stages {
        stage('Build') {
            agent {
                label 'jenkins-maven'
            }
            steps {
                container('java-docker') {
                    withMaven(maven: 'M3') {
                        sh "mvn clean install -B -T 2C"
                    }
                } // container
            }
        }
        stage('Test') {
            steps {
                echo 'Todo'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Todo'
            }
        }
    }
}