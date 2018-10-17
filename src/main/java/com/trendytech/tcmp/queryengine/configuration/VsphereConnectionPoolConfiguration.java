package com.trendytech.tcmp.queryengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.trendytech.tcmp.queryengine.support.vsphere.ConnectionPool;
import com.trendytech.tcmp.queryengine.support.vsphere.FlatConnectionPool;

@Configuration
public class VsphereConnectionPoolConfiguration {

    @Bean("vsphereConnectionPool")
    public ConnectionPool connectionPool() {
        return new FlatConnectionPool();
    }
}
