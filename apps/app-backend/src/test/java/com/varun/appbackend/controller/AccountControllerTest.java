package com.varun.appbackend.controller;

import com.varun.appbackend.config.TestMockBeans;
import com.varun.appbackend.model.Account;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AccountController endpoints
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AccountController.class, TestMockBeans.class})
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("Should create a new account successfully")
    void shouldCreateAccount() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");
        user.setPassword("password");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(1000.00));

        // FIX: flexible matching
        when(accountService.createAccount(eq(1L), any(BigDecimal.class))).thenReturn(account);

        mockMvc.perform(post("/api/accounts/create")
                        .param("userId", "1")
                        .param("initialBalance", "1000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }



    @Test
    @DisplayName("Should get account by user Id")
    void shouldGetAccountByUserId() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");
        user.setPassword("password");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(5000.00));

        when(accountService.getAccountByUserId(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/accounts/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").value(5000.00));
    }
}
