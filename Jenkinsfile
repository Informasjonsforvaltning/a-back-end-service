#!groovy
pipeline {
    //Agent running steps if not specified in individual stage
    agent {
        label 'helm-kubectl'
    }

    stages {
        stage('Build') {
            agent {
                label 'java-docker'
            }
            steps {
                sh 'mvn clean install -B -T 2C'
            }
        }
        stage('Test') {
            steps {
                echo 'Todo'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Todo
        }
    }
}