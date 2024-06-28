package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.DeviceAttributeEntity;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

import static com.landleaf.bms.domain.enums.FunctionTypeEnum.SYSTEM_DEFAULT;

/**
 * 设备属性Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:35
 */
public interface DeviceAttributeMapper extends BaseMapper<DeviceAttributeEntity>  {

    /**
     * 分页查询设备属性
     * @param page 分页
     * @param request   查询参数
     * @return  结果集
     */
    Page<DeviceAttributeTabulationResponse> searchDeviceAttributeTabulation(@Param("page") Page<DeviceAttributeTabulationResponse> page,
                                                                            @Param("request") FeatureQueryRequest request);


    /**
     * 标识符是否存在
     * @param identifier 标识符
     * @param id    id
     * @return  结果
     */
    default boolean existsIdentifier(String identifier, Long id) {
        return exists(Wrappers.<DeviceAttributeEntity>lambdaQuery()
                .eq(DeviceAttributeEntity::getIdentifier, identifier)
                .ne(Objects.nonNull(id), DeviceAttributeEntity::getId, id));
    }

    /**
     * 获取系统默认功能类型的数据
     * @return  结果
     */
    default List<DeviceAttributeEntity> selectDefaultData() {
        return selectList(Wrappers.<DeviceAttributeEntity>lambdaQuery()
                .eq(DeviceAttributeEntity::getFunctionType, SYSTEM_DEFAULT.getValue())
        );
    }

}
