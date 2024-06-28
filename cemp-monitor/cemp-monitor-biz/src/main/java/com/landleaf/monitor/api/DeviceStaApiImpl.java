package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.monitor.service.DeviceMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Yang
 */
@RestController
@RequiredArgsConstructor
public class DeviceStaApiImpl implements DeviceStaApi {
    private final DeviceMonitorService deviceMonitorService;

    @Override
    public Response<List<DeviceStaDTO>> listStaDeviceByCategory(Long tenantId, String categoryType) {
        return Response.success(deviceMonitorService.listStaDeviceByCategory(DeviceStaCategoryEnum.ofCode(categoryType)));
    }

    @Override
    public Response<List<ProjectStaDTO>> listStaProject(Long tenantId) {
        return Response.success(deviceMonitorService.listStaProject(tenantId));
    }
}
