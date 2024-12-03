// vars/generateAndPublishCoverageReport.groovy
def call(String projectDir = 'spring-bootapp') {
    // Step 1: Generate JaCoCo Coverage Report
    echo 'Generating JaCoCo coverage report...'
    dir(projectDir) {
        // Run Maven to generate the coverage report
        sh 'mvn verify'
    }
    
    // Step 2: Publish the JaCoCo Coverage Report
    echo 'Publishing JaCoCo coverage report...'
    publishHTML(target: [
        allowMissing: false,
        alwaysLinkToLastBuild: true,
        keepAll: true,
        reportDir: "${projectDir}/target/site/jacoco",  // Report directory
        reportFiles: 'index.html',  // The report file
        reportName: 'JaCoCo Coverage Report'  // The report name
    ])
}

