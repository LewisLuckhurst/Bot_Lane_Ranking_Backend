package com.botlaneranking.www.BotLaneRankingBackend.config.pojo;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterComponent {
    private final RateLimiter rateLimiter = RateLimiter.create(1);

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
}
