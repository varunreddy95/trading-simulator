package com.varun.appbackend.actuator;

import com.varun.appbackend.util.ForgotPasswordRateLimiter;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Actuator endpoint to expose current forgot-password rate limiter status.
 * 🚀 Future Upgrade Note:
 * If you plan to scale this application or adopt a centralized monitoring stack,
 * consider switching to Prometheus + Micrometer:
 * 1. Add `micrometer-registry-prometheus` to your dependencies.
 * 2. Replace this endpoint with a Prometheus `Gauge` metric to track active rate-limited users.
 * 3. Expose metrics at `/actuator/prometheus` for Prometheus scraping.
 * 4. Visualize and alert on rate limits using Grafana dashboards.
 * Until then, this endpoint offers a lightweight and effective observability tool for single-instance apps.
 */

@Component
@Endpoint(id = "rate-limit")
public class RateLimitEndpoint {

    private final ForgotPasswordRateLimiter rateLimiter;

    public RateLimitEndpoint(ForgotPasswordRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @ReadOperation
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
