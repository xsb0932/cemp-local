package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GatewayListRequest
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Data
public class GatewayListRequest extends PageParam {

    /**
     * 网关名称
     */
    @Schema(description = "网关名称", example = "123")
    private String name;
    /**
     * 网关状态
     */
    @Schema(description = "网关状态", example = "123")
    private String status;

}
