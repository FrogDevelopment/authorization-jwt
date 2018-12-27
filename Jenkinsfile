pipeline {
    agent any

    stages {
        withMaven(
                maven: 'Default',
                jdk: 'Default'
        ) {

            stage('Compile') {
                steps {
                    sh "mvn compile -e "
                }
            }

            stage('Test') {
                steps {
                    sh "mvn test -e  -Dsurefire.useFile=false"
                }
            }

            stage('Install') {
                steps {
                    sh "mvn install -Dmaven.test.skip=true -e "
                }
            }

            stage('Publish') {
                steps {
                 // sh "mvn install -Dmaven.test.skip=true -e "
                }
            }
        }
    }
}
