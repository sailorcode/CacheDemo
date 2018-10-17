package com.trendytech.tcmp.queryengine.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polaris.base.exception.web.BadRequestException;
import com.trendytech.tcmp.queryengine.utils.Page;
import com.trendytech.tcmp.queryengine.utils.PageQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.polaris.base.exception.web.NotFoundException;
import com.trendytech.tcmp.queryengine.core.resource.instance.Instance;
import com.trendytech.tcmp.queryengine.core.resource.instance.InstanceFetcher;

@RestController
public class InstanceController {

    @Autowired
    private InstanceFetcher instanceFetcher;

    @GetMapping("/resource/instances")
    public PageQueryResult queryInstances(InstanceQueryParam pageCondition) {
        Map<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("regionId", pageCondition.getRegionId());
        conditions.put("projectId", pageCondition.getProjectId());
        //获得过滤后的虚拟机集合
        List<Instance> instancesFilterByCondition = instanceFetcher.filter(conditions);
        //分页
        int start = pageCondition.getStart();
        int size = pageCondition.getSize();
        List<Instance> instanceByPage = null;
        if (start >= instancesFilterByCondition.size()) {
            throw new BadRequestException("400", "查询超出范围", "查询超出范围");
        }
        if (start + size >= instancesFilterByCondition.size()) {
            instanceByPage = instancesFilterByCondition.subList(start, instancesFilterByCondition.size());
        } else {
            instanceByPage = instancesFilterByCondition.subList(start, start + size);
        }
        PageQueryResult pageQueryResult = new PageQueryResult();
        pageQueryResult.setRecords(instanceByPage);
        pageQueryResult.setTotal(instancesFilterByCondition.size());
        pageQueryResult.setPage(pageCondition.getPage());
        pageQueryResult.setSize(pageCondition.getSize());
        return pageQueryResult;
    }

    @GetMapping("/resource/instance/{id}")
    public Instance queryInstances(@PathVariable String id) {
        Instance instance = instanceFetcher.fetch(id);
        if (instance == null) {
            throw new NotFoundException("404", "Instance not found", "Instance not found");
        }
        return instance;
    }
}
