package com.trendytech.tcmp.queryengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.trendytech.tcmp.queryengine.cache.Cache;
import com.trendytech.tcmp.queryengine.cache.CacheManager;
import com.trendytech.tcmp.queryengine.cache.ConcurrentHashMapCache;
import com.trendytech.tcmp.queryengine.core.resource.instance.Instance;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        return new CacheManager();
    }

    @Bean(name = "instanceCache")
    public Cache<String, Instance> instanceCache(CacheManager cacheManager) {
        Cache<String, Instance> cache = new ConcurrentHashMapCache<>();
        cacheManager.addCache("instanceCache", cache);
        return cache;
    }
}
