pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                    withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn compile -e "
                }
            }
        }

        stage('Test') {
            steps {
                withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn test -e  -Dsurefire.useFile=false"
                    step( [ $class: 'JacocoPublisher' ] )
                }
            }
        }

        stage('Install') {
            steps {
                withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn install -Dmaven.test.skip=true -e "
                }
            }
        }

        stage('Publish') {
            steps {
                withMaven(maven: 'Default',jdk: 'Java 10') {
                   // sh "mvn install -Dmaven.test.skip=true -e "
                   // sh "echo FIXME"
                }
            }
        }
    }
}
