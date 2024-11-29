// vars/sonarQubeAnalysis.groovy
def call(Map config = [:]) {
    // Set default values for parameters if not provided
    def appDirectory = config.get('appDirectory', '')  // No default; user must specify
    def projectKey = config.get('projectKey', '')  // No default; user must specify
    def projectName = config.get('projectName', '')  // No default; user must specifye
    def vaultPath = config.get('vaultPath', '')  // No default; user must specifyh
    def sonarHostUrl = config.get('sonarHostUrl', 'http://192.168.1.130:9000')  // No default; user must specify
    
    // Ensure that required parameters are passed
    if (projectKey == '' || sonarHostUrl == '' || appDirectory == '' | projectName == '' || vaultPath == '' | ) {
        error "Note: projectKey, sonarHostUrl,  appDirectory, projectName  must be provided."
    }

    // Fetch SonarQube credentials from Vault
    withVault([vaultSecrets: [[path: vaultPath, secretValues: [
        [envVar: 'SONAR_USER', vaultKey: 'SONAR_USER'],
        [envVar: 'SONAR_PASSWORD', vaultKey: 'SONAR_PASS'],
        [envVar: 'SONAR_TOKEN', vaultKey: 'SONAR_TOKEN']
    ]]]]) {
        // Run SonarQube analysis with the provided credentials and configurations
        withSonarQubeEnv('SonarQube') {
            script {
                echo "Running SonarQube analysis for project: ${projectName}..."
                sh """
                    cd ${appDirectory}
                    mvn sonar:sonar \
                        -Dsonar.projectKey=${projectKey} \
                        -Dsonar.projectName=${projectName} \
                        -Dsonar.token=${SONAR_TOKEN} \
                        -Dsonar.host.url=${sonarHostUrl}
                """
                echo "SonarQube analysis completed."
            }
        }
    }
}
