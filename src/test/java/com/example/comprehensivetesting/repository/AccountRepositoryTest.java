package com.example.comprehensivetesting.repository;

import com.example.comprehensivetesting.model.Account;
import com.example.comprehensivetesting.model.AccountStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
REPOSITORY TEST : ACCOUNT REPOSITORY (JPA SLICE TEST)
=====================================================

Description:
- Tests the repository layer using @DataJpaTest
- Focuses only on persistence logic (JPA/Hibernate)
- Ensures database interactions behave correctly in isolation
- Does NOT load the full Spring Boot application context

About @DataJpaTest:
- Loads only JPA-related components:
  - Entity classes
  - Repository interfaces
  - Hibernate / JPA configuration
- Does NOT load:
  - Controllers
  - Services
  - Full application context
- Makes tests lightweight, fast, and focused

Database Behavior:
- Automatically replaces configured database with an in-memory database (H2)
- Requires H2 dependency in pom.xml (scope: test)
- Spring Boot auto-configures H2 when it is available on the classpath

Why use H2 here?
- Repository tests should NOT interact with real databases
- Ensures test isolation and avoids modifying real data
- Fast execution (in-memory database)
- Each test runs within a transaction and is rolled back after execution

Can we use real database like PostgreSQL?
- Yes, using Testcontainers or real DB configuration
- But for repository slice tests, H2 is preferred:
  - Faster
  - No external dependency
  - Ideal for validating mappings and CRUD behavior
- Real DB testing is better suited for integration tests

SQL Initialization:
- Spring Boot can execute SQL scripts during test startup:
  - schema.sql (if present)
  - data.sql (if present)
- This behavior depends on project configuration
- Useful for preloading test data

What do we test here?
- save() behavior
- findById() behavior
- findAll() behavior
- Edge cases like:
  - Entity not found
  - Empty database

Do we test Entity here?
- Not directly
- But entity mapping is implicitly tested:
  - Table mapping
  - Column mapping
- If mapping is incorrect, these tests will fail

Why this test is important:
- Validates persistence layer correctness
- Detects mapping and query issues early
- Provides fast feedback during development

=====================================================
*/
@DataJpaTest
@ActiveProfiles("test")   // use application-test.yml (H2 + data-h2.sql)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    /*
    =====================================================
    TEST: SAVE AND FETCH ACCOUNT BY ID
    =====================================================

    Description:
    - Verifies that an Account entity is persisted and retrieved correctly

    What it tests:
    - save() inserts entity into database
    - findById() retrieves entity by primary key
    - Mapping between entity and table

    =====================================================
    */
    @Test
    void shouldSaveAndFindAccount() {

        Account account = Account.builder()
                .accountNumber("ACC-TEST-1")
                .holderName("Repository Test User")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Account saved = accountRepository.save(account);

        Account found = accountRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getHolderName()).isEqualTo("Repository Test User");
        assertThat(found.getAccountNumber()).isEqualTo("ACC-TEST-1");
    }

    /*
    =====================================================
    TEST: FETCH ALL ACCOUNTS
    =====================================================

    Description:
    - Verifies that multiple entities can be saved and retrieved

    What it tests:
    - save() for multiple records
    - findAll() returns all records

    =====================================================
    */
    @Test
    void shouldFetchAllAccounts() {

        Account account1 = Account.builder()
                .accountNumber("ACC-TEST-2")
                .holderName("User One")
                .balance(BigDecimal.valueOf(2000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Account account2 = Account.builder()
                .accountNumber("ACC-TEST-3")
                .holderName("User Two")
                .balance(BigDecimal.valueOf(3000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts).hasSizeGreaterThanOrEqualTo(2);
    }

    /*
    =====================================================
    TEST: RETURN EMPTY WHEN ACCOUNT NOT FOUND
    =====================================================

    Description:
    - Verifies repository behavior when entity does not exist

    What it tests:
    - findById() returns empty Optional for unknown ID

    =====================================================
    */
    @Test
    void shouldReturnEmptyWhenAccountNotFound() {

        UUID randomId = UUID.randomUUID();

        var result = accountRepository.findById(randomId);

        assertThat(result).isEmpty();
    }

    /*
    =====================================================
    TEST: RETURN EMPTY LIST WHEN NO DATA EXISTS
    =====================================================

    Description:
    - Verifies behavior when database has no records

    What it tests:
    - findAll() returns empty list

    =====================================================
    */
    @Test
    void shouldReturnEmptyListWhenNoAccountsExist() {

        accountRepository.deleteAll();

        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts).isEmpty();
    }
}