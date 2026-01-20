package com.tp.order.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

class CacheConfigurationTest {

    private final CacheConfiguration cacheConfiguration = new CacheConfiguration();

    @Test
    void redisCacheConfiguration_shouldReturnConfiguredRedisCacheConfig() {
        RedisCacheConfiguration config = cacheConfiguration.redisCacheConfiguration();

        assertNotNull(config);

        // Verify TTL is 10 minutes
        Duration ttl = config.getTtl();
        assertEquals(Duration.ofMinutes(10), ttl);

        // Verify null caching is disabled
        assertTrue(config.isDisableCachingNullValues());

        // Verify serializer is set to GenericJackson2JsonRedisSerializer
        var serializerPair = config.getValueSerializationPair();
        assertNotNull(serializerPair);
        assertEquals("org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer",
                serializerPair.getSerializer().getClass().getName());
    }
}