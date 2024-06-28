package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.DeviceEventEntity;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceEventTabulationResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

import static com.landleaf.bms.domain.enums.FunctionTypeEnum.SYSTEM_DEFAULT;

/**
 * 设备事件Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:36
 */
public interface DeviceEventMapper extends BaseMapper<DeviceEventEntity> {

    /**
     * 标识符是否存在
     * @param identifier 标识符
     * @param id    id
     * @return  结果
     */
    default boolean existsIdentifier(String identifier, Long id) {
        return exists(Wrappers.<DeviceEventEntity>lambdaQuery()
                .eq(DeviceEventEntity::getIdentifier, identifier)
                .ne(Objects.nonNull(id), DeviceEventEntity::getId, id));
    }

    /**
     * 分页查询设备事件
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceEventTabulationResponse> searchDeviceEventTabulation(@Param(value = "page") Page<DeviceEventTabulationResponse> page,
                                                                    @Param(value = "request") FeatureQueryRequest request);

    /**
     * 获取系统默认功能类型的数据
     * @return  结果
     */
    default List<DeviceEventEntity> selectDefaultData() {
        return selectList(Wrappers.<DeviceEventEntity>lambdaQuery()
                .eq(DeviceEventEntity::getFunctionType, SYSTEM_DEFAULT.getValue())
        );
    }
}
