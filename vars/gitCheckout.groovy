// vars/gitCheckout.groovy
def call(Map config = [:]) {
    // Set default values for parameters if not provided
    def branch = config.get('branch', 'main') // Default to 'main' branch
    def credentialsId = config.get('credentialsId', 'default-test-id') // Default credentialsId
    def repoUrl = config.get('repoUrl', 'test.gt') // Default repository URL

    // Perform the Git checkout
    script {
        echo "Checking out code from repository: ${repoUrl}, branch: ${branch}..."
        git branch: branch, credentialsId: credentialsId, url: repoUrl
        echo "Code checked out successfully."
    }
}
