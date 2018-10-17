package com.trendytech.tcmp.queryengine.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.trendytech.tcmp.queryengine.support.openstack.OpenstackRestExecutor;

@Configuration
@ConfigurationProperties(prefix = "openstack.keystone")
public class OpenstackRestExecutorConfiguration {
    private String endpoint;
    private String domain;
    private String project;
    private String username;
    private String password;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public OpenstackRestExecutor openstackRestExecutor() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(5000);
        httpRequestFactory.setConnectTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        OpenstackRestExecutor executor =
                        new OpenstackRestExecutor(endpoint, domain, project, username, password, restTemplate);
        executor.initEndpoints();
        return executor;
    }
}
