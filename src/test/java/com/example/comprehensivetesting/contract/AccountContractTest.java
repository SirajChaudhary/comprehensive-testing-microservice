package com.example.comprehensivetesting.contract;

import com.example.comprehensivetesting.controller.AccountController;
import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
=====================================================
CONTRACT TEST : ACCOUNT API RESPONSE
=====================================================

Description:
- Verifies API response structure (contract)
- Ensures response fields remain consistent
- Focuses only on API contract, not business logic

What it tests:
- JSON field presence
- Data types of fields
- API response format and structure
- HTTP status correctness

What it does NOT test:
- Business logic (handled in service tests)
- Database interactions
- External integrations

Why this is important:
- Prevents breaking changes in API
- Ensures backward compatibility for consumers
- Detects accidental changes like:
  - Renaming fields
  - Removing fields
  - Changing response structure
- Provides safety during refactoring

Testing Approach:
- Uses @WebMvcTest (loads only controller layer)
- Service layer is mocked using @MockBean
- Validates response using MockMvc and jsonPath

Real-World Relevance:
- Ensures frontend / other services depending on this API do not break
- Acts as a lightweight contract validation between producer and consumer

=====================================================
*/
@WebMvcTest(AccountController.class)
class AccountContractTest {

    @Autowired
    private MockMvc mockMvc;

    /*
    MockBean:
    - Replaces actual AccountService with a mock
    - Allows controlled response for testing contract
    */
    @MockBean
    private AccountService accountService;

    /*
    =====================================================
    TEST: GET ACCOUNT RESPONSE CONTRACT
    =====================================================

    Description:
    - Verifies GET /api/v1/accounts/{id} response structure

    Flow:
    1. Mock service response
    2. Call API endpoint
    3. Validate response structure and types

    Expected Behavior:
    - Returns HTTP 200 OK
    - Response contains all required fields
    - Field types are correct

    Contract Fields:
    - id
    - accountNumber
    - holderName
    - balance
    - status
    - createdAt

    =====================================================
    */
    @Test
    void shouldMatchAccountResponseContract() throws Exception {

        // Mock service response
        given(accountService.getAccountById(any()))
                .willReturn(new AccountResponse(
                        UUID.randomUUID(),
                        "ACC-123",
                        "Siraj Chaudhary",
                        BigDecimal.valueOf(1000),
                        "ACTIVE",
                        LocalDateTime.now()
                ));

        // Perform API call and validate contract
        mockMvc.perform(get("/api/v1/accounts/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())

                // Field presence validation
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accountNumber").exists())
                .andExpect(jsonPath("$.holderName").exists())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.createdAt").exists())

                // Data type validation
                .andExpect(jsonPath("$.holderName").isString())
                .andExpect(jsonPath("$.balance").isNumber());
    }
}