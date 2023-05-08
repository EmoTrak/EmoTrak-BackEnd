package com.example.emotrak.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

//    @Bean
//    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//        // 직렬화 설정
//        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
//                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());
//
//        // RedisCacheConfiguration에 직렬화 설정 적용
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(60))
//                .serializeValuesWith(jsonSerializer);
//
//        // RedisCacheManager에 설정 적용
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration)
//                .build();
//    }

    /* RedisCacheManager는 캐시된 데이터를 관리하는 데 사용
     * redisCacheManager 메소드를 사용하여 RedisCacheManager를 생성
     * RedisConnectionFactory를 인자로 받아 RedisCacheConfiguration을 설정하며, entryTtl을 통해 TTL을 설정
     * TTL 값이 너무 짧으면 캐시를 효과적으로 사용하지 못하고, 너무 길면 캐시된 데이터가 업데이트 되었음에도 불구하고 오래된 데이터를 반환하는 문제가 발생할 수 있음.
     */

}