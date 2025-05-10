package com.varun.appbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter for forgot-password requests.
 * Suitable for small applications with a single backend instance.
 * If scaling horizontally or needing persistence, consider using Redis.
 */
@Component
public class ForgotPasswordRateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordRateLimiter.class);
    private static final int LIMIT_MINUTES = 5;

    private final Map<String, LocalDateTime> lastRequestMap = new ConcurrentHashMap<>();

    public boolean isAllowed(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastTime = lastRequestMap.get(email);

        if (lastTime == null || lastTime.plusMinutes(LIMIT_MINUTES).isBefore(now)) {
            lastRequestMap.put(email, now);
            return true;
        }

        logger.warn("Rate limit hit for email {} - wait {}s remaining", email, getRemainingCooldownSeconds(email));
        return false;
    }

    public long getRemainingCooldownSeconds(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastTime = lastRequestMap.get(email);
        if (lastTime == null) return 0;

        long seconds = java.time.Duration.between(now, lastTime.plusMinutes(LIMIT_MINUTES)).getSeconds();
        return Math.max(seconds, 0);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 minutes
    public void cleanUpStaleEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(LIMIT_MINUTES);
        int before = lastRequestMap.size();

        lastRequestMap.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));

        int after = lastRequestMap.size();
        int removed = before - after;

        if (removed > 0) {
            logger.info("Rate limiter cleanup: removed {} stale entries", removed);
        }
    }

    public Map<String, LocalDateTime> getAllEntries() {
        return new HashMap<>(lastRequestMap); // return a copy for safety
    }

}
