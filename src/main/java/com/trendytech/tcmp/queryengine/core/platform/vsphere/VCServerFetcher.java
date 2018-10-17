package com.trendytech.tcmp.queryengine.core.platform.vsphere;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trendytech.tcmp.queryengine.core.Fetcher;

@Service
public class VCServerFetcher implements Fetcher<Integer, VCServer> {

    @Autowired
    private VCServerMapper vcServerMapper;

    @Override
    public VCServer fetch(Integer key) {
        return null;
    }

    @Override
    public List<VCServer> fetchAll() {
        return vcServerMapper.listVCServers();
    }

    @Override
    public List<VCServer> filter(Map<String, Object> conditions) {
        List<VCServer> vcServers = fetchAll();

        String region = String.valueOf(conditions.get("region"));
        if (StringUtils.isNotEmpty(region)) {
            for (VCServer vcServer : vcServers) {
                if (!StringUtils.equals(vcServer.getRegionId(), region)) {
                    vcServers.remove(vcServer);
                }
            }
        }
        return vcServers;
    }

}
