package com.trendytech.tcmp.queryengine.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private ConcurrentHashMap<String, Cache<?, ?>> cacheStore = new ConcurrentHashMap<>();

    public Cache<?, ?> getCache(String name) {
        return cacheStore.get(name);
    }

    public void addCache(String name, Cache<?, ?> cache) {
        cacheStore.put(name, cache);
    }
}
