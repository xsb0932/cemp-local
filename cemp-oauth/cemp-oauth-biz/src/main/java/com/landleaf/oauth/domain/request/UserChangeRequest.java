package com.landleaf.oauth.domain.request;

import cn.hutool.core.util.RandomUtil;
import com.landleaf.comm.constance.PatternConstant;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.web.validation.Create;
import com.landleaf.web.validation.Update;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 用户变更参数
 *
 * @author yue lin
 * @since 2023/6/9 16:35
 */
@Data
public class UserChangeRequest {

    /**
     * 用户ID
     * <p>
     * 创建为空， 变更则必填
     */
    @NotNull(groups = Update.class, message = "用户主键不能为空")
    @Null(groups = Create.class, message = "用户主键必须为空")
    private Long id;

    /**
     * 租户ID
     * <p>
     * 创建时， 不传则默认登陆账号所在的租户
     * 变更必须为空
     */
    private Long tenantId;

    /**
     * 用户账号
     */
    @Size(min = 1, max = 30, message = "用户账号长度区间{min}-{max}")
    @NotBlank(message = "用户账号不能为空")
    private String username;

    /**
     * 用户名
     */
    @Size(min = 1, max = 30, message = "用户名长度区间{min}-{max}")
    @NotBlank(message = "用户名不能为空")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误")
    @Size(max = 50, message = "邮箱长度不能超过{max}")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 电话
     */
    @Pattern(regexp = PatternConstant.PHONE_PATTERN, message = "手机号码格式错误")
    @NotBlank(message = "电话不能为空")
    private String mobile;

    /**
     * 用户角色
     */
    @NotEmpty(message = "角色不能为空")
    private List<Long> roleIds;

    /**
     * 用户项目权限
     */
    private List<Long> nodeIds;

    /**
     * 权限类型（1区域 2项目）
     */
    @NotNull(message = "权限类型不能为空")
    private Short nodeType;

    public UserEntity toEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(this.id);
        userEntity.setUsername(this.username);
        userEntity.setNickname(this.nickname);
        userEntity.setEmail(this.email);
        userEntity.setMobile(this.mobile);
        if (Objects.isNull(this.tenantId)) {
            userEntity.setTenantId(TenantContext.getTenantId());
        } else {
            userEntity.setTenantId(this.tenantId);
        }
        userEntity.setSalt(RandomUtil.randomString(15));
        return userEntity;
    }

}
