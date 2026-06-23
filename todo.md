# Spring Boot Book Management Application Todo List

This todo list outlines the steps to build a simple Spring Boot application for managing books using an in-memory H2 database.

---

## Coding Agent Workflow Instructions

For every task listed below (starting from Phase 1), the coding/developer agent MUST follow this consistent pattern:

1. **Branch**: Create a new git branch from `main` named exactly after the task ID (e.g., `P1-01`, `P2-02`).
2. **Implement**: Complete the required code changes for that specific task.
3. **Verify**: Run all Maven unit tests locally with `./mvnw test` to ensure successful compilation and no test failures.
4. **Raise PR**: Create a Pull Request using the `gh` CLI (`gh pr create`).
5. **Review**: Invoke the `springboot reviewer subagent` to inspect the code changes. The reviewer subagent must not modify any code; it must only provide review comments on GitHub.
6. **Wait**: Wait for the reviewer subagent to complete its analysis and post comments or approval.
7. **Resolution Loop**:
   - If the reviewer subagent accepts the PR (no blocking review comments): Merge the PR.
   - If there are review comments/fixes needed: Edit/fix the code, push the changes, re-invoke the reviewer subagent, and repeat this loop until the PR is approved and merged.
8. **Cleanup**: Delete the task branch on local (`git branch -d <branch-name>`) and remote (`git push origin --delete <branch-name>`) after the PR is merged.
9. **Final Output**: Once the entire `todo.md` is completed, the coordinator agent must output ONLY this JSON format:
   ```json
   {
     "implementation": "done",
     "pr": "done"
   }
   ```
   *(Use "failed" or "incomplete" if any part of the process was not successfully completed).*

---

## Phase 0: Git Repository Initialization (P0)

- [x] **P0-01**: Initialize Git Repository
  - Initialize git: `git init`.
  - Add origin remote: `git remote add origin https://github.com/albinzdaniel02/sample-springboot-kubernetes.git`.
  - Create a basic `README.md` and commit/push the initial repository state to `main`.
- [x] **P0-EC**: Phase 0 Exit Check
  - Verify that the local git remote is pointing to `https://github.com/albinzdaniel02/sample-springboot-kubernetes.git`.
  - Verify that the repository is successfully pushed to the remote repository.

---

## Phase 1: Project Initialization, Configuration & CI/CD (P1)

- [x] **P1-01**: Initialize Maven Spring Boot Project
  - Set up a Maven-based Spring Boot project (Java 21, Spring Boot 3.x) with `pom.xml` and Maven Wrapper.
  - Dependencies in `pom.xml`: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-actuator`, `h2`, `lombok`, `spring-boot-starter-test`.
- [x] **P1-02**: Configure Application Properties
  - Configure `src/main/resources/application.yml` for H2 In-Memory Database and Spring Boot Actuator.
  - Enable H2 Console (`spring.h2.console.enabled=true` under web settings) for manual verification.
  - Configure Actuator exposure if needed (by default, `/actuator/health` is exposed).
- [x] **P1-03**: Setup GitHub CI/CD Workflows
  - Create `.github/workflows/ci.yml` for Maven compilation and testing.
    - Steps: Check out code, setup Java 21, cache Maven dependencies, and run `./mvnw clean test` on pull requests and pushes to `main`.
  - Create `.github/workflows/cd.yml` (or include CD steps in CI) to automate executable/package building or Docker image assembly when pull requests merge to `main`.
- [ ] **P1-04**: Configure & Test Actuator Health Endpoint
  - Ensure Spring Boot Actuator is functional.
  - Write an integration/unit test using `@SpringBootTest` and `MockMvc` or `@WebMvcTest` to verify that `GET /actuator/health` returns status `200 OK` and contains `"status": "UP"`.
- [ ] **P1-EC**: Phase 1 Exit Check
  - Verify project compiles and runs locally.
  - Execute `./mvnw clean test` and verify that the Actuator Health endpoint unit test passes.
  - Verify that the workflow files are committed, pushed, and trigger successfully on GitHub.

---

## Phase 2: Domain Model & Data Access (P2)

- [ ] **P2-01**: Implement `Book` Entity & Repository
  - Create the `Book` JPA entity with fields: `Long id`, `String title`, `String author`, `String isbn`, `Double price`.
  - Add Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Entity`).
  - Create `BookRepository` interface extending `JpaRepository<Book, Long>`.
- [ ] **P2-02**: Write Repository Unit Tests
  - Implement `BookRepositoryTest` using `@DataJpaTest`.
  - Test cases: saving a book, finding a book by ID, retrieving all books, updating a book, and deleting a book.
- [ ] **P2-EC**: Phase 2 Exit Check
  - Run database repository unit tests using `./mvnw test -Dtest=BookRepositoryTest`.
  - Verify JPA schema creation log statements match the entity definition.

---

## Phase 3: Service Layer (P3)

- [ ] **P3-01**: Implement `BookService`
  - Create a service class/interface (`BookService`/`BookServiceImpl`) to handle business logic.
  - Define methods: `getAllBooks()`, `getBookById(id)`, `createBook(book)`, `updateBook(id, book)`, `deleteBook(id)`.
- [ ] **P3-02**: Write Service Unit Tests
  - Implement `BookServiceTest` using Mockito (`@ExtendWith(MockitoExtension.class)`).
  - Mock `BookRepository` behavior and test all service method paths (success and failure/not found cases).
- [ ] **P3-EC**: Phase 3 Exit Check
  - Run service layer tests using `./mvnw test -Dtest=BookServiceTest`.
  - Verify all service logic paths (including exceptions for not-found books) pass successfully.

---

## Phase 4: API Layer & Full End-to-End Verification (P4)

- [ ] **P4-01**: Implement `BookController`
  - Create `BookController` with `@RestController` and `@RequestMapping("/api/books")`.
  - Map endpoints:
    - `GET /api/books` -> Returns `List<Book>`
    - `GET /api/books/{id}` -> Returns `Book` (or 404 Not Found)
    - `POST /api/books` -> Creates a new book and returns `201 Created`
    - `PUT /api/books/{id}` -> Updates an existing book (or 404 Not Found)
    - `DELETE /api/books/{id}` -> Deletes the book and returns `204 No Content` (or 404 Not Found)
- [ ] **P4-02**: Write Controller Integration/Unit Tests
  - Implement `BookControllerTest` using `@WebMvcTest(BookController.class)`.
  - Use `MockMvc` to test all HTTP endpoints, verifying status codes, request bodies, and correct interactions with `BookService`.
- [ ] **P4-EC**: Phase 4 Exit Check
  - Run the entire test suite using `./mvnw clean test` and ensure all tests pass (Ping, Repository, Service, and Controller).
  - Boot the application using `./mvnw spring-boot:run` and verify API endpoints and `/h2-console` manually.
