package com.varun.appbackend.service;


import com.varun.appbackend.model.Account;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.AccountRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service class to manage trading account operations
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }


    /**
     * Initializes a new account with a default balance
     *
     * @param userId the associated user ID
     * @param initialBalance the starting balance
     * @return the created Account entity
     */
    public Account createAccount(Long userId, BigDecimal initialBalance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);

        return accountRepository.save(account);
    }

    /**
     * Retrieves the account for a user
     */
    public Optional<Account> getAccountByUserId(long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Adds balance to a user's account
     */
    public Account topUpBalance(long userId, BigDecimal amount) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for User ID: " + userId));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    /**
     * Deducts balance from a user's account
     */
    public Account deductBalance(long userId, BigDecimal amount) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for User Id: " + userId));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }
}
