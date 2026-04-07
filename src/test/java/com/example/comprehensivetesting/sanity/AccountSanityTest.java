package com.example.comprehensivetesting.sanity;

import com.example.comprehensivetesting.dto.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
SANITY TEST : ACCOUNT APPLICATION HEALTH + CORE FUNCTIONALITY
=====================================================

What is Sanity Test?
- Sanity testing is a quick validation to ensure that the application is stable after changes
- It verifies that the system is usable before running detailed tests

Core Idea:
- This test checks whether:
  → Application has started correctly
  → APIs are reachable
  → Basic functionality is working

- It answers:
  → "Is the system up and usable?"

Types of Sanity Tests Covered Here:
1. Application Startup Sanity Test
   - Verifies Spring Boot context loads successfully

2. API Sanity Test
   - Verifies critical endpoints are reachable

3. Core Functionality Sanity Test
   - Verifies important business flow (account creation)

Purpose:
- Detect major failures early
- Fail fast in CI/CD pipelines
- Avoid running expensive test suites if system is broken

Characteristics:
- Fast execution
- Limited scope
- Focus on critical paths only
- No deep validation (like DB assertions or edge cases)

What this test covers:
- ApplicationContext initialization
- Bean creation and dependency injection
- API availability
- Core functionality (happy path)

What this test does NOT cover:
- Business logic validation in depth
- Database validation
- Edge cases or negative scenarios
- Performance or load testing

Real-Time Usage:
- After deployment (QA/UAT/Production)
- After configuration or environment changes
- After dependency upgrades
- As first step in CI/CD pipeline (health check)

Who writes and runs:
- Written by Developers / QA
- Executed automatically in CI/CD pipelines

Relation to other test types:
- Sanity Test → "Is system up and usable?"
- Smoke Test → "Do main features work end-to-end?"
- Integration Test → "Do components work together deeply?"
- Unit Test → "Does a class work in isolation?"

=====================================================
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountSanityTest {

    /*
    =====================================================
    TEST REST TEMPLATE: REAL HTTP CLIENT
    =====================================================
    */
    @Autowired
    private TestRestTemplate restTemplate;

    /*
    =====================================================
    RANDOM PORT INJECTION
    =====================================================
    */
    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    /*
    =====================================================
    TEST: APPLICATION CONTEXT LOAD (STARTUP SANITY)
    =====================================================

    Description:
    - Verifies that Spring Boot application starts successfully

    What it tests:
    - ApplicationContext is initialized successfully
    - All beans are created successfully
    - No configuration or dependency wiring issues

    Important:
    - Assertion is added to satisfy static analysis tools (e.g., SonarQube)
    - Ensures ApplicationContext is successfully initialized and injected
    - If context fails to load → test fails automatically

    Real-Time Scenario:
    - Used in CI pipeline to fail fast if application cannot start

    =====================================================
    */
    @Test
    void shouldLoadApplicationContextSuccessfully() {
        assertThat(context).isNotNull();
    }

    /*
    =====================================================
    TEST: API AVAILABILITY (SYSTEM HEALTH CHECK)
    =====================================================

    Description:
    - Verifies that application is running and API is reachable

    What it tests:
    - Server is up and running
    - Endpoint is accessible
    - Returns HTTP 200 OK

    Real-Time Scenario:
    - After deployment, verify system is live and responding

    =====================================================
    */
    @Test
    void shouldVerifyApiIsReachable() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    /*
    =====================================================
    TEST: CORE FUNCTIONALITY (ACCOUNT CREATION)
    =====================================================

    Description:
    - Verifies that critical business functionality works

    What it tests:
    - API is reachable
    - Request is processed successfully
    - Returns HTTP 201 Created

    Real-Time Scenario:
    - After deployment, ensure users can perform key operations
    - Confirms system is functionally usable

    =====================================================
    */
    @Test
    void shouldCreateAccountSuccessfully() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        CreateAccountRequest request =
                new CreateAccountRequest("Sanity User", BigDecimal.valueOf(5000));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAccountRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Sanity User");
    }
}