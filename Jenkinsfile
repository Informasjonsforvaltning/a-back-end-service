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
                container('cloud-sdk') {
                    withMaven(maven: 'M3') {
                        echo "Build"
                        sh "mvn clean install -DskipTests -B -T 2C"
                    }
                }
            }
        } //end stage build


        stage('Test') {
            agent {
                label 'jenkins-maven'
            }
            //her kan vi evt bestemme at den kun skal kjøres ved pull requests
            steps {
                container('cloud-sdk') {
                    withMaven(maven: 'M3') {
                        echo "Build"
                        sh "./buildWithSonarReport.sh"
                    }
                }
            }
        } //end stage build


        //kan lage tilsvarende for deploy til staging, prod
        //med kriterier for branch, tag eller annet
        //men vi må bestemme hva miljøene skal være
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