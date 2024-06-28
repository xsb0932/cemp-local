package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备批量导入解析对象
 *
 * @author hebin
 * @since 2023-07-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DeviceMonitorOfExcel对象", description = "设备批量导入解析对象")
public class DeviceIotOfExcel {

    private String projectName;
    private String productName;
    private String deviceName;
    private String deviceCode;
    private String bizDeviceId;
    private String spaceName;
    private String locationDesc;
    private String deviceDesc;


}
