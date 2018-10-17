package com.trendytech.tcmp.queryengine.support.openstack;

public enum Module {
    Nova("nova"), Keystone("keystone");

    private String value;

    private Module(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
