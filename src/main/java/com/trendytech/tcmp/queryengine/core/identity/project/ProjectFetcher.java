package com.trendytech.tcmp.queryengine.core.identity.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trendytech.tcmp.queryengine.core.Fetcher;
import com.trendytech.tcmp.queryengine.support.openstack.Module;
import com.trendytech.tcmp.queryengine.support.openstack.OpenstackRestExecutor;

@Service
public class ProjectFetcher implements Fetcher<String, Project> {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private OpenstackRestExecutor excutor;

    @Override
    public Project fetch(String name) {
        for (Project project : fetchAll()) {
            if (StringUtils.equals(name, project.getId())) {
                return project;
            }
        }
        return null;
    }

    @Override
    public List<Project> fetchAll() {
        String region = null;
        return filter(region);
    }

    /**
     * support conditions: region
     */
    public List<Project> filter(Map<String, Object> conditions) {
        String region = (String) conditions.get("region");
        return filter(region);
    }

    private List<Project> filter(String region) {
        Map dataMap = excutor.get(null, Module.Keystone, "/v3/projects", Map.class);
        Map<String, Map<String, Object>> projects = new HashMap<>();
        for (Map<String, Object> projectMap : (List<Map<String, Object>>) dataMap.get("projects")) {
            projects.put(String.valueOf(projectMap.get("id")), projectMap);
        }

        List<Project> projectList = projectMapper.queryProjects(region);
        for (Project porject : projectList) {
            Map<String, Object> projectMap = projects.get(porject.getId());
            if (projectMap != null && projectMap.containsKey("description")) {
                porject.setDescription(String.valueOf(projectMap.get("description")));
            }
        }
        return projectList;
    }
}
