package com.landleaf.bms.api.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目-分页列表查询请求参数
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@Data
@Schema(name = "项目-分页列表查询请求参数", description = "项目-分页列表查询请求参数")
public class ProjectListRequest extends PageParam {
    /**
     * 项目名称
     * 租户内唯一
     */
    @Schema(description = "项目名称", example = "XXX")
    private String name;

    /**
     * 项目编码
     * 租户内唯一
     */
    @Schema(description = "项目编码", example = "XXX")
    private String code;

    /**
     * 项目业态（字典编码-PROJECT_BIZ_TYPE）
     */
    @Schema(description = "项目业态（字典编码-PROJECT_BIZ_TYPE）", example = "XXX")
    private String bizType;

    /**
     * 项目状态（字典编码-PROJECT_STATUS）
     */
    @Schema(description = "项目状态（字典编码-PROJECT_STATUS）", example = "XXX")
    private String status;

}
