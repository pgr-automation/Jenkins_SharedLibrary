package org.pgr.pipeline.utilities
import groovy.txt.*
import java.util.regex.Matcher

def getBranchName(String branchName) {
    branch = scm.branches[0].name
    return branch.split('/').size() == 1 ? branch.split('/')[-1] : branch.split('/')[1..-1].join('/')
}

// def getkuberneters(cluster){
//  def map = ['build-agent': 'k8-jenkins', 'deploy-cluster-p': 'k8-jenkins-2']
//  return map[cluster]
// }


def redLog(String message) {
    echo "\033[31m[CUSTOM] ${message.toString} \033[0m"
}
def redLog(Object message) {
    echo "\033[31m[CUSTOM] ${message.toString} \033[0m"
}
def greenLog(Object message) {
    echo "\033[31m[CUSTOM] ${message.toString} \033[0m"
}
def yellowLog(Object message) {
    echo "\033[31m[CUSTOM] ${message.toString} \033[0m"
}




def call(String methodName, String jenkinsUrl, String credentialsId) {
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
