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

        stage('Analyse') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn sonar:sonar \
                          -Dsonar.projectKey=FrogDevelopment_jwt-authentication \
                          -Dsonar.organization=frogdevelopment \
                          -Dsonar.host.url=https://sonarcloud.io \
                          -Dsonar.login=aae668311f337077578b3986eef0b70203db09f4 \
                          -e "
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
    }
}
