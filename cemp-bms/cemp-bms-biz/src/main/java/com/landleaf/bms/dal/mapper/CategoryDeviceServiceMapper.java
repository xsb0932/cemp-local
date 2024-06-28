package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.CategoryDeviceServiceEntity;
import com.landleaf.bms.domain.enums.FunctionTypeEnum;
import com.landleaf.bms.domain.request.CategoryFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceServiceTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品类管理-设备服务Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:37
 */
public interface CategoryDeviceServiceMapper extends ExtensionMapper<CategoryDeviceServiceEntity> {

    /**
     * 查询目标品类的候选数据（为添加的系统可选功能、标准可选功能）
     *
     * @param page 分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceServiceTabulationResponse> searchCandidateData(@Param("page") Page<?> page,
                                                              @Param("request") CategoryFeatureQueryRequest request);

    /**
     * 查询品类下功能列表
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceServiceTabulationResponse> searchFunctionPage(@Param("page") Page<?> page,
                                                             @Param("request") CategoryFeatureQueryRequest request);

    /**
     * 功能是否存在
     * @param id    功能ID
     * @param categoryId    品类ID
     * @return  结果
     */
    default boolean exists(Long id, Long categoryId) {
        return exists(Wrappers.<CategoryDeviceServiceEntity>lambdaQuery()
                .eq(CategoryDeviceServiceEntity::getId, id)
                .eq(CategoryDeviceServiceEntity::getFunctionType, FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                .eq(CategoryDeviceServiceEntity::getCategoryId, categoryId)
        );
    }

}
