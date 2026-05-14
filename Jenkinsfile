pipeline {
	agent any
	tools {
		maven 'MAVEN_3_9_11'
		jdk 'Java21'
	}

	stages {
        stage ('Compile Project') {
          steps {
            withMaven(maven : 'MAVEN_3_9_11') {
                sh 'mvn clean compile'
            }
          }
        }

        stage('Validate Checkstyle') {
          steps {
            withMaven(maven: 'MAVEN_3_9_11') {
              sh 'mvn checkstyle:check'
            }
          }
        }

        stage('Validate Unit Tests') {
          steps {
            withMaven(maven: 'MAVEN_3_9_11') {
              sh 'mvn test'
            }
          }
        }

        stage('Validate Test Coverage') {
          steps {
            withMaven(maven: 'MAVEN_3_9_11') {
              sh 'mvn clean verify jacoco:report'
              sh 'mvn jacoco:check'
            }
          }
        }

    	 /*stage ('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarLocal') {
                    bat 'mvn clean verify sonar:sonar -Dsonar.projectKey=learning-center'
                }
            }
         }*/

        stage ('package Project') {
            steps {
                withMaven(maven : 'MAVEN_3_9_11') {
                    sh 'mvn package'
                }
            }
        }


    }
}