package com.landleaf.monitor.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 监控设备列表查询
 *
 * @author 张力方
 * @since 2023/7/20
 **/
@Data
@Schema(name = "MonitorDeviceQueryRequest", description = "监控设备列表查询")
public class MonitorDeviceQueryRequest extends PageParam {
    /**
     * 项目业务id
     */
    @Schema(description = "项目业务id")
    private String bizProjectId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

}
