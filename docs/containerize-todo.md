# Spring Boot Book Management Application Containerization Todo List

This todo list outlines the steps to containerize the Spring Boot application using a multi-stage Dockerfile and verify the resulting image.

---

## Coding Agent Workflow Instructions

For every task listed below (starting from Phase 1), the coding/developer agent MUST follow this consistent pattern:

1. **Branch**: Create a new git branch from `main` named exactly after the task ID (e.g., `P1-01`, `P2-02`).
2. **Implement**: Complete the required code changes for that specific task.
3. **Verify**: Perform the verification steps outlined in the task.
4. **Raise PR**: Create a Pull Request using the `gh` CLI (`gh pr create`).
5. **Review**: Invoke the `springboot reviewer subagent` to inspect the code changes. The reviewer subagent must not modify any code; it must only provide review comments on GitHub.
6. **Wait**: Wait for the reviewer subagent to complete its analysis and post comments or approval.
7. **Resolution Loop**:
   - If the reviewer subagent accepts the PR (no blocking review comments): Merge the PR.
   - If there are review comments/fixes needed: Edit/fix the code, push the changes, re-invoke the reviewer subagent, and repeat this loop until the PR is approved and merged.
8. **Cleanup**: Delete the task branch on local (`git branch -d <branch-name>`) and remote (`git push origin --delete <branch-name>`) after the PR is merged.
9. **Final Output**: Once the entire `containerize-todo.md` is completed, the coordinator agent must output ONLY this JSON format:
   ```json
   {
     "containerization": "done",
     "pr": "done"
   }
   ```
   *(Use "failed" or "incomplete" if any part of the process was not successfully completed).*

---

## Phase 1: Docker Configuration & Implementation (P1)

- [x] **P1-01**: Create `.dockerignore`
  - Create a `.dockerignore` file in the root directory to prevent unnecessary files from being copied into the Docker build context.
  - Exclude the following patterns:
    - `.git`
    - `.github`
    - `target/`
    - `*.md`
    - `.mvn/wrapper/maven-wrapper.jar`
    - `mvnw.cmd`
    - `.gitignore`
- [x] **P1-02**: Implement Multi-Stage `Dockerfile`
  - Create a `Dockerfile` in the root directory using a multi-stage approach.
  - **Build Layer**:
    - Use `eclipse-temurin:21-jdk-alpine` as the base image for compilation.
    - Set the working directory to `/app`.
    - Copy Maven wrapper configuration and dependencies descriptor: `pom.xml`, `.mvn/`, `mvnw`.
    - Copy the application source code (`src/`).
    - Run `./mvnw clean package -DskipTests` to build the application executable fat JAR.
  - **Runtime Layer**:
    - Use `eclipse-temurin:21-jre-alpine` as the base image for a slim JRE runtime.
    - Set the working directory to `/app`.
    - Create a system user and group (e.g., `spring:spring`) to run the application as a non-root user.
    - Copy the built JAR from the builder stage (e.g., `/app/target/sample-springboot-0.0.1-SNAPSHOT.jar`) to `/app/app.jar`.
    - Configure the container user context to run as the non-root user (`USER spring:spring`).
    - Expose port `8080`.
    - Set the default command/entrypoint to run the JAR: `ENTRYPOINT ["java", "-jar", "app.jar"]`.
- [x] **P1-EC**: Phase 1 Exit Check
  - Verify that `.dockerignore` and `Dockerfile` are successfully created in the root directory.
  - Verify that the Dockerfile contains both the build (JDK) stage and runtime (JRE) stage.

---

## Phase 2: Local Build & Verification (P2)

- [x] **P2-01**: Build the Docker Image
  - Run `docker build -t sample-springboot:latest .` locally.
  - Verify that the image builds without errors.
  - Verify the resulting image is visible in local registry and check its size (confirming a slim JRE layer is used).
- [x] **P2-02**: Run and Test the Container
  - Start the container locally using: `docker run -d -p 8080:8080 --name sample-app sample-springboot:latest`.
  - Check the startup logs using `docker logs sample-app` to verify the Spring Boot application started successfully.
  - Test the Actuator health endpoint from the host: `curl http://localhost:8080/actuator/health` (or PowerShell command `Invoke-RestMethod -Uri http://localhost:8080/actuator/health`).
  - Verify the response contains `"status": "UP"` and returns HTTP `200 OK`.
- [x] **P2-03**: Clean Up Local Resources
  - Stop and remove the test container: `docker stop sample-app && docker rm sample-app`.
- [x] **P2-EC**: Phase 2 Exit Check
  - Verify that the container runs successfully and the Actuator health check passes.
  - Verify that all temporary containers are cleaned up.
