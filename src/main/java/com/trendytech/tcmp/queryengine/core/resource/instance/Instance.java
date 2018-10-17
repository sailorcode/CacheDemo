package com.trendytech.tcmp.queryengine.core.resource.instance;

public class Instance {
    private String id;
    private String name;

    private String regionId;
    private String projectId;
    private String description;

    /**
     * 虚拟机在虚拟化平台中的ID
     */
    private String realId;

    /**
     * 虚拟机在虚拟化平台中的名称
     */
    private String realName;

    private int cpu;
    private int memory;
    private int totalDiskSize;

    /**
     * 操作系统名称
     */
    private String os;

    private String[] ips;

    //是否备份 1:是,0:否
    private int backupTag;

    private String taskState;
    private String vmState;
    private String powerState;
    private String status;

    private String imageId;
    private String launchedAt;
    private String createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getTotalDiskSize() {
        return totalDiskSize;
    }

    public void setTotalDiskSize(int totalDiskSize) {
        this.totalDiskSize = totalDiskSize;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String[] getIps() {
        return ips;
    }

    public void setIps(String[] ips) {
        this.ips = ips;
    }

    public int getBackupTag() {
        return backupTag;
    }

    public void setBackupTag(int backupTag) {
        this.backupTag = backupTag;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getVmState() {
        return vmState;
    }

    public void setVmState(String vmState) {
        this.vmState = vmState;
    }

    public String getPowerState() {
        return powerState;
    }

    public void setPowerState(String powerState) {
        this.powerState = powerState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getLaunchedAt() {
        return launchedAt;
    }

    public void setLaunchedAt(String launchedAt) {
        this.launchedAt = launchedAt;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
