package com.trendytech.tcmp.queryengine.cache;

import java.util.List;

public interface Cache<K, V> {
    public V get(K name);

    public void put(K name, V value);

    public List<V> getAll();

    public void evict(K name);

    public Object getNativeStore();

    public void resetNativeStore(Object store);
}
