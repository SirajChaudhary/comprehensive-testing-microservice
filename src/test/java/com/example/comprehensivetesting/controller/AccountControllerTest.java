package com.example.comprehensivetesting.controller;

import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.exception.GlobalExceptionHandler;
import com.example.comprehensivetesting.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
=====================================================
WEB LAYER TEST / CONTROLLER TEST : ACCOUNT CONTROLLER
=====================================================

Description:
- This is a Web Layer Test using @WebMvcTest
- Focused on testing controller behavior such as:
  - Request mapping
  - Input validation
  - Response structure
  - HTTP status codes

Important Clarification:
- This test uses JUnit and Mockito, which are also used in unit testing
- So it may look like a unit test at first glance

- However, this is NOT a pure unit test

Why?
- It loads a partial Spring context (Spring MVC components)
- It uses MockMvc to simulate HTTP requests
- It tests request handling and web layer behavior

Conclusion:
- It is partially similar to unit testing (due to mocks and JUnit)
- But correctly classified as:
  → Web Layer Test / Controller Test (Slice Test)

Scope:
- Only controller layer is tested
- Service layer is mocked

How it works:
- Loads only controller layer (not full application context)
- Uses MockMvc to simulate HTTP requests
- Mocks service layer using @MockBean

What is NOT included:
- No database interaction
- No real service/business logic execution
- No full Spring Boot context

Who writes this:
- Developers

Key Concepts Covered:
- API contract testing
- Request/response validation
- JSON assertions
- Mocking dependencies

=====================================================
*/
@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    /*
    =====================================================
    MOCK MVC: SIMULATES HTTP REQUESTS
    =====================================================

    Description:
    - Used to perform API calls without starting server
    - Executes requests against controller layer only

    =====================================================
    */
    @Autowired
    private MockMvc mockMvc;

    /*
    =====================================================
    MOCK SERVICE LAYER
    =====================================================

    Description:
    - Replaces actual service with mock implementation
    - Ensures only controller logic is tested

    =====================================================
    */
    @MockBean
    private AccountService accountService;

    /*
    =====================================================
    TEST: CREATE ACCOUNT SUCCESS
    =====================================================

    Description:
    - Verifies account creation API with valid input

    What it tests:
    - Correct request mapping
    - Valid input processing
    - Returns HTTP 201 Created
    - Response JSON structure is correct

    =====================================================
    */
    @Test
    void shouldCreateAccountSuccessfully() throws Exception {

        String request = """
                {
                    "holderName": "Rohit Kulkarni",
                    "initialBalance": 10000
                }
                """;

        AccountResponse response = new AccountResponse(
                UUID.randomUUID(),
                "ACC-12345678",
                "Rohit Kulkarni",
                BigDecimal.valueOf(10000),
                "ACTIVE",
                LocalDateTime.now()
        );

        // Mock service behavior
        when(accountService.createAccount(org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);

        // Perform API request and validate response
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holderName").value("Rohit Kulkarni"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    /*
    =====================================================
    TEST: VALIDATION FAILURE
    =====================================================

    Description:
    - Verifies validation error for invalid request input

    What it tests:
    - Empty holderName should fail validation
    - Returns HTTP 400 Bad Request

    =====================================================
    */
    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {

        String request = """
                {
                    "holderName": "",
                    "initialBalance": 1000
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    /*
    =====================================================
    TEST: GET ACCOUNT BY ID
    =====================================================

    Description:
    - Verifies fetching account by ID

    What it tests:
    - Valid request returns HTTP 200 OK
    - Correct account data is returned

    =====================================================
    */
    @Test
    void shouldReturnAccountById() throws Exception {

        UUID id = UUID.randomUUID();

        AccountResponse response = new AccountResponse(
                id,
                "ACC-11111111",
                "Ankit Deshpande",
                BigDecimal.valueOf(5000),
                "ACTIVE",
                LocalDateTime.now()
        );

        when(accountService.getAccountById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("Ankit Deshpande"));
    }

    /*
    =====================================================
    TEST: GET ALL ACCOUNTS
    =====================================================

    Description:
    - Verifies fetching all accounts

    What it tests:
    - Returns HTTP 200 OK
    - Returns list of accounts

    =====================================================
    */
    @Test
    void shouldReturnAllAccounts() throws Exception {

        List<AccountResponse> responseList = List.of(
                new AccountResponse(
                        UUID.randomUUID(),
                        "ACC-1001",
                        "Siraj Chaudhary",
                        BigDecimal.valueOf(20000),
                        "ACTIVE",
                        LocalDateTime.now()
                )
        );

        when(accountService.getAllAccounts()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}