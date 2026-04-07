package com.example.comprehensivetesting.mapper;

import com.example.comprehensivetesting.dto.AccountResponse;
import com.example.comprehensivetesting.model.Account;

public class AccountMapper {

    private AccountMapper() {}

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getHolderName(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt()
        );
    }
}