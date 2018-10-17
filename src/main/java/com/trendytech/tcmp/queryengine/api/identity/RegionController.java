package com.trendytech.tcmp.queryengine.api.identity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trendytech.tcmp.queryengine.core.identity.region.Region;
import com.trendytech.tcmp.queryengine.core.identity.region.RegionFetcher;

@RestController
public class RegionController {

    @Autowired
    private RegionFetcher regionFetcher;

    @GetMapping("/identity/regions")
    public List<Region> listRegions() {
        return regionFetcher.fetchAll();
    }
}
