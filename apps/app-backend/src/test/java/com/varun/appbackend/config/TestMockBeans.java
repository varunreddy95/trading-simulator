package com.varun.appbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.exception.GlobalExceptionHandler;
import com.varun.appbackend.repository.PasswordResetTokenRepository;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.service.*;
import com.varun.appbackend.util.ForgotPasswordRateLimiter;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestMockBeans {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public AccountService accountService() {
        return Mockito.mock(AccountService.class);
    }

    @Bean
    public PositionService positionService() {
        return Mockito.mock(PositionService.class);
    }

    @Bean
    public StockPriceService stockPriceService() {
        return Mockito.mock(StockPriceService.class);
    }

    @Bean
    public StockService stockService() {
        return Mockito.mock(StockService.class);
    }

    @Bean
    public TradeService tradeService() {
        return Mockito.mock(TradeService.class);
    }

    @Bean
    public MailService mailService() {
        return Mockito.mock(MailService.class);
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public PasswordResetTokenRepository passwordResetTokenRepository() {
        return Mockito.mock(PasswordResetTokenRepository.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    @Bean
    public ForgotPasswordRateLimiter forgotPasswordRateLimiter() {
        ForgotPasswordRateLimiter mockLimiter = Mockito.mock(ForgotPasswordRateLimiter.class);
        when(mockLimiter.isAllowed(anyString())).thenReturn(true);
        return mockLimiter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
