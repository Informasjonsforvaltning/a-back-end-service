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
                        echo "Build without running tests to check for build problems"
                        sh "mvn clean install -DskipTests -DskipDockerBuild -B -T 2C"
                    }
                }
            }
        } //end stage build


        stage('Test') {
            steps {
                echo 'Todo'
            }
        }//end stage test


        stage('Deploy to UT1') {
            when {
                branch "develop"
            }
            steps {
                echo 'Todo'
            }
        } //end stage Deploy to UT1
    }
}