def call(body) {
    // Define the parameters for the script
    common =  new org.pgr.pipeline.utilities.common()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Ensure required parameters are provided
    def cloudAgent = config.buildAgentCluster
      println "cloudAgent: ${cloudAgent}"
    def nexusRepo = "{env.ORGANIATION}-nexus-repo-snapshot"
    def yamlFilePath = libraryResource 'podconfigs/agentpod.yaml'
    def appDirectory = config.appDirectory
    def vaultPath = config.vaultPath
    def repoName = config.repoName

    def gitremoteURL = ""
    def gitBranch = ""
    def gitCommit = ""
    def gitTag = ""
    def gitCommitMessage = ""
    def appImageTag = ""
    def appVersion = ""
    def appImageName = ""
    def appGitRepoName = ""
    def pomversion = ""
    def userInput = ""

    def buildTag = config.buildTag
    if (buildTag != null && buildTag.size() > 0) {
      BUILDERIMAGE = "${env.IMAGE_REPO_URL}/${env.ORGANIZATION}/${env.BUILDER_IMAGE_NAME}:${buildTag}"
        println "BUILDERIMAGE: ${BUILDERIMAGE}"
    } 
        def yamlFileName = yamlFilePath.replace("BUILDERIMAGE", BUILDERIMAGE)

}



pipeline {
  environment {
    ARTIFACTORY_USER = credentials('artifactory-user')
    ARTIFACTORY_PASSWORD = credentials('artifactory-password')
  }
  agent none
  options {
    ansiColor('xterm')
  }
  tools {
    // 
  }
  stages {
    stage('build agent'){
      agent {
        kubernetes {
          cloud "$cloudAgent"
          yaml yamlFileName
          slaveConnectTimeout 300
          podRetention never()
        }
      }
      when {
        beforeAgent true
        not {
          changLog '.*\\[maven-release-plugin\\].+|.*\\[jenkins-release\\].*'
        }
      }
    }
  }
  stages {
    stage('Maven Build') {
      steps {
        script {
          // Maven Build
          
            common.stageLog("Maven Build") // Start Logging

            // Fetch Git information
            def gitCommit = sh(label: 'Get Git commit', returnStdout: true, script: "git log -n 1 --pretty-format:'%h'").trim() // Get the Git commit hash
            def gitRemoteURL = sh(label: 'Get Git remote URL', returnStdout: true, script: "git config --get remote.origin.url").trim() // Get the Git remote URL

            // Derive Git SCM URL and repository name
            def gitScm = "git@github.westernasset.com:" + gitRemoteURL.drop(32) // Derive the Git SCM URL
            def appGitRepoName = gitRemoteURL.split('/')[-1].replace('.git', '') // Derive the Git repository name

            // Log Git information
            common.customLog("appGitRepoName: ${appGitRepoName}") // Log the Git repository name
            common.customLog("Git Commit: ${gitCommit}") // Log the Git commit hash
            common.customLog("Git Remote URL: ${gitRemoteURL}") // Log the Git remote URL
            common.customLog("Git SCM URL: ${gitScm}") // Log the Git SCM URL

            // Set base image tag and log other variables
            def image_tag = "rhel-ubi9-future"
            common.customLog("Base image tag: ${image_tag}") // Log the base image tag
            common.customLog("Protected branch: ${protectedBranch ?: 'N/A'}") // Log the protected branch
            common.customLog("env.BRANCH_NAME: ${env.BRANCH_NAME}") // Log the branch name

            // Read and log Maven POM details
            def pom = readMavenPom file: 'pom.xml' // Read the Maven POM file
            def pomVersion = pom.version ?: pom.parent?.version // Get the POM version
            if (!pomVersion) {
                error "Failed to determine POM version. Please check the POM file."
            } // Throw an error if the POM version is not found
            common.customLog("Pom version: ${pomVersion}") // Log the POM version
            common.customLog("Pom artifactId: ${pom.artifactId}") // Log the POM artifact ID

            // Update Jenkins build metadata
            currentBuild.displayName = "${pomVersion}-${BUILD_NUMBER}" // Set the Jenkins build display name
            currentBuild.description = "Deployment Code: ${pomVersion}-${BUILD_NUMBER}" // Set the Jenkins build description

            common.customLog("Starting Maven snapshot build...") // Log the start of the Maven snapshot build

            // Execute Maven build inside the Maven container
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
    stage ('Dockerfile Linter'){
      steps {
        script {
          common.stageLog("Dockerfile Linter") // Start Logging
          // Lint the Dockerfile
          sh """
          docker run --rm -i hadolint/hadolint < Dockerfile || true
          """
        }
      }
    }
  }
  
}