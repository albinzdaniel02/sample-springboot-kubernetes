# Spring Boot Book Management Application Deployment Todo List

This todo list outlines the steps to deploy the Spring Boot application to a local Minikube or kind cluster, configure applications using a ConfigMap and a Secret, and verify the deployment.

---

## Coding Agent Workflow Instructions

For every task listed below (starting from Phase 1), the coding/developer agent MUST follow this consistent pattern:

1. **Branch**: Create a new git branch from `main` named exactly after the task ID (e.g., `P1-01`, `P2-01`).
2. **Implement**: Complete the required code changes or manifest definitions for that specific task.
3. **Verify**: Perform the verification steps outlined in the task.
4. **Raise PR**: Create a Pull Request using the `gh` CLI (`gh pr create`).
5. **Review**: Invoke the `springboot reviewer subagent` (using the `self` subagent) to inspect the changes. The reviewer subagent must not modify any code; it must only provide review comments on GitHub.
6. **Wait**: Wait for the reviewer subagent to complete its analysis and post comments or approval.
7. **Resolution Loop**:
   - If the reviewer subagent accepts the PR (no blocking review comments): Merge the PR.
   - If there are review comments/fixes needed: Edit/fix the code, push the changes, re-invoke the reviewer subagent, and repeat this loop until the PR is approved and merged.
8. **Cleanup**: Delete the task branch on local (`git branch -d <branch-name>`) and remote (`git push origin --delete <branch-name>`) after the PR is merged.
9. **Final Output**: Once the entire `deploy-todo.md` is completed, the coordinator agent must output ONLY this JSON format:
   ```json
   {
     "deployment": "done",
     "pr": "done"
   }
   ```
   *(Use "failed" or "incomplete" if any part of the process was not successfully completed).*

---

## Phase 1: Kubernetes Manifests Implementation (P1)

- [done] **P1-01**: Create ConfigMap & Secret Manifests
  - Create a directory named `k8s` in the root workspace.
  - Create `k8s/configmap.yaml` containing application configurations (e.g. logging levels or profile settings).
  - Create `k8s/secret.yaml` containing a base64 encoded dummy credential.
- [ ] **P1-02**: Create Deployment & Service Manifests
  - Create `k8s/deployment.yaml` with a replica count of 1, selector `app: sample-springboot`, exposing port 8080, using the image `sample-springboot:latest` (configured with `imagePullPolicy: IfNotPresent`), and referencing the ConfigMap and Secret as environment variables.
  - Create `k8s/service.yaml` defining a `ClusterIP` Service exposing the application deployment on port 8080.
- [ ] **P1-EC**: Phase 1 Exit Check
  - Verify that the four manifests (`configmap.yaml`, `secret.yaml`, `deployment.yaml`, `service.yaml`) are successfully created in the `k8s` directory.
  - Verify that the Deployment correctly pulls configurations and secrets into environment variables.

---

## Phase 2: Local Cluster Setup & Deployment Verification (P2)

- [ ] **P2-01**: Build and Load Image to Local K8s Environment
  - Connect to the local Kubernetes cluster's docker daemon (e.g., if using Minikube, run environment setting command or configure kind to load images).
  - Build/ensure the `sample-springboot:latest` Docker image is available inside the K8s cluster's node.
- [ ] **P2-02**: Apply Manifests and Verify Pod Startup
  - Apply the manifests in the `k8s/` directory to the cluster: `kubectl apply -f k8s/`.
  - Check the pod status and wait for it to transition to `Running` state: `kubectl get pods`.
  - Inspect the pod logs to verify that the application started up successfully: `kubectl logs -l app=sample-springboot`.
- [ ] **P2-03**: Verify Service Connection via Port-Forwarding
  - Establish a port-forward to the Service: `kubectl port-forward svc/sample-springboot-service 8080:8080`.
  - Test the Actuator health endpoint from the host: `curl http://localhost:8080/actuator/health` (or PowerShell equivalent).
  - Verify the response contains `"status": "UP"` and returns HTTP `200 OK`.
- [ ] **P2-04**: Clean Up Kubernetes Resources
  - Stop the port-forward process.
  - Delete all deployed resources from the cluster: `kubectl delete -f k8s/`.
- [ ] **P2-EC**: Phase 2 Exit Check
  - Verify that all temporary Kubernetes resources (Deployment, Service, ConfigMap, Secret) are cleaned up and deleted from the cluster.
