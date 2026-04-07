# Comprehensive Testing Microservice

Testing is a fundamental part of software development that ensures applications behave correctly, remain reliable, and can be safely evolved over time.

Without proper testing:
- Bugs can reach production
- Refactoring becomes risky
- Systems become unstable
- Confidence in the codebase decreases

Effective testing helps to:
- Detect defects early
- Improve code quality
- Ensure system stability
- Enable safe and continuous development

---

## Overview

This microservice demonstrates **real-world testing strategies** used in
modern Spring Boot microservices.

It shows how different testing types work together to ensure: 
- Correctness 
- Reliability 
- Performance 
- Maintainability

#### What We Built

Account Management API:
- Create Account
- Get Account by ID
- Get All Accounts

#### Architecture

Controller → Service → Repository → Database

---

## Testing Strategy 
Recommended sequence

1. **Unit Testing**
2. **Mapper Testing**
3. **Repository Testing** (JPA / Data Layer Testing)
4. **Controller Testing** (Web Layer Testing)
5. **Contract Testing** (API Contract Testing)
6. **Integration Testing** (End-to-End within application)
7. **External API Testing** (WireMock Testing)
8. **Sanity Testing**
9. **Smoke Testing**
10. **Regression Testing** (Conceptual)

### Quality Assurance & Performance Validation

1. **Code Coverage** (JaCoCo)
2. **Code Quality Analysis** (SonarQube)
3. **Mutation Testing** (PIT / PITEST)
4. **Performance Testing** (k6)

---

## Functional Testing

### Unit Testing
- What it is: Verifies individual classes in isolation without loading the full application context
- Purpose: Ensure business logic behaves correctly
- Use cases: Service layer logic, calculations, validations
- Tools: JUnit, Mockito
- What we built: Tested AccountServiceImpl using mocked AccountRepository
- Reference: `src/test/java/com/example/comprehensivetesting/service/AccountServiceTest.java`
- Who writes: Developer

### Mapper Testing
- What it is: Verifies mapping between Entity and DTO objects
- Purpose: Ensure correct data transformation between layers
- Use cases: Entity → DTO conversion, response mapping
- Tools: JUnit
- What we built: Tested AccountMapper for accurate field mapping
- Reference: `src/test/java/com/example/comprehensivetesting/mapper/AccountMapperTest.java`
- Who writes: Developer

### Repository Testing (JPA / Data Layer Testing)
- What it is: Verifies database interactions using JPA in isolation
- Purpose: Ensure correct persistence behavior and entity mappings
- Use cases: CRUD operations, query validation
- Tools: Spring Boot Test, H2 (in-memory database)
- What we built: Tested AccountRepository using @DataJpaTest with H2
- Reference: `src/test/java/com/example/comprehensivetesting/repository/AccountRepositoryTest.java`
- Who writes: Developer

### Controller Testing (Web Layer Testing)
- What it is: Verifies REST API endpoints without loading full application
- Purpose: Ensure correct request handling, response structure, and HTTP status codes
- Use cases: API validation, request/response testing
- Tools: MockMvc
- What we built: Tested AccountController endpoints with mocked service layer
- Reference: `src/test/java/com/example/comprehensivetesting/controller/AccountControllerTest.java`
- Who writes: Developer

### Contract Testing (API Contract Testing)
- What it is: Verifies API response structure and fields
- Purpose: Ensure API remains consistent for consumers (frontend / other services)
- Use cases: Prevent breaking API changes
- Tools: MockMvc
- What we built: Verified JSON response structure and field types
- Reference: `src/test/java/com/example/comprehensivetesting/contract/AccountContractTest.java`
- Who writes: Developer / QA

### Integration Testing (End-to-End within application)
- What it is: Verifies complete flow across all layers (Controller → Service → Repository → DB)
- Purpose: Ensure all components work together correctly
- Use cases: End-to-end API validation
- Tools: SpringBootTest, Testcontainers
- What we built: Tested full application flow using real database container
- Reference: `src/test/java/com/example/comprehensivetesting/integration/AccountIntegrationTest.java`
- Who writes: Developer / QA

### External API Testing (WireMock Testing)
- What it is: Verifies interaction with external APIs using a mock server
- Purpose: Avoid dependency on real external systems and ensure reliable tests
- Use cases: Payment APIs, KYC services, third-party integrations
- Tools: WireMock
- What we built: Mocked external account validation API using WireMock to simulate real API responses
- Reference: `src/test/java/com/example/comprehensivetesting/integration/ExternalServiceIntegrationTest.java`
- Who writes: Developer

### Sanity Testing
- What it is: Quick check to verify the system is up and basic functionality works
- Purpose: Ensure application is usable before deeper testing
- Use cases: CI/CD pipelines, initial validation after deployment
- Tools: SpringBootTest
- Reference: `src/test/java/com/example/comprehensivetesting/sanity/AccountSanityTest.java`
- Who writes: Developer / QA

### Smoke Testing
- What it is: Verifies critical application features end-to-end
- Purpose: Ensure core functionality works before full testing
- Use cases: Post-deployment validation
- Tools: SpringBootTest
- Reference: `src/test/java/com/example/comprehensivetesting/smoke/AccountSmokeTest.java`
- Who writes: Developer / QA

