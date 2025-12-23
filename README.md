# BPMN Generator

Camunda 7 sample with Spring Boot that embeds the engine (REST + webapps) and a standalone external task worker that handles the `Generate BPMN Service` task from `bpmn-gen.bpmn`.

## Project layout
- `pom.xml`: Parent aggregator (Java 17, Spring Boot 3.5, Camunda 7.24).
- `bpmn-generator-engine`: Camunda 7 engine + REST/webapp; auto-deploys `bpmn-gen.bpmn`; uses file-based H2 DB `engine-h2-database.mv.db`; default admin `demo/demo`.
- `bpmn-generator-external-task`: Standalone external task worker subscribing to topic `bpmn-generator-worker`; logs a simple message and completes with process variable `statusProcess=OK` (no required input variables).

## Prerequisites
- JDK 17
- Maven 3.9+
- Ports 8080 (engine) and 8081 (worker) available
- Optional: Camunda Modeler to edit the BPMN diagram.

## Quick start
1) Start the engine (REST + Tasklist + Cockpit):

   ```bash
   mvn -pl bpmn-generator-engine spring-boot:run
   ```

   - Web apps + REST: http://localhost:8080
   - Default admin user: `demo` / `demo` (set in `bpmn-generator-engine/src/main/resources/application.yaml`).

2) Start the external task worker in another terminal:

   ```bash
   mvn -pl bpmn-generator-external-task spring-boot:run
   ```

   - Runs on port 8081.
   - Subscribes to topic `bpmn-generator-worker` (see `bpmn-generator-external-task/src/main/java/com/dilos/gpa/engine/handlers/BpmnGeneratorExternalHandler.java`).

3) Trigger the process instance via REST (requires both apps running):

   ```bash
   curl -X POST http://localhost:8080/engine-rest/process-definition/key/Process_04vlk9k/start \
     -H "Content-Type: application/json" \
     -d '{"variables": {}}'
   ```

   - The worker prints a start message and completes the task with `statusProcess=OK`.
   - You can include custom variables in the payload; they are not required for completion.

## BPMN model
- Diagram: `bpmn-generator-engine/src/main/resources/bpmn-gen.bpmn`.
- Flow: Start -> `Generate BPMN Service` (external task, topic `bpmn-generator-worker`) -> End.
- The engine auto-deploys this diagram on startup.

## Configuration notes
- Engine DB: `spring.datasource.url: jdbc:h2:file:./engine-h2-database` (see `application.yaml`). Delete/move `engine-h2-database.mv.db` to reset state.
- External task client settings: `bpmn-generator-external-task/src/main/resources/application.yml` (engine REST base-url, worker id, async timeout, lock duration, logging levels).
- Logging: worker enables DEBUG for Camunda client; engine uses Spring Boot defaults.

## Build & packaging
- Build everything: `mvn clean package`
- Run packaged apps (after build):

  ```bash
  java -jar bpmn-generator-engine/target/bpmn-generator-engine-0.0.1-SNAPSHOT.jar
  java -jar bpmn-generator-external-task/target/bpmn-generator-external-task-0.0.1-SNAPSHOT.jar
  ```
