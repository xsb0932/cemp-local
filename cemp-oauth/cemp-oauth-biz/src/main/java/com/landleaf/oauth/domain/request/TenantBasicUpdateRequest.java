package com.landleaf.oauth.domain.request;

import cn.hutool.core.text.CharSequenceUtil;
import com.landleaf.oauth.domain.entity.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.List;

/**
 * 租户基础信息变更
 *
 * @author yue lin
 * @since 2023/6/1 16:57
 */
@Data
@Schema(name = "租户基础信息变更", description = "租户基础信息变更")
public class TenantBasicUpdateRequest {

    /**
     * id
     */
    @NotNull(message = "租户主键不能为空")
    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * 租户企业名称
     */
    @NotBlank(message = "租户企业名称不能为空")
    @Size(min = 1, max = 30, message = "租户企业名称长度区间{min}-{max}")
    @Schema(description = "租户企业名称", example = "租户企业名称")
    private String name;

    /**
     * 营业执照
     */
    @NotBlank(message = "营业执照不能为空")
    @Schema(description = "营业执照", example = "营业执照")
    private String businessLicense;

    /**
     * logo
     */
    @NotBlank(message = "Logo不能为空")
    @Schema(description = "logo", example = "logo")
    private String logo;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    @Size(min = 1, max = 100, message = "地址长度区间{min}-{max}")
    @Schema(description = "地址", example = "山东省日照市市辖区东张乡")
    private String address;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 需要变更的菜单权限
     * 菜单唯一标识符
     */
    @NotEmpty(message = "菜单权限不能为空")
    @Schema(description = "需要变更的菜单权限")
    private List<String> permissions;

    /**
     * 月报周期
     */
    @NotNull(message = "月报周期不能为null")
    @Schema(description = "月报周期", example = "0")
    private String reportingCycle;


    public TenantEntity toTenantEntity() {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setId(this.id);
        if (CharSequenceUtil.isNotBlank(this.name)) {
            tenantEntity.setName(this.name);
        }
        if (CharSequenceUtil.isNotBlank(this.businessLicense)) {
            tenantEntity.setBusinessLicense(this.businessLicense);
        }
        if (CharSequenceUtil.isNotBlank(this.logo)) {
            tenantEntity.setLogo(this.logo);
        }
        if (CharSequenceUtil.isNotBlank(this.address)) {
            tenantEntity.setAddress(this.address);
        }
        if (CharSequenceUtil.isNotBlank(this.remark)) {
            tenantEntity.setRemark(this.remark);
        }
        if (null != reportingCycle) {
            tenantEntity.setReportingCycle(reportingCycle);
        }
        return tenantEntity;
    }

}