### Regression Testing (Conceptual)
- What it is: Ensures new changes do not break existing functionality
- Purpose: Maintain system stability over time
- Use cases: After feature changes or bug fixes
- Tools: Combination of all automated tests
- Who writes: Developer + QA

## Non-Functional Testing

### Performance Testing (k6)
- What it is: Verifies system behavior under load
- Purpose: Ensure system is scalable, stable, and performant
- Use cases:
    - Load testing (normal traffic)
    - Stress testing (high traffic)
    - Spike testing (sudden traffic increase)
    - Soak testing (long duration stability)
- Tools: k6
- What we built: Implemented load, stress, spike, and soak tests for Account API
- Reference: `performance-tests/`

## Quality Assurance & Performance Validation

### Code Coverage (JaCoCo)
- What it is: Measures how much of the application code is executed during test runs
- Purpose: Identify untested or partially tested code areas
- Key insight: High coverage does NOT guarantee good tests, only that code was executed
- Tools: JaCoCo
- What we built: Generated coverage reports and enforced minimum coverage thresholds
- Report: `target/site/jacoco/index.html`

### Code Quality Analysis (SonarQube)
- What it is: Performs static code analysis without executing the application
- Purpose: Detect bugs, code smells, vulnerabilities, and maintainability issues
- Key insight: Combines code quality analysis with coverage data (from JaCoCo)
- Tools: SonarQube
- What we built: Integrated SonarQube to analyze code quality and enforce quality gates
- Result on SonarQube: `http://localhost:9000`

### Mutation Testing (PIT)
- What it is: Evaluates test effectiveness by introducing small changes (mutations) in code
- Purpose: Ensure tests can detect real defects, not just execute code
- Key insight: Complements coverage by measuring test strength, not just execution
- Tools: PIT (pitest)
- What we built: Executed mutation testing and analyzed survived vs killed mutations
- Report: `target/pit-reports/index.html`

---

## Documentation

Refer docs folder for detailed guides: 
- [JaCoCo](docs/code-coverage-jacoco.md)
- [SonarQube](docs/code-quality-analysis-sonarqube.md) 
- [WireMock](docs/external-api-testing-wiremock.md) 
- [Mutation Testing](docs/mutation-testing-pit.md) 
- [Performance Testing](docs/performance-tests-k6.md)

Each document explains how to configure required tools like SonarQube and k6, which are necessary to run their respective tests (e.g., k6 for performance testing).

---

## How to Run

### Step 1: Clone
```
git clone https://github.com/SirajChaudhary/comprehensive-testing-microservice.git
```

### Step 2: Start Application
```
mvn spring-boot:run
```

### Step 3: Run APIs
Postman collection is included to run API.

### Note
- Create database manually `CREATE DATABASE banking_db;`
- Spring Boot automatically runs schema.sql and data.sql on start.

---

## Run Tests

### All Tests
```
mvn clean test
```
Or simply open the test case -> right click -> Run 'Test Case'

### JaCoCo
```
mvn clean test
```
View coverage report
```
target/site/jacoco/index.html
```
<img width="3472" height="686" alt="image" src="https://github.com/user-attachments/assets/2220d213-8c39-4fd6-a8dd-5553e900eb6c" />

### SonarQube
```
mvn clean verify sonar:sonar
-Dsonar.projectKey=comprehensive-testing-microservice
-Dsonar.host.url=http://localhost:9000 -Dsonar.token=`<sonar_server_token>`
```
View results on sonarqube
```
http://localhost:9000
```
<img width="3472" height="2096" alt="image" src="https://github.com/user-attachments/assets/f2950c78-a102-49ec-abcc-42b4c2ea5dd8" />

### PIT
```
mvn org.pitest:pitest-maven:mutationCoverage
```
View report
```
target/pit-reports/index.html
```
<img width="3472" height="1038" alt="image" src="https://github.com/user-attachments/assets/2b16f49d-1c25-42f5-a83f-4da2ef507efe" />

### Performance Tests
```
k6 run performance-tests/load-test.js

k6 run performance-tests/stress-test.js

k6 run performance-tests/spike-test.js

k6 run performance-tests/soak-test.js
```
<img width="3394" height="1860" alt="image" src="https://github.com/user-attachments/assets/d72e93d7-4277-4784-b14a-01aac848630d" />

---

## Summary

This project demonstrates:

- Focus on testing business logic over trivial code
    - DTOs and Entities are excluded from testing as they contain no business logic
- Use of isolated and realistic test environments
    - H2 for fast tests and Testcontainers for production-like validation
- Mocking of external systems using WireMock to ensure reliable and independent tests
- Emphasis on test effectiveness, not just coverage
    - Mutation testing (PIT) is used to detect weak tests
- Adoption of industry benchmarks:
    - Service layer coverage: 80–90%
    - Controller layer coverage: 70–80%
    - Overall coverage: 70%+
    - Focus on meaningful tests rather than maximizing coverage
- Practical approach to building maintainable and scalable test suites  

---

## License

Free software, [Siraj Chaudhary](https://www.linkedin.com/in/sirajchaudhary/)
