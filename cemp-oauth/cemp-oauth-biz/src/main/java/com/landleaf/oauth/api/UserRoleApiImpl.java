package com.landleaf.oauth.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.dal.mapper.RoleEntityMapper;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Feign 服务 - 用户角色
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@RestController
@RequiredArgsConstructor
public class UserRoleApiImpl implements UserRoleApi {
    private final RoleEntityMapper roleEntityMapper;

    @Override
    public Response<Boolean> isPlatformAdmin(Long userId) {
        List<UserRoleEntity> userRoleByRoleType = roleEntityMapper.getUserRoleByRoleType(RoleTypeEnum.PLATFORM.getType(), userId);
        return Response.success(!CollectionUtils.isEmpty(userRoleByRoleType));
    }

    @Override
    public Response<Boolean> isTenantAdmin(Long userId) {
        List<UserRoleEntity> userRoleByRoleType = roleEntityMapper.getUserRoleByRoleType(RoleTypeEnum.TENANT.getType(), userId);
        return Response.success(!CollectionUtils.isEmpty(userRoleByRoleType));
    }
}
