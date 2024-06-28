package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典类型列表查询请求参数
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@Schema(name = "字典类型列表查询请求参数", description = "字典类型列表查询请求参数")
public class DictTypeListRequest {
    /**
     * 搜索条件 名称/编码
     */
    @Schema(description = "搜索条件 名称/编码", example = "01")
    private String keywords;

}
