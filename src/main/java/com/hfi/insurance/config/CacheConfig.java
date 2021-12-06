package com.hfi.insurance.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, Object> caffeineCache() {
        CacheRemovalListener cacheRemovalListener = new CacheRemovalListener();
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(8, TimeUnit.HOURS)
                // 初始的缓存空间大小100M
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(20000)
                .removalListener(cacheRemovalListener)
                .build();
    }

}