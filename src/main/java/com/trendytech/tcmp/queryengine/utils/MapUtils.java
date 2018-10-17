package com.trendytech.tcmp.queryengine.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MapUtils {
    public static Object get(Map map, String multiKey) {
        String[] keys = StringUtils.split(multiKey, ".");
        for (int i = 0; i < keys.length - 1; i++) {
            Object obj = map.get(keys[i]);
            if (obj == null || !(obj instanceof Map)) {
                return null;
            }
            map = (Map) obj;
        }

        return map.get(keys[keys.length - 1]);
    }
}
