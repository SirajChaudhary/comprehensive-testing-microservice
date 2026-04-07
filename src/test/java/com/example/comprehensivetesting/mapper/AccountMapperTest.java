package com.example.comprehensivetesting.mapper;

import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.model.Account;
import com.example.comprehensivetesting.model.AccountStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
UNIT TEST : ACCOUNT MAPPER
=====================================================

Description:
- Tests mapping logic between Account entity and AccountResponse DTO

Type:
- Unit Test (no Spring context)

Why:
- Ensures correct data transformation
- Prevents mapping-related bugs

=====================================================
*/
class AccountMapperTest {

    /*
    =====================================================
    TEST: ENTITY TO RESPONSE MAPPING
    =====================================================

    Description:
    - Verifies all fields from entity are correctly mapped
      to response DTO

    =====================================================
    */
    @Test
    void shouldMapEntityToResponse() {

        UUID id = UUID.randomUUID();
        String accountNumber = "ACC-123";
        String holderName = "Siraj Chaudhary";
        BigDecimal balance = BigDecimal.valueOf(10000);
        AccountStatus status = AccountStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now();

        Account account = Account.builder()
                .id(id)
                .accountNumber(accountNumber)
                .holderName(holderName)
                .balance(balance)
                .status(status)
                .createdAt(createdAt)
                .build();

        AccountResponse response = AccountMapper.toResponse(account);

        // Verify all fields are mapped correctly
        assertThat(response.id()).isEqualTo(account.getId());
        assertThat(response.accountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(response.holderName()).isEqualTo(account.getHolderName());
        assertThat(response.balance()).isEqualTo(account.getBalance());

        // IMPORTANT: enum → string mapping
        assertThat(response.status()).isEqualTo(account.getStatus().name());

        assertThat(response.createdAt()).isEqualTo(account.getCreatedAt());
    }
}