package com.varun.appbackend.service;

import com.varun.appbackend.model.Account;
import com.varun.appbackend.repository.AccountRepository;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        userRepository = mock(UserRepository.class);
        accountService = new AccountService(accountRepository, userRepository);
    }

    @Test
    void testCreateAccountSuccessfully() {
        Long userId = 1L;
        BigDecimal initialBalance = new BigDecimal("1000.00");

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("varun");


        Account mockAccount = new Account();
        mockAccount.setUser(mockUser);
        mockAccount.setBalance(initialBalance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        Account createdAccount = accountService.createAccount(userId, initialBalance);

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getUser().getId()).isEqualTo(userId);
        assertThat(createdAccount.getBalance()).isEqualTo(initialBalance);

        verify(userRepository, times(1)).findById(userId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccountWhenUserNotFound() {
        Long userId = 99L;
        BigDecimal initialBalance = new BigDecimal("500.00");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountService.createAccount(userId, initialBalance));
    }
}
