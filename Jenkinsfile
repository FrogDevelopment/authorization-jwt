pipeline {
    agent any

    stages {
        stage('Clean') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn clean -e -B"
                }
            }
        }
        stage('Compile') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn compile -e -B"
                }
            }
        }
        stage('Test') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn test -e -B"
                    step( [ $class: 'JacocoPublisher' ] )
                }
            }
        }
//        stage('Verify') {
//            steps {
//                withMaven(
//                        maven: 'Default',
//                        jdk: 'Java 10'
//                ) {
//                    sh "mvn verify -e -B"
//                }
//            }
//        }
        stage('Install') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn install -e -B"
                }
            }
        }
    }
}