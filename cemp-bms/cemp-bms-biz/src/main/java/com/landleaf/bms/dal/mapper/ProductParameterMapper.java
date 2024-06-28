package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductParameterEntity;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.ProductParameterTabulationResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

import static com.landleaf.bms.domain.enums.FunctionTypeEnum.SYSTEM_DEFAULT;

/**
 * 产品参数Mapper
 *
 * @author yue lin
 * @since 2023/6/25 11:38
 */
public interface ProductParameterMapper extends BaseMapper<ProductParameterEntity> {

    /**
     * 分页查询产品参数
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<ProductParameterTabulationResponse> searchProductParameterTabulation(@Param("page") Page<ProductParameterTabulationResponse> page,
                                                            @Param("request") FeatureQueryRequest request);

    /**
     * 标识符是否存在
     * @param identifier 标识符
     * @param id    id
     * @return  结果
     */
    default boolean existsIdentifier(String identifier, Long id) {
        return exists(Wrappers.<ProductParameterEntity>lambdaQuery()
                .eq(ProductParameterEntity::getIdentifier, identifier)
                .ne(Objects.nonNull(id), ProductParameterEntity::getId, id));
    }

    /**
     * 获取系统默认功能类型的数据
     * @return  结果
     */
    default List<ProductParameterEntity> selectDefaultData() {
        return selectList(Wrappers.<ProductParameterEntity>lambdaQuery()
                .eq(ProductParameterEntity::getFunctionType, SYSTEM_DEFAULT.getValue())
        );
    }

}
