package com.trendytech.tcmp.queryengine.api.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trendytech.tcmp.queryengine.core.identity.project.Project;
import com.trendytech.tcmp.queryengine.core.identity.project.ProjectFetcher;

@RestController
public class ProjectController {

    @Autowired
    private ProjectFetcher projectFetcher;

    @GetMapping("/identity/projects")
    public List<Project> queryProjects(String region) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("region", region);
        return projectFetcher.filter(conditions);
    }
}
