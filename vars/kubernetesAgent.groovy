def call(String jenkinsUrl = "http://192.168.1.120:8070") {
    return """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: "jenkins-build_deploy"
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
      requests:
        memory: "64Mi"
        cpu: "100m"
      limits:
        memory: "2000Mi"
        cpu: "2000m"
    volumeMounts:
    - name: "workspace-volume"
      mountPath: "/home/jenkins/agent"
    - name: "docker-sock"
      mountPath: "/var/run/docker.sock"

  - name: "hadolint-agent"
    image: "hadolint/hadolint:v2.12.0-alpine"
    command:
      - "cat"
    tty: true
    resources:
      requests:
        memory: "64Mi"
        cpu: "100m"
      limits:
        memory: "2000Mi"
        cpu: "2000m"
    volumeMounts:
    - name: "workspace-volume"
      mountPath: "/home/jenkins/agent"

  nodeSelector:
    kubernetes.io/os: "linux"
  restartPolicy: "Never"
  serviceAccountName: "jenkins"
  volumes:
  - name: "workspace-volume"
    emptyDir: {}
  - name: "docker-sock"
    hostPath:
      path: "/var/run/docker.sock"
"""
}
