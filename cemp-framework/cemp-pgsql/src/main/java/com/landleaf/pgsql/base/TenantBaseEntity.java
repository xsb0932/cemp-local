package com.landleaf.pgsql.base;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户通用实体
 *
 * @author yue lin
 * @since 2023/6/1 9:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantBaseEntity extends BaseEntity {

    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    @Schema(description = "租户id")
    private Long tenantId;

}
