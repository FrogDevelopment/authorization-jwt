pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn clean compile -e -B"
                }
            }
        }
        stage('Test') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn surefire:test -e -B"
                    step( [ $class: 'JacocoPublisher' ] )
                }
            }
        }
        stage('Verify') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Java 10'
                ) {
                    sh "mvn failsafe:verify -e -B"
                }
            }
        }
    }
}