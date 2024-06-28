package com.landleaf.oauth.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.api.dto.StaTenantDTO;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.oauth.dal.mapper.TenantEntityMapper;
import com.landleaf.oauth.domain.entity.TenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.CommonConstant.TENANT_ADMIN_CODE;

/**
 * Feign 服务 - 租户
 *
 * @author 张力方
 * @since 2023/6/13
 **/
@RestController
@RequiredArgsConstructor
public class TenantApiImpl implements TenantApi {
    private final TenantEntityMapper tenantEntityMapper;

    /**
     * 获取租户信息
     *
     * @param tenantId 租户id
     */
    @Override
    public Response<TenantInfoResponse> getTenantInfo(Long tenantId) {
        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
        TenantInfoResponse tenantInfoResponse = new TenantInfoResponse();
        BeanUtils.copyProperties(tenantEntity, tenantInfoResponse);
        return Response.success(tenantInfoResponse);
    }

    @Override
    public Response<List<StaTenantDTO>> listStaJobTenant() {
        return Response.success(
                tenantEntityMapper.selectList(new LambdaQueryWrapper<TenantEntity>().ne(TenantEntity::getCode, TENANT_ADMIN_CODE))
                        .stream()
                        .map(o -> {
                            StaTenantDTO dto = new StaTenantDTO();
                            BeanUtil.copyProperties(o, dto);
                            return dto;
                        }).collect(Collectors.toList())
        );
    }

    @Override
    public Response<Boolean> tenantIsAdmin(Long tenantId) {
        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
        if (null != tenantEntity) {
            return Response.success(StrUtil.equals(tenantEntity.getCode(), TENANT_ADMIN_CODE));
        }
        return Response.success(false);
    }

    @Override
    public Response<Long> getTenantAdmin() {
        TenantEntity tenantEntity = tenantEntityMapper.selectOne(new LambdaQueryWrapper<TenantEntity>().eq(TenantEntity::getCode, TENANT_ADMIN_CODE));
        if (null != tenantEntity) {
            return Response.success(tenantEntity.getId());
        }
        return Response.success(null);
    }

    @Override
    public Response<Long> getTenantIdByCode(String code) {
        TenantEntity tenantEntity = tenantEntityMapper.selectOne(new LambdaQueryWrapper<TenantEntity>().eq(TenantEntity::getCode, code));
        if (null != tenantEntity) {
            return Response.success(tenantEntity.getId());
        }
        return Response.success(null);
    }
}
