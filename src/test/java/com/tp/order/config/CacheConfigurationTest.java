package com.tp.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CacheConfiguration.class)
@TestPropertySource(properties = "app.cache.enabled=true")
class CacheConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisCacheConfiguration redisCacheConfiguration;

    @Test
    void redisCacheConfigurationBean_shouldBeCreated_whenCacheEnabled() {
        assertNotNull(redisCacheConfiguration);
        assertTrue(applicationContext.containsBean("redisCacheConfiguration"));
    }

    @Test
    void shouldConfigureEntryTtlCorrectly() {
        assertEquals(Duration.ofMinutes(10), redisCacheConfiguration.getTtl());
    }

    @Test
    void shouldUseGenericJacksonJsonSerializer() {
        Object serializer =
                redisCacheConfiguration
                        .getValueSerializationPair()
                        .getWriter();

        assertTrue(serializer instanceof GenericJackson2JsonRedisSerializer);
    }

    @Test
    void shouldDisableCachingNullValues() {
        // Behavior-based assertion (correct way)
        boolean canSerializeNull =
                ((RedisSerializer<Object>) redisCacheConfiguration
                        .getValueSerializationPair()
                        .getWriter())
                        .canSerialize(null);

        assertFalse(canSerializeNull);
    }
}
