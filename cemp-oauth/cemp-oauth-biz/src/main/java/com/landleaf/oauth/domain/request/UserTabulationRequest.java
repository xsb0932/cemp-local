package com.landleaf.oauth.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户列表查询参数
 *
 * @author yue lin
 * @since 2023/6/9 16:07
 */
@Data
@Schema(description = "用户列表查询参数")
public class UserTabulationRequest extends PageParam {

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String mobile;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String nickname;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

}
