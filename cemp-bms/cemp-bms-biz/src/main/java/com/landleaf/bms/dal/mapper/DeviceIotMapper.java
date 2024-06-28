package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.dto.GatewayDeviceIdRelationDTO;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.bms.domain.response.DeviceManagerDetailResponse;
import com.landleaf.bms.domain.response.DeviceManagerPageResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * 设备-监测平台的数据库操作句柄
 *
 * @author hebin
 * @since 2023-07-12
 */
public interface DeviceIotMapper extends BaseMapper<DeviceIotEntity> {

    @Select("select * from tb_project where deleted = 0 and name = #{projectName} limit 1")
    ProjectEntity getProject(@Param("projectName") String projectName);

    @Select("select * from tb_product where deleted = 0 and name = #{productName} limit 1")
    ProductEntity getProduct(@Param("productName") String productName);

    @Select("select * from tb_project_space where deleted = 0 and name = #{spaceName} and project_id = #{projectId} limit 1")
    ProjectSpaceEntity getSpace(@Param("spaceName") String spaceName, @Param("projectId") Long projectId);

    default boolean existsSpace(Long spaceId) {
        return exists(Wrappers.<DeviceIotEntity>lambdaQuery().eq(DeviceIotEntity::getBizAreaId, String.valueOf(spaceId)));
    }

    ;

    @Select("select biz_device_id from tb_device_iot where deleted = 0 and biz_product_id= #{pkId} and source_device_id= #{sourceDevId} limit 1")
    String queryBizDeviceIdByOuterId(@Param("gateId") String gateId, @Param("pkId") String pkId, @Param("sourceDevId") String sourceDevId);

    List<GatewayDeviceIdRelationDTO> listGatewayDeviceIdRelationByDeviceId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    Page<DeviceManagerPageResponse> deviceManagePageQuery(
            @Param("page") Page<DeviceManagerPageResponse> page,
            @Param("projectBizIds") Collection<String> projectBizIds,
            @Param("areaIds") Collection<Long> areaIds,
            @Param("categoryIds") Collection<String> categoryIds,
            @Param("bizProductId") String bizProductId,
            @Param("name") String name,
            @Param("bizDeviceIds") List<String> bizDeviceIds
    );

    List<String> selectAllBizDeviceIdWhenPage(
            @Param("projectBizIds") Collection<String> projectBizIds,
            @Param("areaIds") Collection<Long> areaIds,
            @Param("categoryIds") Collection<String> categoryIds,
            @Param("bizProductId") String bizProductId,
            @Param("name") String name
    );

    DeviceManagerDetailResponse detail(@Param("bizDeviceId") String bizDeviceId);
}
