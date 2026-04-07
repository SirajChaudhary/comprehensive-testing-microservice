package com.example.comprehensivetesting.service;

import com.example.comprehensivetesting.dto.CreateAccountRequest;
import com.example.comprehensivetesting.model.Account;
import com.example.comprehensivetesting.model.AccountStatus;
import com.example.comprehensivetesting.repository.AccountRepository;
import com.example.comprehensivetesting.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
=====================================================
UNIT TEST: ACCOUNT SERVICE TEST
=====================================================

Description:
- Unit tests for AccountServiceImpl
- Verifies business logic without involving database or Spring context

Type of Testing:
- Unit Testing (isolated, fast, no external dependencies)

How it works:
- Repository layer is mocked using Mockito
- Service logic is tested independently

Who writes this:
- Developers

Key Concepts Covered:
- Mocking dependencies
- Behavior verification
- Assertion testing
- Exception handling

=====================================================
*/
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AccountServiceTest {

    /*
    =====================================================
    MOCK DEPENDENCY: ACCOUNT REPOSITORY
    =====================================================

    Description:
    - Simulates database interactions
    - Prevents real DB calls

    Behavior:
    - Controlled using Mockito (when → thenReturn)

    =====================================================
    */
    @Mock
    private AccountRepository accountRepository;

    /*
    =====================================================
    CLASS UNDER TEST: ACCOUNT SERVICE IMPLEMENTATION
    =====================================================

    Description:
    - Actual service class being tested
    - Mockito injects mocked dependencies automatically

    =====================================================
    */
    @InjectMocks
    private AccountServiceImpl accountService;

    /*
    =====================================================
    TEST: CREATE ACCOUNT SUCCESS
    =====================================================

    Description:
    - Verifies successful account creation

    What it tests:
    - Service creates account with correct data
    - Repository save method is invoked
    - Response mapping is correct

    Flow:
    1. Create request object
    2. Mock repository save behavior
    3. Call service method
    4. Validate response
    5. Verify repository interaction

    =====================================================
    */
    @Test
    void shouldCreateAccountSuccessfully() {

        // Step 1: Prepare input request
        CreateAccountRequest request =
                new CreateAccountRequest("Rohit Kulkarni", BigDecimal.valueOf(10000));

        // Step 2: Mock repository response
        Account savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-12345678")
                .holderName("Rohit Kulkarni")
                .balance(BigDecimal.valueOf(10000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Mockito behavior: simulate save operation
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Step 3: Call service method
        var response = accountService.createAccount(request);

        // Step 4: Assertions
        assertThat(response).isNotNull();
        assertThat(response.holderName()).isEqualTo("Rohit Kulkarni");
        assertThat(response.balance()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(response.status()).isEqualTo("ACTIVE");

        // Step 5: Verify repository interaction
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    /*
    =====================================================
    TEST: DEFAULT BALANCE HANDLING
    =====================================================

    Description:
    - Verifies that null balance defaults to ZERO

    What it tests:
    - Service sets balance to BigDecimal.ZERO when input is null

    =====================================================
    */
    @Test
    void shouldSetZeroBalanceWhenInitialBalanceIsNull() {

        // Input with null balance
        CreateAccountRequest request =
                new CreateAccountRequest("Sophia Miller", null);

        // Mock repository response
        Account savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-87654321")
                .holderName("Sophia Miller")
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Execute
        var response = accountService.createAccount(request);

        // Validate
        assertThat(response.balance()).isEqualTo(BigDecimal.ZERO);
    }

    /*
    =====================================================
    TEST: GET ACCOUNT BY ID (SUCCESS)
    =====================================================

    Description:
    - Verifies fetching account when record exists

    What it tests:
    - Repository returns data
    - Service maps entity to response correctly

    =====================================================
    */
    @Test
    void shouldReturnAccountById() {

        UUID id = UUID.randomUUID();

        Account account = Account.builder()
                .id(id)
                .accountNumber("ACC-11111111")
                .holderName("Ankit Deshpande")
                .balance(BigDecimal.valueOf(5000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Mock repository behavior
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        // Execute
        var response = accountService.getAccountById(id);

        // Validate
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.holderName()).isEqualTo("Ankit Deshpande");
    }

    /*
    =====================================================
    TEST: ACCOUNT NOT FOUND EXCEPTION
    =====================================================

    Description:
    - Verifies exception when account does not exist

    What it tests:
    - Repository returns empty
    - Service throws RuntimeException

    =====================================================
    */
    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        UUID id = UUID.randomUUID();

        // Mock empty result
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        // Validate exception
        assertThatThrownBy(() -> accountService.getAccountById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
    }
}