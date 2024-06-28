package com.landleaf.monitor;

import com.alibaba.fastjson2.JSON;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.domain.dto.DeviceMonitorCurrentDTO;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.impl.DeviceMonitorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
public class DeviceMonitorTest {

    @Autowired
    private DeviceMonitorService deviceMonitorServiceImpl;

    @Test
    public void getCurrent() {
        DeviceMonitorCurrentDTO qrd = new DeviceMonitorCurrentDTO();
        qrd.setBizCategoryId("PC0026");
        qrd.setProjectIds(Arrays.asList("PJ00001021"));
        TenantContext.setTenantId(1004L);
        System.out.println(JSON.toJSONString(deviceMonitorServiceImpl.getcurrent(qrd)));
    }

    @Test
    public void getSpaceV1() {
//        System.out.println(JSON.toJSONString(deviceMonitorServiceImpl.getDeviceTreeV1Space()));
    }

}
