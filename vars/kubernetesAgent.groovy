def call(String jenkinsUrl = "http://192.168.1.120:8070", String appType = "java") {
    // Define default image for each application type from Docker Hub
    def image = ""
    switch (appType) {
        case "java":
            image = "openjdk:11-jdk-slim"  // Official OpenJDK image for Java applications
            break
        case "nodejs":
            image = "node:16-slim"  // Official Node.js image
            break
        case "python":
            image = "python:3.9-slim"  // Official Python image
            break
        case "springboot":
            image = "openjdk:11-jdk-slim"  // Using OpenJDK for Spring Boot
            break
        default:
            image = "jenkins/inbound-agent:latest"  // Default Jenkins inbound agent image
    }

    return """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: k8-jenkins
spec:
  containers:
  - name: jnlp
    image: ${image}
    imagePullPolicy: Always
    args: ["\$(JENKINS_SECRET)", "\$(JENKINS_NAME)"]
    env:
    - name: JENKINS_URL
      value: "${jenkinsUrl}"
    - name: JAVA_OPTS
      value: "-Djenkins.slave.agents.disableSslVerification=true"
    resources:
      requests:
        cpu: "100m"
        memory: "256Mi"
      limits:
        cpu: "500m"
        memory: "512Mi"
    volumeMounts:
    - name: workspace-volume
      mountPath: /home/jenkins/agent
  volumes:
  - name: workspace-volume
    emptyDir: {}
  nodeSelector:
    kubernetes.io/os: linux
  tolerations:
  - key: "key"
    operator: "Equal"
    value: "value"
    effect: "NoSchedule"
  dnsPolicy: ClusterFirst
"""
}

