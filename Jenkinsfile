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
                        sh "mvn clean install -B -T 2C"
                        //hvor skal vi laste opp Docker images?
                    }
                }
            }
        } //end stage build


        stage('Sonar analyse') {
            agent {
                label 'jenkins-maven'
            }
            //her kan vi evt bestemme at den kun skal kjøres ved pull requests
            steps {
                /*
                container('cloud-sdk') {
                    withMaven(maven: 'M3') {
                        echo "Build"
                        sh "./buildWithSonarReport.sh"
                    }
                }
                */
                echo 'midlertidig avslått'
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