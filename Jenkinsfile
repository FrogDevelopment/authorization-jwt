pipeline {
    agent any
    
    stages {
        stage('Clean') {
            steps {
                withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "@echo $JAVA_HOME'
                    sh "mvn clean -B -V"
                }
            }
        }
        stage('Compile') {
            steps {
                    withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn compile -e -B"
                }
            }
        }
        stage('Test') {
            steps {
                withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn test -e -B -Dsurefire.useFile=false"
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
                withMaven(maven: 'Default',jdk: 'Java 10') {
                    sh "mvn install -Dmaven.test.skip=true -e -B"
                }
            }
        }
    }
}
