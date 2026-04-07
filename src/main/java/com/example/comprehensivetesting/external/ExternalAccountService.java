package com.example.comprehensivetesting.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
=====================================================
SERVICE : EXTERNAL ACCOUNT VALIDATION FLOW
=====================================================

Description:
- Service layer component that depends on an external system
- Delegates validation logic to ExternalValidationClient
- Acts as an example of integrating with external APIs

Responsibilities:
- Calls external validation service before proceeding
- Interprets external response
- Controls business flow based on validation result

Why this is important:
- In real-world applications, services often depend on external systems
  (e.g., payment gateway, KYC service, fraud detection)
- This class demonstrates how such dependencies are handled

What it handles:
- Success scenario:
  - External service returns true → processing continues
- Failure scenarios:
  - External service returns false → business validation failure
  - External service throws error → propagated as exception

Relation with WireMock Test:
- This service is tested using WireMock in integration tests
- WireMock simulates external API responses (success/failure)
- Ensures behavior is validated without calling real systems

=====================================================
*/
@Service
@RequiredArgsConstructor
public class ExternalAccountService {

    private final ExternalValidationClient externalValidationClient;

    /*
    =====================================================
    METHOD: PROCESS ACCOUNT
    =====================================================

    Description:
    - Validates account using external service before processing

    Flow:
    1. Calls external API via ExternalValidationClient
    2. Receives validation result (true/false)
    3. If validation fails → throws exception
    4. If validation succeeds → continues processing

    Note:
    - Exception thrown here is intentional to simulate
      failure handling in downstream systems

    =====================================================
    */
    public void processAccount(String accountNumber) {

        boolean isValid = externalValidationClient.validateAccount(accountNumber);

        if (!isValid) {
            throw new IllegalStateException("External validation failed");
        }

        // continue business logic (simulated)
    }
}