package com.trendytech.tcmp.queryengine.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCache<K, V> implements Cache<K, V> {

    private ConcurrentHashMap<K, V> store = new ConcurrentHashMap<K, V>();

    @Override
    public V get(K name) {
        return store.get(name);
    }

    @Override
    public void put(K name, V value) {
        store.put(name, value);
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void evict(K name) {
        store.remove(name);
    }

    @Override
    public Object getNativeStore() {
        return store;
    }

    @Override
    public void resetNativeStore(Object store) {
        this.store = (ConcurrentHashMap) store;
    }

}
