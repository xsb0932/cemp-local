package com.landleaf.bms.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.CategoryDeviceAttributeResponse;
import com.landleaf.bms.api.dto.CategoryDeviceParameterResponse;
import com.landleaf.bms.api.dto.ProjectBizCategoryResponse;
import com.landleaf.bms.constance.ValueConstance;
import com.landleaf.bms.dal.mapper.CategoryDeviceAttributeMapper;
import com.landleaf.bms.dal.mapper.CategoryDeviceParameterMapper;
import com.landleaf.bms.dal.mapper.CategoryMapper;
import com.landleaf.bms.domain.entity.CategoryDeviceAttributeEntity;
import com.landleaf.bms.domain.entity.CategoryEntity;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.CATEGORY_NOT_EXIST;

/**
 * CategoryApiImpl
 *
 * @author 张力方
 * @since 2023/7/20
 **/
@RestController
@RequiredArgsConstructor
public class CategoryApiImpl implements CategoryApi {
    private final CategoryMapper categoryMapper;
    private final CategoryDeviceParameterMapper categoryDeviceParameterMapper;
    private final CategoryDeviceAttributeMapper categoryDeviceAttributeMapper;

    @Override
    public Response<String> getBizCategoryId(String code) {
        CategoryEntity categoryEntity = categoryMapper.selectOne(Wrappers.<CategoryEntity>lambdaQuery()
                .eq(CategoryEntity::getCode, code));
        return Response.success(categoryEntity.getBizId());
    }

    @Override
    public Response<List<String>> getBizCategoryIdList(List<String> codes) {
        List<String> result = categoryMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().in(CategoryEntity::getCode, codes))
                .stream().map(CategoryEntity::getBizId).toList();
        return Response.success(result);
    }

    @Override
    public Response<List<ProjectBizCategoryResponse>> searchCategoryByBizId(List<String> bizIds) {
        List<ProjectBizCategoryResponse> responses = categoryMapper.selectBatchByBizIds(bizIds)
                .stream()
                .map(it -> {
                    ProjectBizCategoryResponse response = new ProjectBizCategoryResponse();
                    response.setCategoryId(it.getBizId());
                    response.setCategoryName(it.getName());
                    response.setCategoryCode(it.getCode());
                    return response;
                }).toList();
        return Response.success(responses);
    }

    @Override
    public Response<List<CategoryDeviceParameterResponse>> searchDeviceParameterByBizId(String bizId) {
        TenantContext.setIgnore(true);
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(bizId);
        if (Objects.nonNull(categoryEntity)) {
            List<CategoryDeviceParameterResponse> responseList = categoryDeviceParameterMapper.selectListByBizId(categoryEntity.getId())
                    .stream()
                    .map(it -> {
                        CategoryDeviceParameterResponse response = new CategoryDeviceParameterResponse();
                        response.setIdentifier(it.getIdentifier());
                        response.setFunctionName(it.getFunctionName());
                        if (CollUtil.isNotEmpty(it.getValueDescription())) {
                            it.getValueDescription()
                                    .stream()
                                    .filter(kv -> StrUtil.equals(kv.getKey(), ValueConstance.UNIT_KEY) && StrUtil.isNotBlank(kv.getValue()))
                                    .findAny()
                                    .ifPresent(kv -> response.setFunctionName(response.getFunctionName() + "(" + kv.getValue() + ")"));
                        }
                        return response;
                    }).toList();
            return Response.success(responseList);
        }
        return Response.error(CATEGORY_NOT_EXIST);
    }

    @Override
    public Response<List<CategoryDeviceAttributeResponse>> searchDeviceAttributeByBizId(String bizId) {
        TenantContext.setIgnore(true);
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(bizId);
        if (Objects.nonNull(categoryEntity)) {
            List<CategoryDeviceAttributeResponse> responseList = categoryDeviceAttributeMapper.selectList(new LambdaQueryWrapper<CategoryDeviceAttributeEntity>().eq(CategoryDeviceAttributeEntity::getCategoryId, categoryEntity.getId()))
                    .stream()
                    .map(it -> {
                        CategoryDeviceAttributeResponse response = new CategoryDeviceAttributeResponse();
                        response.setIdentifier(it.getIdentifier());
                        response.setFunctionName(it.getFunctionName());
                        if (CollUtil.isNotEmpty(it.getValueDescription())) {
                            it.getValueDescription()
                                    .stream()
                                    .filter(kv -> StrUtil.equals(kv.getKey(), ValueConstance.UNIT_KEY) && StrUtil.isNotBlank(kv.getValue()))
                                    .findAny()
                                    .ifPresent(kv -> response.setFunctionName(response.getFunctionName() + "(" + kv.getValue() + ")"));
                        }
                        return response;
                    }).toList();
            return Response.success(responseList);
        }
        return Response.error(CATEGORY_NOT_EXIST);
    }
}
