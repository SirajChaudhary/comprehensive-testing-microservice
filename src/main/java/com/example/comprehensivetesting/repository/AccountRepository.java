package com.example.comprehensivetesting.repository;

import com.example.comprehensivetesting.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}