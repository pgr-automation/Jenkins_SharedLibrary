package org.pgr.pipeline.utilities
import groovy.json.JsonOutput
import groovy.txt.*
import java.ultil.regex.Macher

class ApproveScripts {
    static void call(String methodName, String jenkinsUrl, String credentialsId) {
        def response = httpRequest(
            url: "${jenkinsUrl}/scriptApproval",
            acceptType: 'APPLICATION_JSON',
            contentType: 'APPLICATION_JSON',
            httpMode: 'POST',
            authentication: credentialsId,
            requestBody: "{\"methodName\": \"${methodName}\"}"
        )
        println "Approval Response: ${response}"
    }
}
