package com.trendytech.tcmp.queryengine.core.platform.vsphere;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.trendytech.tcmp.queryengine.utils.IPValidateUtil;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.mo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.trendytech.tcmp.queryengine.cache.Cache;
import com.trendytech.tcmp.queryengine.core.resource.instance.Instance;
import com.trendytech.tcmp.queryengine.support.vsphere.ConnectionPool;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualEthernetCard;

@Component
public class VsphereCacheRefreshTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(VsphereCacheRefreshTasks.class);

    @Autowired
    private ConnectionPool pool;

    @Autowired
    private VCServerFetcher vcServerFetcher;

    @Resource(name = "instanceCache")
    private Cache<String, Instance> instanceCache;

    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 20 * 1000)
    public void cacheAllVMs() {
        for (VCServer vcServer : vcServerFetcher.fetchAll()) {
            ServerConnection connection = pool.getConnection(vcServer);
            try {
                ServiceInstance si = connection.getServiceInstance();
                InventoryNavigator inventoryNavigator = new InventoryNavigator(si.getRootFolder());
                ManagedEntity[] entities = inventoryNavigator.searchManagedEntities("VirtualMachine");
                for (ManagedEntity entity : entities) {
                    VirtualMachine virtualMachine = (VirtualMachine) entity;
                    if (!virtualMachine.getConfig().template) {
                        copyVirtualMachineProperties2Instance(virtualMachine);
                    }
                }
            } catch (RemoteException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                pool.releaseConnection(connection);
            }
        }
    }

    private void copyVirtualMachineProperties2Instance(VirtualMachine virtualMachine) {
        Instance instance = instanceCache.get(virtualMachine.getConfig().getInstanceUuid());
        if (instance != null) {
            instance.setRealId(virtualMachine.getConfig().getUuid());
            instance.setRealName(virtualMachine.getConfig().getName());
            instance.setOs(virtualMachine.getConfig().getGuestFullName());

            // 计算所有硬盘的总大小
            int totalDiskSize = 0;
            for (VirtualDevice virtualDevice : virtualMachine.getConfig().hardware.getDevice()) {
                if (virtualDevice instanceof VirtualDisk) {
                    VirtualDisk virtualDisk = (VirtualDisk) virtualDevice;
                    totalDiskSize += virtualDisk.getCapacityInKB() / 1024 / 1024;
                }
            }
            instance.setTotalDiskSize(totalDiskSize);

            //查询所有ip
            List<String> ipv4s = new ArrayList<String>();
            GuestNicInfo[] netInfos = virtualMachine.getGuest().getNet();
            if(netInfos != null){
                for(int i = 0;i < netInfos.length;i++){
                    String[] ips = netInfos[i].getIpAddress();
                    if(null != ips){
                        for(String ip:ips){
                            if(IPValidateUtil.isIPv4(ip)){
                                ipv4s.add(ip);
                            }
                        }
                    }
                }
            }
            //赋值到instance.ips
            instance.setIps(ipv4s.toArray(new String[ipv4s.size()]));
        }
    }
}
