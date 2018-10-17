package com.trendytech.tcmp.queryengine.core.platform.vsphere;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface VCServerMapper {
    public List<VCServer> listVCServers();

    public VCServer findVCServer(@Param("id") int id);
}
