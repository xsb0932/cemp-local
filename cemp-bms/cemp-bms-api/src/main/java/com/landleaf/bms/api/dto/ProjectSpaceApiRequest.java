package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目-空间管理请求参数
 *
 * @author yue lin
 * @since 2023/7/12 15:35
 */
@Data
public class ProjectSpaceApiRequest {

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
        @PositiveOrZero(message = "区域面积参数异常")
        @Schema(description = "区域面积")
        private BigDecimal spaceProportion;

        /**
         * 区域备注
         */
        @Schema(description = "区域备注")
        private String spaceDescription;

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
        @PositiveOrZero(message = "区域面积参数异常")
        @Schema(description = "区域面积")
        private BigDecimal spaceProportion;

        /**
         * 区域备注
         */
        @Schema(description = "区域备注")
        private String spaceDescription;

    }

}
