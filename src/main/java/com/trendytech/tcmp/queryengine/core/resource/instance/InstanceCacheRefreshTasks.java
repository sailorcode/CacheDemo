package com.trendytech.tcmp.queryengine.core.resource.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.trendytech.tcmp.queryengine.cache.Cache;
import com.trendytech.tcmp.queryengine.core.identity.region.Region;
import com.trendytech.tcmp.queryengine.core.identity.region.RegionFetcher;
import com.trendytech.tcmp.queryengine.support.openstack.Module;
import com.trendytech.tcmp.queryengine.support.openstack.OpenstackRestExecutor;
import com.trendytech.tcmp.queryengine.utils.MapUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class InstanceCacheRefreshTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCacheRefreshTasks.class);

    private static final String[] PROPERTIES_FROM_OPENSTACK_SERVER = {"id", "name", "regionId", "projectId",
                    "description", "status", "vmState", "powerState", "taskState"};

    private static final String[] PROPERTIES_FROM_OPENSTACK_FLAVOR = {"cpu", "memory"};

    private static final String[] PROPERTIES_FROM_VSPHERE = {"totalDiskSize", "os"};

    @Resource(name = "instanceCache")
    private Cache<String, Instance> cache;

    @Autowired
    private RegionFetcher regionFetcher;

    @Autowired
    private OpenstackRestExecutor executor;

    @Value("${cloudos.backup.endpoint}")
    private String cloudosBackupUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private Map<String, Flavor> flavorsMap = new ConcurrentHashMap<String, Flavor>();

    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void cacheOpenstackInstances() {
        // 遍历区域查询虚拟机并加入map中
        ConcurrentHashMap<String, Instance> instancesMap = new ConcurrentHashMap<String, Instance>();
        for (Region region : regionFetcher.fetchAll()) {
            // 捕获异常避免因单个区域连接失败时导致后续处理失败数据异常导致的缓存任务失败
            try {
                for (Instance instance : listAllInstanceOfRegion(region.getId())) {
                    Instance instanceInCache = cache.get(instance.getId());
                    if (instanceInCache != null) {
                        BeanUtils.copyProperties(instanceInCache, instance, PROPERTIES_FROM_OPENSTACK_SERVER);
                    }
                    instancesMap.put(instance.getId(), instance);
                }
            } catch (BeansException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        cache.resetNativeStore(instancesMap);
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void cacheOpenstackFlavors() {
        for (Region region : regionFetcher.fetchAll()) {
            LOGGER.info("fetch flavors from openstack: " + region.getId());
            // 捕获异常避免因单个区域连接失败时导致后续处理失败数据异常导致的缓存任务失败
            try {
                Map responseData = executor.get(region.getId(), Module.Nova, "/flavors/detail", Map.class);

                LOGGER.debug(String.valueOf(responseData));
                List<Instance> instances = new ArrayList<>();
                for (Map flavorMap : (List<Map>) responseData.get("flavors")) {
                    Flavor flavor = new Flavor();
                    flavor.setId(String.valueOf(flavorMap.get("id")));
                    flavor.setCpu((int) flavorMap.get("vcpus"));
                    flavor.setMemory((int) flavorMap.get("vcpus"));
                    flavorsMap.put(flavor.getId(), flavor);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 标记是否备份,1:是,0:否
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 30 * 1000)
    public void markInstanceBackupTag() {
        //缓存中虚拟机
        List<Instance> instances = cache.getAll();
        //获取已备份虚拟机名字集合
        List<String> backupedInstances = new ArrayList<String>();
        for (Region region : regionFetcher.fetchAll()) {
            try {
                List<Map<String, Object>> policyDetails = getPolicys(region.getId());
                for (Map<String, Object> policyMap : policyDetails) {
                    List<Map<String, Object>> servers = (List<Map<String, Object>>) policyMap.get("server");
                    if (!CollectionUtils.isEmpty(servers)) {
                        for (Map<String, Object> serverMap : servers) {
                            backupedInstances.add((String) serverMap.get("serverName"));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("markBackupTag异常 regionId=" + region.getId() + e.getMessage(),e);
            }
        }
        boolean flag = false;
        for (Instance instance : instances) {
            flag = false;
            for (String serverName : backupedInstances) {
                if (StringUtils.equals(instance.getRealName(), serverName)) {
                    //该虚拟机已备份,设置属性
                    instance.setBackupTag(1);
                    flag = true;
                    break;
                }
            }
            //如果标志位还是false,则未备份
            if (!flag) {
                instance.setBackupTag(0);
            }
        }
    }

    private Flavor getFlavor(String region, String id) {
        Flavor flavor = flavorsMap.get(id);
        if (flavor == null) {
            flavor = new Flavor();
            LOGGER.info("fetch flavor from openstack: " + id);
            Map responseData = executor.get(region, Module.Nova, "/flavors/" + id, Map.class);

            LOGGER.debug(String.valueOf(responseData));

            Map flavorMap = (Map) responseData.get("flavor");
            flavor.setId(String.valueOf(flavorMap.get("id")));
            flavor.setCpu((int) flavorMap.get("vcpus"));
            flavor.setMemory((int) flavorMap.get("vcpus"));
            flavorsMap.put(flavor.getId(), flavor);
        }
        return flavor;
    }

    private List<Instance> listAllInstanceOfRegion(String region) {
        LOGGER.info("fetch instances from openstack: " + region);
        Map responseData = executor.get(region, Module.Nova, "/servers/detail?all_tenants=1", Map.class);

        LOGGER.debug(String.valueOf(responseData));
        List<Instance> instances = new ArrayList<>();
        for (Map server : (List<Map>) responseData.get("servers")) {
            Instance instance = null;
            // 捕获异常避免因特殊虚拟机数据异常导致的缓存任务失败
            try {
                instance = convertOpenstackServer2Instance(region, server);
                instances.add(instance);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return instances;
    }

    private Instance convertOpenstackServer2Instance(String region, Map server) {
        Instance instance = new Instance();
        instance.setId((String) server.get("id"));
        instance.setRegionId(region);
        instance.setProjectId((String) server.get("tenant_id"));
        instance.setName((String) server.get("name"));
        instance.setDescription((String) MapUtils.get(server, "metadata.description"));
        instance.setStatus((String) server.get("status"));
        instance.setTaskState((String) server.get("OS-EXT-STS:task_state"));
        instance.setVmState((String) server.get("OS-EXT-STS:vm_state"));
        instance.setPowerState(String.valueOf(server.get("OS-EXT-STS:power_state")));

        instance.setLaunchedAt(String.valueOf(server.get("OS-SRV-USG:launched_at")));
        instance.setCreateAt(String.valueOf(server.get("created")));
        instance.setImageId(String.valueOf(MapUtils.get(server,"image.id")));

        Flavor flavor = null;
        try {
            flavor = getFlavor(region, (String) MapUtils.get(server, "flavor.id"));
            BeanUtils.copyProperties(flavor, instance, "id");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return instance;
    }

    public List<Map<String,Object>> getPolicys(String regionId){
        //设置返回类型
        ParameterizedTypeReference<List<Map<String, Object>>> typeRef = new ParameterizedTypeReference<List<Map<String, Object>>>() {
        };
        //设置header
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("regionId", regionId);
        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(cloudosBackupUrl + "/backup/policys", HttpMethod.GET, httpEntity, typeRef);

        List<Map<String, Object>> result = responseEntity.getBody();

        return CollectionUtils.isEmpty(result) ? new ArrayList<>() : result;
    }
}
