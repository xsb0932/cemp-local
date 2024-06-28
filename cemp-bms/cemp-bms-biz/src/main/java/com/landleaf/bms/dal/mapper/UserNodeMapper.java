package com.landleaf.bms.dal.mapper;

import com.landleaf.bms.api.dto.UserProjRelationResponse;
import com.landleaf.bms.domain.entity.UserNodeEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserNodeMapper
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Mapper
public interface UserNodeMapper extends ExtensionMapper<UserNodeEntity> {
    /**
     * 获取用户和proj的对用关系
     *
     * @param tenantId
     * @return
     */
    List<UserProjRelationResponse> getUserProjRelation(@Param("tenantId") Long tenantId);
}
