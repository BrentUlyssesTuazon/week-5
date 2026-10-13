package com.fintech.walletapi.repository;

import com.fintech.walletapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Derived Query: Spring translates this into "SELECT * FROM accounts WHERE status = ?"
    List<Account> findByStatus(String status);
}
