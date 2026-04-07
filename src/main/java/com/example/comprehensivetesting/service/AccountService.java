package com.example.comprehensivetesting.service;

import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.dto.CreateAccountRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccountById(UUID id);

    List<AccountResponse> getAllAccounts();
}