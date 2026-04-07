package com.example.comprehensivetesting.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/*
=====================================================
EXTERNAL CLIENT : ACCOUNT VALIDATION SERVICE
=====================================================

Description:
- Client responsible for communicating with an external validation API
- Makes HTTP calls to validate account details

Responsibilities:
- Builds request URL using configurable base URL
- Calls external API using RestTemplate
- Returns response in a simplified form (boolean)

Why this exists:
- Separates external communication logic from business logic
- Keeps service layer clean and focused
- Improves testability (can be mocked or simulated via WireMock)

Configuration:
- Base URL is injected from application properties:
    external.base-url=http://<host>:<port>
- Allows switching between:
  - Real external service (production)
  - Mock server like WireMock (testing)

Relation with Integration Test:
- In WireMock tests, this base URL is overridden dynamically
- Ensures all HTTP calls are redirected to mock server instead of real API

=====================================================
*/
@Component
public class ExternalValidationClient {

    /*
    RestTemplate:
    - Spring utility for making HTTP calls
    - Used here for simple GET request to external API
    */
    private final RestTemplate restTemplate = new RestTemplate();

    /*
    Base URL of external service:
    - Injected via application configuration
    - Example: http://localhost:8089 (WireMock in tests)
    */
    private final String baseUrl;

    public ExternalValidationClient(@Value("${external.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /*
    =====================================================
    METHOD: VALIDATE ACCOUNT
    =====================================================

    Description:
    - Calls external API to validate account number

    Flow:
    1. Constructs URL: '{baseUrl}/validate/{accountNumber}'
    2. Sends HTTP GET request
    3. Parses response as Boolean
    4. Returns result to service layer

    Expected External API Contract:
    - Endpoint: GET '/validate/{accountNumber}'
    - Response: true or false

    Behavior:
    - Returns true → account is valid
    - Returns false → account is invalid
    - If external service fails → exception is thrown

    =====================================================
    */
    public boolean validateAccount(String accountNumber) {
        Boolean response = restTemplate.getForObject(
                baseUrl + "/validate/" + accountNumber,
                Boolean.class
        );

        return Boolean.TRUE.equals(response);
    }
}