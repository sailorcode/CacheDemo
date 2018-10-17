package com.trendytech.tcmp.queryengine.core;

import java.util.List;
import java.util.Map;

public interface Fetcher<K, V> {
    V fetch(K key);

    List<V> fetchAll();

    List<V> filter(Map<String, Object> conditions);
}
