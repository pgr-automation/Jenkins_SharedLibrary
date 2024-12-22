// vars/approveScripts.groovy
def call(String methodName) {
    // Perform the HTTP request for script approval
    def response = httpRequest(
        url: 'http://192.168.1.120:8070/scriptApproval',
        acceptType: 'APPLICATION_JSON',
        contentType: 'APPLICATION_JSON',
        httpMode: 'POST',
        authentication: 'jenkins-api-credentials',
        requestBody: "{\"methodName\": \"${methodName}\"}"
    )
    echo "Approval Response: ${response}"
}

