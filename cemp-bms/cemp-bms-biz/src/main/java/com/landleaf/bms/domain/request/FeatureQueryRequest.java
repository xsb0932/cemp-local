package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 功能管理-列表查询请求参数
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "功能管理-列表查询请求参数", description = "功能管理-列表查询请求参数")
public class FeatureQueryRequest extends PageParam {

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符", example = "wxxxx001")
    private String identifier;

    /**
     * 功能名称
     */
    @Schema(description = "功能名称", example = "xxx")
    private String functionName;

}
