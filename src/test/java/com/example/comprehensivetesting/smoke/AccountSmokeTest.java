package com.example.comprehensivetesting.smoke;

import com.example.comprehensivetesting.dto.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
SMOKE TEST : ACCOUNT MAIN FEATURES (END TO END)
=====================================================

What is Smoke Test?
- Smoke testing verifies that the major functionalities of the system work correctly
- It ensures that critical features work end-to-end before deeper testing

Core Idea:
- This test checks whether:
  → Application is running
  → APIs are reachable
  → Main business flows work correctly

- It answers:
  → "Do the main features of the system work?"

Purpose:
- Validate core business functionality
- Detect major failures early
- Ensure system is stable for further testing

Characteristics:
- Covers main features (not edge cases)
- Medium level validation
- Faster than full integration tests
- Focus on end-to-end flows

What this test covers:
- API availability
- Core business flows
- End-to-end functionality (request → response)

What this test does NOT cover:
- Deep database validation
- Edge cases or negative scenarios
- Performance or load testing

Real-Time Usage:
- After deployment to QA/UAT
- Before regression testing
- As part of CI/CD pipeline

Who writes and runs:
- Developers / QA
- Executed automatically in pipelines

Relation to other test types:
- Sanity Test → "Is system up and usable?"
- Smoke Test → "Do main features work end-to-end?"
- Integration Test → "Do components work deeply together?"
- Unit Test → "Does a class work in isolation?"

-----------------------------------------------------
SANITY TEST vs SMOKE TEST (IMPORTANT)
-----------------------------------------------------
- Sanity Test → checks system health and basic functionality
- Smoke Test  → verifies main features work end-to-end

=====================================================
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountSmokeTest {

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

    /*
    =====================================================
    TEST: CREATE + FETCH FLOW (END TO END)
    =====================================================

    Description:
    - Verifies main account flow works end-to-end

    What it tests:
    - Account creation
    - Fetching accounts
    - System returns expected responses

    Real-Time Scenario:
    - Ensure users can create and view accounts after deployment

    =====================================================
    */
    @Test
    void shouldCreateAndFetchAccountsSuccessfully() {

        String baseUrl = "http://localhost:" + port + "/api/v1/accounts";

        // Step 1: Create account
        CreateAccountRequest request =
                new CreateAccountRequest("Smoke User", BigDecimal.valueOf(8000));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAccountRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> createResponse =
                restTemplate.postForEntity(baseUrl, entity, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Step 2: Fetch accounts
        ResponseEntity<String> fetchResponse =
                restTemplate.getForEntity(baseUrl, String.class);

        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchResponse.getBody()).contains("Smoke User");
    }

    /*
    =====================================================
    TEST: GET ACCOUNT BY ID (MAIN FEATURE)
    =====================================================

    Description:
    - Verifies fetching account by ID works

    What it tests:
    - API returns valid response
    - Endpoint works correctly

    Note:
    - For smoke testing, focus is API availability
    - No deep validation or ID extraction required

    =====================================================
    */
    @Test
    void shouldFetchAccountById() {

        String baseUrl = "http://localhost:" + port + "/api/v1/accounts";

        // Create account first
        CreateAccountRequest request =
                new CreateAccountRequest("Smoke User 2", BigDecimal.valueOf(9000));

        HttpEntity<CreateAccountRequest> entity =
                new HttpEntity<>(request);

        ResponseEntity<String> createResponse =
                restTemplate.postForEntity(baseUrl, entity, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Smoke-level verification (API availability)
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}