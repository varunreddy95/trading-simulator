package com.varun.appbackend.service;

import com.varun.appbackend.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Scheduled service to clean up expired password reset tokens from the database
 */
@Service
public class TokenCleanUpService {

    private final PasswordResetTokenRepository tokenRepository;

    public TokenCleanUpService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Runs every hour to delete expired password reset tokens
     */
    @Scheduled(cron = "0 0 * * * *") // At minute 0 of every hour
    public void cleanUpExpiredTokens() {
        int countBefore = tokenRepository.findAll().size();
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        int countAfter = tokenRepository.findAll().size();
        System.out.println("Token cleanup completed. Removed " + (countBefore - countAfter) + " expired tokens.");
    }
}
