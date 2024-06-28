package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.comm.tenant.TenantContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * 项目-空间管理请求参数
 *
 * @author yue lin
 * @since 2023/7/12 15:35
 */
@Data
public class ProjectSpaceRequest {

    @Data
    @Schema(description = "创建区域")
    public static class Create {

        /**
         * 项目ID
         */
        @NotNull(message = "项目ID不能为空")
        @Schema(description = "项目ID")
        private Long projectId;

        /**
         * 区域名称
         */
        @Length(min = 1, max = 50, message = "区域名称长度{min}-{max}")
        @NotBlank(message = "区域名称不能为空")
        @Schema(description = "区域名称")
        private String spaceName;

        /**
         * 区域父级ID
         */
        @NotNull(message = "区域父级不能为空")
        @Schema(description = "区域父级ID")
        private Long parentId;

        /**
         * 区域类型
         */
        @NotBlank(message = "区域类型不能为空")
        @Schema(description = "区域类型")
        private String spaceType;

        /**
         * 区域面积
         */
        @NotNull(message = "区域面积不能为空")
        @PositiveOrZero(message = "区域面积参数异常")
        @Schema(description = "区域面积")
        private BigDecimal spaceProportion;

        /**
         * 区域备注
         */
        @Schema(description = "区域备注")
        private String spaceDescription;

        public ProjectSpaceEntity toEntity() {
            ProjectSpaceEntity entity = new ProjectSpaceEntity();
            entity.setName(spaceName);
            entity.setParentId(parentId);
            entity.setType(spaceType);
            entity.setProportion(spaceProportion);
            entity.setDescription(spaceDescription);
            entity.setProjectId(this.projectId);
            entity.setTenantId(TenantContext.getTenantId());
            return entity;
        }

    }

    @Data
    @Schema(description = "更新区域")
    public static class Update {

        /**
         * 区域ID
         */
        @NotNull(message = "区域ID不能为空")
        @Schema(description = "区域ID")
        private Long spaceId;

        /**
         * 项目ID
         */
        @NotNull(message = "项目ID不能为空")
        @Schema(description = "项目ID")
        private Long projectId;

        /**
         * 区域名称
         */
        @Size(min = 1, max = 50, message = "区域名称长度{min}-{max}")
        @NotBlank(message = "区域名称不能为空")
        @Schema(description = "区域名称")
        private String spaceName;

        /**
         * 区域类型
         */
        @NotBlank(message = "区域类型不能为空")
        @Schema(description = "区域类型")
        private String spaceType;

        /**
         * 区域面积
         */
        @NotNull(message = "区域面积不能为空")
        @PositiveOrZero(message = "区域面积参数异常")
        @Schema(description = "区域面积")
        private BigDecimal spaceProportion;

        /**
         * 区域备注
         */
        @Schema(description = "区域备注")
        private String spaceDescription;

        public ProjectSpaceEntity toEntity() {
            ProjectSpaceEntity entity = new ProjectSpaceEntity();
            entity.setId(spaceId);
            entity.setName(spaceName);
            entity.setType(spaceType);
            entity.setProportion(spaceProportion);
            entity.setDescription(spaceDescription);
            entity.setProjectId(this.projectId);
            entity.setTenantId(TenantContext.getTenantId());
            return entity;
        }

    }

}
