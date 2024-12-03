// vars/buildAndTest.groovy
def call(Map config = [:]) {
    // Set default values for parameters if not provided
    def appDirectory = config.get('appDirectory', 'spring-bootapp') // Default to 'spring-bootapp'
    def mavenOptions = config.get('mavenOptions', '--add-opens java.base/java.lang=ALL-UNNAMED') // Default Maven options
    def skipTests = config.get('skipTests', false) // Default to false, meaning tests will run
    def mvnCommand = skipTests ? "mvn clean install -DskipTests=true" : "mvn clean install -DskipTests=false"
    
    // Run build and test
    script {
        echo "Building and testing the project in directory: ${appDirectory}..."
        
        // Run the Maven build with custom options
        sh """
            cd ${appDirectory}
            export MAVEN_OPTS="${mavenOptions}"
            ${mvnCommand}
        """
        echo "Build & Test completed."
    }
}
