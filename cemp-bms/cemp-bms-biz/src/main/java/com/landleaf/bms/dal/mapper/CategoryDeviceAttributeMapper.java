package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.CategoryDeviceAttributeEntity;
import com.landleaf.bms.domain.enums.FunctionTypeEnum;
import com.landleaf.bms.domain.request.CategoryFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 品类管理-设备属性Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:35
 */
public interface CategoryDeviceAttributeMapper extends ExtensionMapper<CategoryDeviceAttributeEntity> {

    /**
     * 查询目标品类的候选数据（为添加的系统可选功能、标准可选功能）
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceAttributeTabulationResponse> searchCandidateData(@Param("page") Page<?> page,
                                                                @Param("request") CategoryFeatureQueryRequest request);

    /**
     * 查询品类下功能列表
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceAttributeTabulationResponse> searchFunctionPage(@Param("page") Page<?> page,
                                                               @Param("request") CategoryFeatureQueryRequest request);

    /**
     * 功能是否存在
     *
     * @param id         功能ID
     * @param categoryId 品类ID
     * @return 结果
     */
    default boolean exists(Long id, Long categoryId) {
        return exists(Wrappers.<CategoryDeviceAttributeEntity>lambdaQuery()
                .eq(CategoryDeviceAttributeEntity::getId, id)
                .eq(CategoryDeviceAttributeEntity::getFunctionType, FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                .eq(CategoryDeviceAttributeEntity::getCategoryId, categoryId)
        );
    }

    @Select("select identifier from tb_category_management_device_attribute t1 where t1.deleted = 0 and t1.category_id = (select id from tb_category_management_category where deleted = 0 and biz_id = #{bizCategoryId}) and data_type in ('04','05')")
    List<String> getEnumAttrs(String bizCategoryId);
}
