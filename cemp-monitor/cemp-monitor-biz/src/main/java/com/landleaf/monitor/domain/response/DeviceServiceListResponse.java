package com.landleaf.monitor.domain.response;

import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.api.json.FunctionParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备服务信息,继承是为了在类型为GeneralWritePoints->SysWritablePoint时添加controlPointDesc
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "设备服务信息", description = "设备服务信息")
public class DeviceServiceListResponse extends ProductDeviceServiceListResponse {

    /**
     * 控制参数
     */
    @Schema(description = "控制参数")
    private List<FunctionParameter> controlPointDesc;
}
