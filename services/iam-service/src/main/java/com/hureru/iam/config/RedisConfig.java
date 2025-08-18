package com.hureru.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zheng
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 1. Configure default cache settings
        RedisCacheConfiguration defaultConfig = createCacheConfiguration(Duration.ofMinutes(30));

        // 2. Configure specific cache settings for different cache names
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache for products, base TTL 20 mins + random 10 mins
        cacheConfigurations.put("products", createCacheConfiguration(Duration.ofMinutes(20)));
        // Cache for artisans, base TTL 30 mins + random 10 mins
        cacheConfigurations.put("artisans", createCacheConfiguration(Duration.ofMinutes(30)));
        // Cache for user details, base TTL 60 mins + random 30 mins
        cacheConfigurations.put("user-details", createCacheConfiguration(Duration.ofHours(1)));
        // Cache for user addresses, base TTL 10 mins + random 5 mins
        cacheConfigurations.put("userAddresses", createCacheConfiguration(Duration.ofMinutes(10)));
        // Cache for user carts, base TTL 2 hours + random 30 mins
        cacheConfigurations.put("userCarts", createCacheConfiguration(Duration.ofHours(2)));


        return RedisCacheManager.builder(redisConnectionFactory)
                // Set default configuration
                .cacheDefaults(defaultConfig)
                // Apply specific configurations
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private RedisCacheConfiguration createCacheConfiguration(Duration baseTtl) {
        // Add a random offset to the base TTL to prevent cache avalanche
        // The random part is up to 1/3 of the base TTL
        long randomSeconds = ThreadLocalRandom.current().nextLong(baseTtl.toSeconds() / 3);
        Duration finalTtl = baseTtl.plusSeconds(randomSeconds);

        // Configure JSON serialization for cache values
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        return RedisCacheConfiguration.defaultCacheConfig()
                // Apply the randomized TTL
                .entryTtl(finalTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
        // We are removing .disableCachingNullValues() to allow caching empty objects
        // This helps prevent cache penetration
    }
}
