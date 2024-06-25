pipeline {
    agent any

    environment {
        MAVEN_HOME = tool name: 'M3', type: 'maven'
        JAVA_HOME = tool name: 'JDK 17', type: 'jdk'
        PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test with dev profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -Pdev'
                    }
                }
            }
        }

        stage('Build and Test with prod profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -Pprod'
                    }
                }
            }
        }

        stage('Build and Test with local profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -Plocal'
                    }
                }
            }
        }

        stage('Build and Test with SSL profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -PSSL'
                    }
                }
            }
        }

        stage('Build and Test with self-tomcat profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -Pself-tomcat'
                    }
                }
            }
        }

        stage('Build and Test with war profile') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean install -Pwar'
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: '**/target/*.war', allowEmptyArchive: true
        }
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}