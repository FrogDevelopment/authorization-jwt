pipeline {
    agent any
    
    tools {
        maven: 'Default',
        jdk: 'Java 10'
    }

    stages {
        stage('Clean') {
            steps {
                withMaven {
                    sh "mvn clean -e -B"
                }
            }
        }
        stage('Compile') {
            steps {
                withMaven {
                    sh "mvn compile -e -B"
                }
            }
        }
        stage('Test') {
            steps {
                withMaven {
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
                withMaven {
                    sh "mvn install -Dmaven.test.skip=true -e -B"
                }
            }
        }
    }
}
