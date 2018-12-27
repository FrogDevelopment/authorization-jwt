pipeline {
    agent any

    withMaven(
            maven: 'Default',
            jdk: 'Default'
    ) {

        stages {
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
