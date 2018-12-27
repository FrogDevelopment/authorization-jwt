pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn compile -e "
                }
            }
        }

        stage('Test') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn test -e  -Dsurefire.useFile=false"
                }
            }
        }

        stage('Install') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn install -Dmaven.test.skip=true -e "
                }
            }
        }

        stage('Publish') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                   // sh "mvn install -Dmaven.test.skip=true -e "
                   // sh "echo FIXME"
                }
            }
        }
    }
}
