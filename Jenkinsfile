pipeline {
    agent any
    tools {
        maven 'maven 3.9.2'
    }
    stages {
        stage('checkout') {
            steps {
                script {
                    checkout scm
                }
            }
        }
        stage('build') {
            steps {
                sh 'mvn clean install -Dmaven.test.skip'
                //sh 'mvn spring-boot:run'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}