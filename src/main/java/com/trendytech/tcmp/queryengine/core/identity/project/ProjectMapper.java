package com.trendytech.tcmp.queryengine.core.identity.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ProjectMapper {
    public List<Project> queryProjects(@Param("region") String region);
}
