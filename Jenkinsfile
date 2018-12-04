node {
    env.JAVA_HOME="${tool 'Java 10'}"
    env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
    sh 'java -version'

    stage('Clean') {
        steps {
            withMaven(jdk: 'Java 10',maven: 'Default') {
                sh "echo JAVA_HOME=$JAVA_HOME"
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