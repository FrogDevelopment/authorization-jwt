pipeline {
    agent any

    stages {
        stage('TMP') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "echo $JAVA_HOME"
                    sh "java -version"
                }
            }
        }
        stage('Clean') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn clean -e -B"
                }
            }
        }
        stage('Compile') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn compile -e -B"
                }
            }
        }
        stage('Test') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
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
//                        jdk: 'Default'
//                ) {
//                    sh "mvn verify -e -B"
//                }
//            }
//        }
        stage('Install') {
            steps {
                withMaven(
                        maven: 'Default',
                        jdk: 'Default'
                ) {
                    sh "mvn install -Dmaven.test.skip=true -e -B"
                }
            }
        }
    }
}
