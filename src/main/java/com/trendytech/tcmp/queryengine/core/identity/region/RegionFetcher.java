package com.trendytech.tcmp.queryengine.core.identity.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trendytech.tcmp.queryengine.core.Fetcher;
import com.trendytech.tcmp.queryengine.support.openstack.Module;
import com.trendytech.tcmp.queryengine.support.openstack.OpenstackRestExecutor;

@Service
public class RegionFetcher implements Fetcher<String, Region> {

    @Autowired
    private OpenstackRestExecutor excutor;

    @Override
    public Region fetch(String name) {
        for (Region region : fetchAll()) {
            if (StringUtils.equals(name, region.getId())) {
                return region;
            }
        }
        return null;
    }

    @Override
    public List<Region> fetchAll() {
        List<Region> regions = new ArrayList<>();

        Map dataMap = excutor.get(null, Module.Keystone, "/v3/regions", Map.class);
        for (Map<String, Object> regionMap : (List<Map<String, Object>>) dataMap.get("regions")) {
            Region region = new Region();
            region.setId(String.valueOf(regionMap.get("id")));
            region.setDescription(String.valueOf(regionMap.get("description")));
            regions.add(region);
        }

        return regions;
    }

    @Override
    public List<Region> filter(Map<String, Object> conditions) {
        return null;
    }

}
