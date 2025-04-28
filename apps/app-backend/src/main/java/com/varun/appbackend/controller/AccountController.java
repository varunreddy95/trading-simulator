package com.varun.appbackend.controller;


import com.varun.appbackend.dto.AccountResponseDTO;
import com.varun.appbackend.model.Account;
import com.varun.appbackend.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller to manage account-related endpoints
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * POST a new trading account for a user
     *
     * @param userId         the ID of the user to associate the account with
     * @param initialBalance the initial balance to set
     * @return the created Account object
     */
    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestParam long userId, @RequestParam BigDecimal initialBalance){
        Account account = accountService.createAccount(userId, initialBalance);
        AccountResponseDTO response = new AccountResponseDTO(account.getId(), account.getBalance());
        return ResponseEntity.ok(response);
    }

    /**
     * GET the account details for a give user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponseDTO> getAccountByUserId(@PathVariable long userId) {
        return accountService.getAccountByUserId(userId)
                .map(account -> ResponseEntity.ok(new AccountResponseDTO(account.getId(), account.getBalance())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT the account balance (e.g. for top-up)
     */
    @PutMapping("/top-up")
    public ResponseEntity<Account> topUpBalance(@RequestParam long userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.topUpBalance(userId, amount));
    }

    /**
     * Deducts from the account balance (e.g. for placing a trade)
     */
    @PutMapping("/deduct")
    public ResponseEntity<Account> deductBalance(@RequestParam long userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.deductBalance(userId, amount));
    }
}
