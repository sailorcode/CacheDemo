package com.trendytech.tcmp.queryengine.api.resource;

import com.trendytech.tcmp.queryengine.utils.Page;

public class InstanceQueryParam extends Page {
    private String regionId;
    private String projectId;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
