pipeline {
    agent any
    
    post {
        always {
            deleteDir()
        }
    }
    
    tools {
        maven 'Default'
        jdk 'Java 10'
    }
    
    stages {
        stage('Clean') {
            steps {
                sh "mvn clean -e -B"
            }
        }
        stage('Compile') {
            steps {
                sh "mvn compile -e -B"
            }
        }
        stage('Test') {
            steps {
                sh "mvn test -e -B"
                step( [ $class: 'JacocoPublisher' ] )
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
                sh "mvn install -Dmaven.test.skip=true -e -B"
            }
        }
    }
}
