def call(String jenkinsUrl = "http://192.168.1.120:8070") {
    return """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: "jenkins-build_deploy"
  namespace: "jenkins-agent"
spec:
  containers:
  - name: "docker-agent"
    image: "docker:24.0.6-dind"
    command:
      - "cat"
    tty: true
    securityContext:
      privileged: true
    resources:
      limits:
        memory: "128Mi"
        cpu: "500m"
    volumeMounts:
    - name: "workspace-volume"
      mountPath: "/home/jenkins/agent"
    - name: "docker-sock"
      mountPath: "/var/run/docker.sock"
  
  - name: "hadolint-agent"
    image: "hadolint/hadolint:v2.12.0-alpine"
    tty: true
    securityContext:
      privileged: true
    resources:
      limits:
        memory: "128Mi"
        cpu: "500m"
    volumeMounts:
    - name: "workspace-volume"
      mountPath: "/home/jenkins/agent"
    - name: "docker-sock"
      mountPath: "/var/run/docker.sock"
  
  nodeSelector:
    kubernetes.io/os: "linux"
  restartPolicy: "Never"
  serviceAccount: "jenkins"
  volumes:
  - name: "workspace-volume"
    emptyDir: {}
  - name: "docker-sock"
    hostPath:
      path: "/var/run/docker.sock"
"""
}

