package com.landleaf.monitor.controller;

import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.dto.DeviceControlDTO;
import com.landleaf.monitor.domain.enums.ModeTypeEnum;
import com.landleaf.monitor.service.DeviceModeService;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.DeviceWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备写入控制层接口定义
 *
 * @author hebin
 * @since 2023-06-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/device-cmd")
@Tag(name = "设备写入控制层接口定义", description = "设备写入控制层接口定义")
public class DeviceWriteController {

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    @Resource
    private DeviceWriteService deviceWriteServiceImpl;

    @Resource
    private DeviceModeService deviceModeServiceImpl;

    @Resource
    private UserProjectApi userProjectApi;

    /**
     * 写入设备控制指令
     *
     * @param cmd 设备控制的参数封装
     * @return 返回写入结果，成功/世白
     */
    @PostMapping("/write")
    @Operation(summary = "写入设备控制指令", description = "写入设备控制指令")
    public Response<Boolean> writeCmd(@RequestBody DeviceControlDTO cmd) {
        if(StringUtils.isNotBlank(cmd.getMode())){
            deviceModeServiceImpl.setMode(cmd);
            if(ModeTypeEnum.MODE_2.getCode().equals(cmd.getMode())){
                return Response.success();
            }
        }
        boolean result = deviceWriteServiceImpl.writeCmd(cmd);
        return Response.success(result);
    }
}
