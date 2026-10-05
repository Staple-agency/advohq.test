package in.advohq.config;

import in.advohq.security.InMemoryRateLimiter;
import in.advohq.security.RateLimiter;
import in.advohq.security.RedisRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Chooses the {@link RateLimiter} implementation.
 *
 * <ul>
 *   <li>Default (no config, or {@code RATELIMIT_BACKEND=memory}): the
 *       per-instance {@link InMemoryRateLimiter} — zero infrastructure.</li>
 *   <li>{@code RATELIMIT_BACKEND=redis}: the shared {@link RedisRateLimiter},
 *       for exact limits across horizontally-scaled instances. Point it at a
 *       Redis instance with {@code REDIS_URL} (Spring's
 *       {@code spring.data.redis.url}).</li>
 * </ul>
 */
@Configuration
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    @ConditionalOnProperty(name = "advohq.security.ratelimit.backend", havingValue = "redis")
    public RateLimiter redisRateLimiter(StringRedisTemplate redis) {
        log.info("Rate limiting backend: Redis (shared across instances)");
        return new RedisRateLimiter(redis);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    public RateLimiter inMemoryRateLimiter() {
        log.info("Rate limiting backend: in-memory (per-instance). Set RATELIMIT_BACKEND=redis " +
                "for an exact cluster-wide limit when running more than one instance.");
        return new InMemoryRateLimiter();
    }
}
