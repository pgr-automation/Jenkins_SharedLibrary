pipeline {
  environment {
    ARTIFACTORY_USER = credentials('artifactory-user')
    ARTIFACTORY_PASSWORD = credentials('artifactory-password')
  }
  agent none
  // options {
  //   ansiColor('xterm')
  // }
  stages {
    stage('build agent') {
      agent {
        kubernetes {
          cloud "k8-jenkins"
          yaml yamlFileName
          slaveConnectTimeout 300
          podRetention never()
        }
      }
      //   when {
      //     beforeAgent true
      //     not {
      //       changlog '.*\\[maven-release-plugin\\].+|.*\\[jenkins-release\\].*'
      //     }
      // }
      steps {
        script {
          println "Starting build agent setup..."
          // Add build agent initialization logic if required.
        }
      }
    }
    stage('Maven Build') {
      steps {
        script {
          common.stageLog("Maven Build") // Start Logging

          // Fetch Git information
          def gitCommit = sh(label: 'Get Git commit', returnStdout: true, script: "git log -n 1 --pretty-format:'%h'").trim()
          def gitRemoteURL = sh(label: 'Get Git remote URL', returnStdout: true, script: "git config --get remote.origin.url").trim()
          def gitScm = "git@github.westernasset.com:" + gitRemoteURL.drop(32)
          def appGitRepoName = gitRemoteURL.split('/')[-1].replace('.git', '')

          common.customLog("appGitRepoName: ${appGitRepoName}")
          common.customLog("Git Commit: ${gitCommit}")
          common.customLog("Git Remote URL: ${gitRemoteURL}")
          common.customLog("Git SCM URL: ${gitScm}")

          def image_tag = "rhel-ubi9-future"
          common.customLog("Base image tag: ${image_tag}")
          common.customLog("Protected branch: ${protectedBranch ?: 'N/A'}")
          common.customLog("env.BRANCH_NAME: ${env.BRANCH_NAME}")

          def pom = readMavenPom file: 'pom.xml'
          def pomVersion = pom.version ?: pom.parent?.version
          if (!pomVersion) {
              error "Failed to determine POM version. Please check the POM file."
          }
          common.customLog("Pom version: ${pomVersion}")
          common.customLog("Pom artifactId: ${pom.artifactId}")

          currentBuild.displayName = "${pomVersion}-${BUILD_NUMBER}"
          currentBuild.description = "Deployment Code: ${pomVersion}-${BUILD_NUMBER}"

          common.customLog("Starting Maven snapshot build...")

          container('maven') {
              sh """
              mvn -v 
              export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m -Dmaven.test.failure.ignore=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true"
              mvn -U clean deploy -X
              """
          }
        }
      }
    }
    stage('Dockerfile Linter') {
      steps {
        script {
          common.stageLog("Dockerfile Linter")
          sh """
          docker run --rm -i hadolint/hadolint < Dockerfile || true
          """
        }
      }
    }
  }
}
