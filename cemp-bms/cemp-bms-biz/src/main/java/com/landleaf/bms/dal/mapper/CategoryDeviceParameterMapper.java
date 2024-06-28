package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.CategoryDeviceParameterEntity;
import com.landleaf.bms.domain.enums.FunctionTypeEnum;
import com.landleaf.bms.domain.request.CategoryFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceParameterTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品类管理-设备参数Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:36
 */
public interface CategoryDeviceParameterMapper extends ExtensionMapper<CategoryDeviceParameterEntity> {

    /**
     * 查询目标品类的候选数据（为添加的系统可选功能、标准可选功能）
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceParameterTabulationResponse> searchCandidateData(@Param("page") Page<?> page,
                                                                @Param("request") CategoryFeatureQueryRequest request);

    /**
     * 查询品类下功能列表
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceParameterTabulationResponse> searchFunctionPage(@Param("page") Page<?> page,
                                                               @Param("request") CategoryFeatureQueryRequest request);


    /**
     * 功能是否存在
     *
     * @param id         功能ID
     * @param categoryId 品类ID
     * @return 结果
     */
    default boolean exists(Long id, Long categoryId) {
        return exists(Wrappers.<CategoryDeviceParameterEntity>lambdaQuery()
                .eq(CategoryDeviceParameterEntity::getId, id)
                .eq(CategoryDeviceParameterEntity::getFunctionType, FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                .eq(CategoryDeviceParameterEntity::getCategoryId, categoryId)
        );
    }

    /**
     * 根据品类id查询设备参数列表
     *
     * @param categoryId 品类ID
     * @return 结果
     */
    default List<CategoryDeviceParameterEntity> selectListByBizId(@NotNull Long categoryId) {
        return selectList(Wrappers.<CategoryDeviceParameterEntity>lambdaQuery().eq(CategoryDeviceParameterEntity::getCategoryId, categoryId)
        );
    }

}
