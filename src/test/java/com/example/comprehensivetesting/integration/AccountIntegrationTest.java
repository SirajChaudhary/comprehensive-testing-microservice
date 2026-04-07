package com.example.comprehensivetesting.integration;

import com.example.comprehensivetesting.dto.CreateAccountRequest;
import com.example.comprehensivetesting.model.Account;
import com.example.comprehensivetesting.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
INTEGRATION TEST : ACCOUNT API END TO END
=====================================================

This is an integration test that validates the full application flow using real HTTP calls and a real database. It is sometimes referred to as end-to-end within the backend scope.

Description:
- This is a full integration test using @SpringBootTest
- Tests complete flow:
  Controller → Service → Repository → Database

Type of Testing:
- Integration Test (End to End within application)

Important Clarification:
- This test uses real Spring Boot context
- Real database interaction happens (via Testcontainers PostgreSQL)
- No mocking is used

Testcontainers Note:
- This test requires Docker to be installed and running on the machine
- Testcontainers will automatically start a PostgreSQL Docker container
- The container is used as a real database during test execution
- After tests complete, container is stopped automatically

Why?
- Verifies all layers work together correctly
- Ensures configuration, database, and APIs are properly wired

Conclusion:
- This is NOT a unit test
- NOT a web layer test
- This is a full integration test

Scope:
- Entire application flow is tested
- Real HTTP calls are made
- Real database is used

How it works:
- Starts full Spring Boot application
- Uses random port
- Uses TestRestTemplate to call APIs
- Uses Testcontainers PostgreSQL database

What is included:
- Controller layer
- Service layer
- Repository layer
- Database interaction

Who writes this:
- Developers / QA (depending on team)

Key Concepts Covered:
- End to end backend API testing
- Real HTTP calls
- Database verification
- Spring Boot test context

=====================================================
*/
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AccountIntegrationTest {

    /*
    =====================================================
    TESTCONTAINERS: POSTGRESQL CONTAINER
    =====================================================

    What is Testcontainers?
    - Testcontainers is a Java library that allows you to run lightweight, disposable Docker containers for testing
    - It provides real infrastructure (like PostgreSQL, MySQL, Kafka, Redis) during test execution
    - Instead of using in-memory databases (like H2), it uses real databases inside Docker containers
    - This makes tests more realistic and closer to production behavior

    Description:
    - Starts a real PostgreSQL Docker container for tests
    - Provides isolated database environment

    Configuration Details:
    - withDatabaseName("test_db")
      → Name of database created inside container

    - withUsername("postgres")
      → Username used to connect to container database

    - withPassword("postgres")
      → Password used to connect to container database

    Important Notes:
    - These credentials are used only inside the Docker container
    - They do NOT need to match your local PostgreSQL setup
    - However, you can keep them same as your actual DB for consistency

    - Application connects to this container using dynamic datasource configuration
    - Container is automatically started before tests and stopped after execution

    Prerequisite:
    - Docker must be installed and running on the system
    - Testcontainers uses Docker to spin up PostgreSQL instance

    =====================================================
    */
    @Container
    static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_db")
                    .withUsername("postgres")
                    .withPassword("postgres");

    /*
    =====================================================
    DYNAMIC DATASOURCE CONFIGURATION
    =====================================================

    Description:
    - Ensures application connects to container DB instead of local DB
    - Overrides Spring datasource properties at runtime

    =====================================================
    */
    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    /*
    =====================================================
    TEST REST TEMPLATE: REAL HTTP CLIENT
    =====================================================
    */
    @Autowired
    private TestRestTemplate restTemplate;

    /*
    =====================================================
    ACCOUNT REPOSITORY: DATABASE VALIDATION
    =====================================================

    Description:
    - Used to verify actual database state after API operations

    =====================================================
    */
    @Autowired
    private AccountRepository accountRepository;

    /*
    =====================================================
    RANDOM PORT INJECTION
    =====================================================
    */
    @LocalServerPort
    private int port;

    /*
    =====================================================
    TEST: CREATE ACCOUNT END TO END + DB ASSERTION
    =====================================================

    Description:
    - Verifies full flow of account creation

    What it tests:
    - API request is accepted
    - Data is saved in database
    - Response is returned correctly
    - Database contains expected record

    =====================================================
    */
    @Test
    void shouldCreateAccountEndToEnd() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        CreateAccountRequest request =
                new CreateAccountRequest("Integration User", BigDecimal.valueOf(15000));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAccountRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        // Validate API response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Integration User");

        // Validate database state
        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts)
                .anyMatch(acc ->
                        acc.getHolderName().equals("Integration User") &&
                                acc.getBalance().compareTo(BigDecimal.valueOf(15000)) == 0
                );
    }

    /*
    =====================================================
    TEST: GET ALL ACCOUNTS END TO END
    =====================================================
    */
    @Test
    void shouldFetchAllAccounts() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    /*
    =====================================================
    TEST: VALIDATION FAILURE (NEGATIVE CASE)
    =====================================================

    Description:
    - Verifies validation error for invalid request input

    What it tests:
    - Invalid data should return HTTP 400 Bad Request

    =====================================================
    */
    @Test
    void shouldReturnBadRequestForInvalidInput() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        String invalidRequest = """
                {
                    "holderName": "",
                    "initialBalance": -100
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /*
    =====================================================
    TEST: GET ACCOUNT BY INVALID ID (NEGATIVE CASE)
    =====================================================

    Description:
    - Verifies behavior when account does not exist

    What it tests:
    - API should return error response

    =====================================================
    */
    @Test
    void shouldReturnErrorWhenAccountNotFound() {

        String url = "http://localhost:" + port + "/api/v1/accounts/" +
                "99999999-9999-9999-9999-999999999999";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError()).isTrue();
    }
}