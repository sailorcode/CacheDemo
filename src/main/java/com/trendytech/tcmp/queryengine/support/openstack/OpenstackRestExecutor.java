package com.trendytech.tcmp.queryengine.support.openstack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.trendytech.tcmp.queryengine.utils.MapUtils;

public class OpenstackRestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenstackRestExecutor.class);

    private String keystoneEndpoint;
    private String domain;
    private String project;
    private String username;
    private String password;

    private Map<String, String> endpoints = new HashMap<>();

    private RestTemplate restTemplate;

    public OpenstackRestExecutor(String keystoneEndpoint, String domain, String project, String username,
                    String password, RestTemplate restTemplate) {
        this.keystoneEndpoint = keystoneEndpoint;
        this.domain = domain;
        this.project = project;
        this.username = username;
        this.password = password;
        this.restTemplate = restTemplate;
        if (this.restTemplate == null) {
            this.restTemplate = new RestTemplate();
        }

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new TokenSetClientRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
    }

    public <T> T get(String region, Module module, String url, Class<T> responseType) {
        return restTemplate.getForObject(getUrl(region, module, url), responseType);
    }

    private String getUrl(String region, Module module, String url) {
        if (module == Module.Keystone) {
            return keystoneEndpoint + url;
        } else {
            return endpoints.get(region + "-" + module) + url;
        }
    }

    private String fetchToken() {
        ExecuteContext context = ExecuteContext.getContext();
        String token = context.getToken();
        if (token != null) {
            return token;
        }

        ResponseEntity<Map> entity = requestForTokenEntity();
        token = entity.getHeaders().getFirst("X-Subject-Token");
        context.setToken(token);

        LOGGER.info("fetch token from openstack: " + token);

        return token;
    }

    private ResponseEntity<Map> requestForTokenEntity() {
        Authectication authectication = new Authectication();
        authectication.getIdentity().getPassword().getUser().getDomain().setName(domain);
        authectication.getIdentity().getPassword().getUser().setName(username);
        authectication.getIdentity().getPassword().getUser().setPassword(password);
        authectication.getScope().getProject().getDomain().setName(domain);
        authectication.getScope().getProject().setName(project);

        Map<String, Authectication> data = new HashMap<>();
        data.put("auth", authectication);

        return restTemplate.postForEntity(keystoneEndpoint + "/v3/auth/tokens", data, Map.class);
    }

    public void initEndpoints() {
        ResponseEntity<Map> entity = requestForTokenEntity();
        List<Map> catalogs = (List<Map>) MapUtils.get(entity.getBody(), "token.catalog");
        for (Map catalog : catalogs) {
            String service = (String) catalog.get("name");
            for (Map endpoint : (List<Map>) catalog.get("endpoints")) {
                if (StringUtils.equals("admin", (String) endpoint.get("interface"))) {
                    String region = (String) endpoint.get("region");
                    endpoints.put(region + "-" + service, (String) endpoint.get("url"));
                }
            }
        }
    }

    private class TokenSetClientRequestInterceptor implements ClientHttpRequestInterceptor {

        private String[] excludeUrls = new String[] {"/v3/auth/tokens"};

        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                        throws IOException {
            if (!isExcluded(request.getURI().toString())) {
                ExecuteContext context = ExecuteContext.getContext();
                String token = context.getToken();
                if (token == null) {
                    token = fetchToken();
                    ExecuteContext.getContext().setToken(token);
                }
                request.getHeaders().add("X-Auth-Token", token);
            }

            request.getHeaders().set("Content-Type", "application/json");
            request.getHeaders().set("Accept", "application/json");

            return execution.execute(request, body);
        }

        private boolean isExcluded(String url) {
            for (String excludeUrl : excludeUrls) {
                if (StringUtils.contains(url, excludeUrl)) {
                    return true;
                }
            }
            return false;
        }
    }
}
