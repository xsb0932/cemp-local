package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.DeviceParameterEntity;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import com.landleaf.bms.domain.response.DeviceParameterTabulationResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

import static com.landleaf.bms.domain.enums.FunctionTypeEnum.SYSTEM_DEFAULT;

/**
 * 设备参数Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:36
 */
public interface DeviceParameterMapper extends BaseMapper<DeviceParameterEntity> {

    /**
     * 分页查询设备参数
     * @param page 分页
     * @param request   查询参数
     * @return  结果集
     */
    Page<DeviceParameterTabulationResponse> searchDeviceParameterTabulation(@Param("page") Page<DeviceAttributeTabulationResponse> page,
                                                                            @Param("request") FeatureQueryRequest request);

    /**
     * 标识符是否存在
     * @param identifier 标识符
     * @param id    id
     * @return  结果
     */
    default boolean existsIdentifier(String identifier, Long id) {
        return exists(Wrappers.<DeviceParameterEntity>lambdaQuery()
                .eq(DeviceParameterEntity::getIdentifier, identifier)
                .ne(Objects.nonNull(id), DeviceParameterEntity::getId, id));
    }

    /**
     * 获取系统默认功能类型的数据
     * @return  结果
     */
    default List<DeviceParameterEntity> selectDefaultData() {
        return selectList(Wrappers.<DeviceParameterEntity>lambdaQuery()
                .eq(DeviceParameterEntity::getFunctionType, SYSTEM_DEFAULT.getValue())
        );
    }

}
