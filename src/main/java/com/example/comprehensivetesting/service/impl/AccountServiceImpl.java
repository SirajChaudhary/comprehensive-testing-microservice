package com.example.comprehensivetesting.service.impl;

import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.dto.CreateAccountRequest;
import com.example.comprehensivetesting.exception.ResourceNotFoundException;
import com.example.comprehensivetesting.mapper.AccountMapper;
import com.example.comprehensivetesting.model.Account;
import com.example.comprehensivetesting.model.AccountStatus;
import com.example.comprehensivetesting.repository.AccountRepository;
import com.example.comprehensivetesting.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .holderName(request.holderName())
                .balance(request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Account savedAccount = accountRepository.save(account);

        return AccountMapper.toResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(UUID id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found")); // Handled by GlobalExceptionHandler, We wrote integration test for it.

        return AccountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8);
    }
}