package com.varun.appbackend.controller;

import com.varun.appbackend.util.ForgotPasswordRateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Actuator-style endpoint to expose forgot-password rate limiter state.
 * This uses a normal @RestController under the /actuator namespace,
 * since @RestControllerEndpoint is deprecated in Spring Boot 3.3+.
 */
@RestController
public class RateLimitActuatorController {

    private final ForgotPasswordRateLimiter rateLimiter;

    public RateLimitActuatorController(ForgotPasswordRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/actuator/rate-limit")
    public Map<String, Object> rateLimitStatus() {
        Map<String, Object> status = new HashMap<>();
        rateLimiter.getAllEntries().forEach((email, lastRequest) -> {
            long secondsLeft = rateLimiter.getRemainingCooldownSeconds(email);
            if (secondsLeft > 0) {
                status.put(email, secondsLeft + "s remaining");
            }
        });
        return status;
    }
}
