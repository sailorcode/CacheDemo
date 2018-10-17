package com.trendytech.tcmp.queryengine.core.resource.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.trendytech.tcmp.queryengine.cache.Cache;
import com.trendytech.tcmp.queryengine.core.Fetcher;
import org.springframework.util.CollectionUtils;

@Service
public class InstanceFetcher implements Fetcher<String, Instance> {

    @Resource(name = "instanceCache")
    private Cache<String, Instance> cache;

    @Override
    public Instance fetch(String key) {
        return cache.get(key);
    }

    @Override
    public List<Instance> fetchAll() {
        return cache.getAll();
    }

    @Override
    public List<Instance> filter(Map<String, Object> conditions) {
        final String regionId = (String) conditions.get("regionId");
        final String projectId = (String) conditions.get("projectId");

        List<Instance> instances = cache.getAll();
        if (CollectionUtils.isEmpty(instances)) {
            return new ArrayList<Instance>();
        }
        //过滤不符合条件虚机
        List<Instance> instancesFilterByCondition = filterInstances(instances, regionId, projectId);
        return instancesFilterByCondition == null ? new ArrayList<Instance>() : instancesFilterByCondition;
    }

    /**
     * 过滤不符合条件虚机
     * @param instanceList
     * @param regionId
     * @param projectId
     * @return
     */
    public List<Instance> filterInstances(List<Instance> instanceList, String regionId , String projectId){
        List<Instance> instancesFilterByCondition = new ArrayList<Instance>();
        if (StringUtils.isEmpty(regionId) && StringUtils.isEmpty(projectId)) {
            instancesFilterByCondition.addAll(instanceList);
        } else {
            instancesFilterByCondition.addAll(instanceList.stream()
                    .filter(inst -> this.checkInstance(inst, regionId, projectId)).collect(Collectors.toList()));
        }
        return instancesFilterByCondition;
    }

    /**
     * 判断虚拟机是否符合条件
     * @param instance
     * @param regionId
     * @param projectId
     * @return
     */
    private boolean checkInstance(Instance instance , String regionId , String projectId) {
        boolean region = false;
        boolean project = false;
        if (StringUtils.isEmpty(regionId) || StringUtils.equals(instance.getRegionId(), regionId)) {
            region = true;
        }
        if (StringUtils.isEmpty(projectId) || StringUtils.equals(instance.getProjectId(), projectId)) {
            project = true;
        }
        return region && project;
    }

}
