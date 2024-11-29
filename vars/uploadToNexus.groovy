// vars/uploadToNexus.groovy
def call(Map config = [:]) {
    // Ensure required parameters are provided
    def nexusRepo = config.get('nexusRepo', 'default-nexus-repo')  // Default to 'default-nexus-repo' if not provided
    def vaultPath = config.get('vaultPath', 'secret/data/nexuscred') // Default Vault path for credentials
    def altRepoName = config.get('altRepoName', 'pgr-nexus-repo-snapshot')  // Alternative repo name
    def appDirectory = config.get('appDirectory', 'spring-bootapp') // Default directory for the app

    // Retrieve credentials from Vault and set them as environment variables
    withVault([vaultSecrets: [[path: vaultPath, secretValues: [
        [envVar: 'NEXUS_USER', vaultKey: 'NEXUS_USER'],
        [envVar: 'NEXUS_PASSWORD', vaultKey: 'NEXUS_PASS']
    ]]]]) {
        script {
            echo "Deploying artifacts to Nexus..."
            
            // Build and deploy to Nexus
            sh """
                cd ${appDirectory}
                mvn deploy -DaltDeploymentRepository=${altRepoName}::default::${nexusRepo} \
                    -Dusername=${NEXUS_USER} -Dpassword=${NEXUS_PASSWORD}
            """
            
            echo "Artifacts uploaded successfully to Nexus."
        }
    }
}
