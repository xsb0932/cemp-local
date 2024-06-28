package com.landleaf.oauth.api;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.oauth.api.dto.UserDTO;
import com.landleaf.oauth.api.dto.UserEmailDTO;
import com.landleaf.oauth.dal.mapper.UserEntityMapper;
import com.landleaf.oauth.dal.mapper.UserRoleEntityMapper;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@RestController
@RequiredArgsConstructor
public class UserRpcApiImpl implements UserRpcApi {
    private final UserEntityMapper userEntityMapper;
    private final UserRoleEntityMapper userRoleEntityMapper;

    @Override
    public Response<UserDTO> getUserInfo(Long userId) {
        UserEntity userEntity = userEntityMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, userId));
        if (null != userEntity) {
            UserDTO data = new UserDTO();
            data.setId(userEntity.getId())
                    .setUsername(userEntity.getUsername())
                    .setNickname(userEntity.getNickname());
            return Response.success(data);
        }
        return Response.success();
    }

    @Override
    public Response<List<UserDTO>> getUserInfoList(List<Long> userIds) {
        List<UserEntity> userEntities = userEntityMapper.selectList(new LambdaQueryWrapper<UserEntity>().in(UserEntity::getId, userIds));
        List<UserDTO> result = userEntities.stream()
                .map(o -> {
                    UserDTO data = new UserDTO();
                    data.setId(o.getId())
                            .setUsername(o.getUsername())
                            .setNickname(o.getNickname());
                    return data;
                }).collect(Collectors.toList());
        return Response.success(result);
    }

    @Override
    public Response<List<UserEmailDTO>> getUsersEmail(List<Long> userIds) {
        TenantContext.setIgnore(true);
        try {
            List<UserEntity> userEntities = userEntityMapper.selectList(new LambdaQueryWrapper<UserEntity>().in(UserEntity::getId, userIds));
            List<UserEmailDTO> result = userEntities.stream()
                    .map(o -> {
                        UserEmailDTO data = new UserEmailDTO();
                        data.setId(o.getId()).setEmail(o.getEmail());
                        return data;
                    }).collect(Collectors.toList());
            return Response.success(result);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<UserEmailDTO> getAdminUserEmail(Long tenantId) {
        TenantContext.setIgnore(true);
        UserEmailDTO dto = null;
        try {
            List<UserRoleEntity> userRoleEntityList = userRoleEntityMapper.getByTenantIdAndRoleType(tenantId, RoleTypeEnum.PLATFORM.getType());
            if (!CollectionUtils.isEmpty(userRoleEntityList)) {
                List<UserEntity> userEntities = userEntityMapper.selectList(new LambdaQueryWrapper<UserEntity>().in(UserEntity::getTenantId, userRoleEntityList.stream().map(UserRoleEntity::getUserId).collect(Collectors.toList())));
                if (!CollectionUtils.isEmpty(userEntities)) {
                    List<UserEmailDTO> result = userEntities.stream()
                            .map(o -> {
                                UserEmailDTO data = new UserEmailDTO();
                                data.setId(o.getId()).setEmail(o.getEmail());
                                return data;
                            }).collect(Collectors.toList());
                    dto = result.get(0);
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
        return Response.success(dto);
    }

    @Override
    public Response<Boolean> checkUserPassword(Long userId, String userPassword) {
        TenantContext.setIgnore(true);
        try {
            UserEntity user = userEntityMapper.selectById(userId);
            if (null == user) {
                return Response.success(Boolean.FALSE);
            }
            if (!SecureUtil.md5(user.getSalt() + userPassword).equals(user.getPassword())) {
                return Response.success(Boolean.FALSE);
            }
            return Response.success(Boolean.TRUE);
        } finally {
            TenantContext.setIgnore(false);
        }
    }
}
