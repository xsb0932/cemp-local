package com.landleaf.oauth.domain.request;

import com.landleaf.oauth.domain.entity.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

/**
 * 企业信息变更
 *
 * @author yue lin
 * @since 2023/6/9 13:34
 */
@Data
public class EnterpriseUpdateRequest {

    /**
     * id
     */
    @NotNull(message = "主键不能为空")
    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    @Size(min = 1, max = 30, message = "企业名称长度区间{min}-{max}")
    @Schema(description = "企业名称", example = "企业名称")
    private String name;

    /**
     * 企业编码
     */
    @NotBlank(message = "企业编码不能为空")
    @Size(min = 1, max = 10, message = "企业编码长度区间{min}-{max}")
    @Schema(description = "企业编码", example = "CODE")
    private String code;

    /**
     * 社会信用代码
     */
    @NotBlank(message = "社会信用代码不能为空")
    @Schema(description = "社会信用代码", example = "社会信用代码")
    private String socialCreditCode;

    /**
     * 营业执照
     */
    @NotBlank(message = "营业执照不能为空")
    @Schema(description = "营业执照", example = "营业执照")
    private String businessLicense;

    /**
     * logo
     */
    @NotBlank(message = "LOGO不能为空")
    @Schema(description = "logo", example = "logo")
    private String logo;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    @Size(min = 1, max = 100, message = "地址长度区间{min}-{max}")
    @Schema(description = "地址", example = "山东省日照市市辖区东张乡")
    private String address;


    public TenantEntity toEntity() {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setId(this.id);
        tenantEntity.setName(this.name);
        tenantEntity.setCode(this.code);
        tenantEntity.setSocialCreditCode(this.socialCreditCode);
        tenantEntity.setBusinessLicense(this.businessLicense);
        tenantEntity.setLogo(this.logo);
        tenantEntity.setAddress(this.address);
        return tenantEntity;
    }

}
