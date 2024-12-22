// vars/kubernetesAgent.groovy
def call(String jenkinsUrl = "http://192.168.1.120:8070") {
    return """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: k8-jenkins
spec:
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest
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

