package com.swapServer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swapServer.bean.Organization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    List<Organization> findAllOrganization();
    Optional<Organization> findOrganizationById(long id);
}
