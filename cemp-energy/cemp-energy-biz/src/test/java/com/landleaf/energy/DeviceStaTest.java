package com.landleaf.energy;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceElectricityDayMapper;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.energy.service.ProjectStaService;
import com.landleaf.energy.service.StaSubitemStatisticsService;
import com.landleaf.energy.service.job.DeviceStaDayService;
import com.landleaf.job.api.dto.JobRpcRequest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DeviceStaTest
 *
 * @author 张力方
 * @since 2023/8/7
 **/
@SpringBootTest
class DeviceStaTest {
    @Autowired
    DeviceStaDayService deviceStaDayService;
    @Autowired
    StaSubitemStatisticsService staSubitemStatisticsService;
    @Autowired
    ProjectStaService projectStaService;

    @Test
    void testProjSta() {
        System.out.println(projectStaService.getProjectKpi("PJ00000001", "2"));
    }

    @Test
    void initStaSubitemStatisticsServiceHour() {
        TenantContext.setIgnore(true);
        LocalDateTime startTime = LocalDateTime.of(2023, 8, 25, 0, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        List<String> strings = new ArrayList<>();
        while (true) {
            if (!(startTime.compareTo(now) < 0)) break;
            String startStr = LocalDateTimeUtil.formatNormal(startTime);
            strings.add(startStr.substring(0, 13));
            startTime = startTime.plusHours(1L);
        }
        String[] strings1 = new String[strings.size()];
        String[] strings2 = strings.toArray(strings1);
        staSubitemStatisticsService.statisticsByHour(strings2, new JobRpcRequest());
    }


    @Resource
    private ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;

    @Test
    void test1() {
        TenantContext.setIgnore(true);
        ProjectStaDeviceElectricityDayEntity entity = projectStaDeviceElectricityDayMapper.selectById(136414);
        projectStaDeviceElectricityDayMapper.updateById(entity);
    }
}
