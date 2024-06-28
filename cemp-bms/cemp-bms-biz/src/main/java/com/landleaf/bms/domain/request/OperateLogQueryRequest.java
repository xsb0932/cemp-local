package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日志查询请求
 *
 * @author xushibai
 * @since 2024/4/25
 **/
@Data
@Schema(name = "日志查询请求", description = "日志查询请求")
public class OperateLogQueryRequest extends PageParam {

    @Schema(description = "用户id")
    private String userid;

    @Schema(description = "查询起期")
    private String timeBegin;

    @Schema(description = "查询止期")
    private String timeEnd;

}
