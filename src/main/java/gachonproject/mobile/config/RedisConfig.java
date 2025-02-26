package gachonproject.mobile.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.pool.max-total:50}")
    private int maxTotal;

    @Value("${spring.redis.pool.max-idle:20}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle:5}")
    private int minIdle;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        
        // 커넥션 풀 설정
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(3000);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);
        
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(1))
                .poolConfig(poolConfig)
                .build();
        
        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, MeterRegistry meterRegistry) {
        // 기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.simple())
                .prefixCacheNameWith("intelly:");

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("ingredients", defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put("ingredient", defaultConfig.entryTtl(Duration.ofHours(12)));
        configMap.put("ingredient_features", defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put("ingredient_purposes", defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put("ingredient_skin", defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put("preferred_ingredients", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 캐시 매니저 생성
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .transactionAware()
                .build();

        // 메트릭 등록
        meterRegistry.gauge("redis.cache.size", cacheManager, this::getCacheSize);
        
        return cacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key Serializer 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value Serializer 설정
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // 트랜잭션 지원 활성화
        template.setEnableTransactionSupport(true);
        
        template.afterPropertiesSet();
        return template;
    }

    private double getCacheSize(RedisCacheManager cacheManager) {
        // 전체 캐시 크기 계산
        return cacheManager.getCacheNames().stream()
                .mapToLong(cacheName -> {
                    try {
                        return cacheManager.getCache(cacheName).getNativeCache().toString().length();
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .sum();
    }
}
