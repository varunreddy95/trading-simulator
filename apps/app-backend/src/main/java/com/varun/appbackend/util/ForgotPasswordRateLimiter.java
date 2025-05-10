package com.varun.appbackend.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter for forgot-password requests.
 * Suitable for small applications with a single backend instance.
 * If scaling horizontally or needing persistence, consider using Redis.
 */
@Component
public class ForgotPasswordRateLimiter {

    private static final int LIMIT_MINUTES = 5;

    // email -> last request time
    private final Map<String, LocalDateTime> lastRequestMap = new ConcurrentHashMap<>();

    /**
     * Checks if the user is allowed to request a new password reset.
     */
    public boolean isAllowed(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastTime = lastRequestMap.get(email);

        if (lastTime == null || lastTime.plusMinutes(LIMIT_MINUTES).isBefore(now)) {
            lastRequestMap.put(email, now);
            return true;
        }
        return false;
    }

    /**
     * Gets the remaining cooldown time for the given email.
     */
    public long getRemainingCooldownSeconds(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastTime = lastRequestMap.get(email);
        if (lastTime == null) return 0;

        long seconds = java.time.Duration.between(now, lastTime.plusMinutes(LIMIT_MINUTES)).getSeconds();
        return Math.max(seconds, 0);
    }

    /**
     * Scheduled cleanup that runs every 10 minutes and removes expired entries.
     */
    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 minutes
    public void cleanUpStaleEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(LIMIT_MINUTES);
        lastRequestMap.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }
}
