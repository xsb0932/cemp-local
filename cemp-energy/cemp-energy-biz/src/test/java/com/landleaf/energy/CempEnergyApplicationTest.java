package com.landleaf.energy;

import cn.hutool.json.JSONUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.controller.ProjectCnfGasFeeController;
import com.landleaf.energy.controller.ProjectCnfWaterFeeController;
import com.landleaf.energy.controller.StaSubareaStatisticsTask;
import com.landleaf.energy.controller.StaSubitemStatisticsTask;
import com.landleaf.energy.controller.job.DeviceStaJobController;
import com.landleaf.energy.domain.dto.ProjectCnfGasFeeAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfWaterFeeAddDTO;
import com.landleaf.energy.domain.dto.SubitemRelationDevicesDTO;
import com.landleaf.energy.domain.vo.ProjectCnfWaterFeeVO;
import com.landleaf.energy.service.ProjectCnfTimePeriodService;
import com.landleaf.energy.service.ProjectCnfWaterFeeService;
import com.landleaf.job.api.dto.JobRpcRequest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CempEnergyApplicationTest {
    @Resource
    private StaSubareaStatisticsTask staSubareaStatisticsTask;

    @Resource
    private StaSubitemStatisticsTask staSubitemStatisticsTask;

    @Test
    public void testSubareaStatisticsHour() {
        staSubareaStatisticsTask.statisticsByHour("2023-06-23 02", new JobRpcRequest());
    }


    @Test
    public void testSubareaStatisticsDay() {
        JobRpcRequest req = new JobRpcRequest();
        staSubareaStatisticsTask.statisticsByDay("2023-12-30", new JobRpcRequest());
    }

    @Test
    public void testSubareaStatisticsMonth() {
        staSubareaStatisticsTask.statisticsByMonth("2023-06", new JobRpcRequest());
    }

    @Test
    public void testSubareaStatisticsYear() {
        staSubareaStatisticsTask.statisticsByYear("2023", new JobRpcRequest());
    }

    @Test
    public void testSubitemStatisticsHour() {
        staSubitemStatisticsTask.statisticsByHour("2023-06-23 02", new JobRpcRequest());
    }

    @Test
    public void testSubitemStatisticsDay() {
        staSubitemStatisticsTask.statisticsByDay("2023-06-23", new JobRpcRequest());
    }

    @Test
    public void testSubitemStatisticsMonth() {
        staSubitemStatisticsTask.statisticsByMonth("2023-06", new JobRpcRequest());
    }

    @Test
    public void testSubitemStatisticsYear() {
        staSubitemStatisticsTask.statisticsByYear("2023", new JobRpcRequest());
    }

    @Resource
    private DeviceStaJobController deviceStaJobController;

    @Resource
    private ProjectCnfTimePeriodService projectCnfTimePeriodServiceImpl;

    @Resource
    private ProjectCnfWaterFeeService projectCnfWaterFeeServiceImpl;

    @Resource
    private ProjectCnfWaterFeeController projectCnfWaterFeeController;

    @Resource
    private ProjectCnfGasFeeController projectCnfGasFeeController;

    @Test
    public void testQueryElecConf() {
        TenantContext.setTenantId(2L);
        List<?> result = projectCnfTimePeriodServiceImpl.listByBizProjectId("PJ00000001");
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testInsertElecConf() {
//        TenantContext.setTenantId(2L);
//        ProjectCnfTimePeriodAddDTO add = new ProjectCnfTimePeriodAddDTO();
//        add.setTime("2023年12月");
//        add.setOriginalTime("2023年11月");
//        add.setFlatPrice(new BigDecimal("2"));
//        add.setPeakPrice(new BigDecimal("3"));
//        add.setTipPrice(new BigDecimal("4"));
//        add.setValleyPrice(new BigDecimal("1"));
//        add.setProjectId("PJ00000001");
//
//        List<TimeDuringDTO> list = new ArrayList();
//        list.add(TimeDuringDTO.builder().timeBegin(1).timeEnd(2).build());
//        add.setFlatTimes(list);
//        list = new ArrayList();
//        list.add(TimeDuringDTO.builder().timeBegin(3).timeEnd(4).build());
//        add.setTipTimes(list);
//        list = new ArrayList();
//        list.add(TimeDuringDTO.builder().timeBegin(6).timeEnd(7).build());
//        add.setValleyTimes(list);
//        list = new ArrayList();
//        list.add(TimeDuringDTO.builder().timeBegin(10).timeEnd(11).build());
//        add.setPeakTimes(list);
//        Boolean result = projectCnfTimePeriodServiceImpl.intelligentInsert(add);
//        System.out.println(result);
    }

    @Test
    public void testWaterCnfDetail() {
        TenantContext.setTenantId(2L);
        ProjectCnfWaterFeeVO vo = projectCnfWaterFeeServiceImpl.selectByBizProjectId("PJ00000001");
        System.out.println(JSONUtil.toJsonStr(vo));
    }

    @Test
    public void testWaterCnfAdd() {
        TenantContext.setTenantId(2L);
        ProjectCnfWaterFeeAddDTO add = new ProjectCnfWaterFeeAddDTO();
        add.setProjectId("PJ00000001");
        add.setChargingMode(0);
        add.setPrice(new BigDecimal("10.3"));
        add.setSewagePrice(new BigDecimal("7.5"));
        add.setSewageRatio(new BigDecimal("65"));

        List<SubitemRelationDevicesDTO> deviceList = new ArrayList<>();
        SubitemRelationDevicesDTO temp = new SubitemRelationDevicesDTO();
        temp.setBizDeviceId("D000000000001");
        temp.setComputerTag("1");
        deviceList.add(temp);
        temp = new SubitemRelationDevicesDTO();
        temp.setBizDeviceId("D000000000002");
        temp.setComputerTag("-1");
        deviceList.add(temp);

        add.setDeviceList(deviceList);

        projectCnfWaterFeeController.save(add);
    }

    @Test
    public void testGasCnfDetail() {
        TenantContext.setTenantId(2L);
        Response vo = projectCnfGasFeeController.get("PJ00000001");
        System.out.println(JSONUtil.toJsonStr(vo));
    }

    @Test
    public void testGasCnfAdd() {
        TenantContext.setTenantId(2L);
        ProjectCnfGasFeeAddDTO add = new ProjectCnfGasFeeAddDTO();
        add.setProjectId("PJ00000001");
        add.setChargingMode(0);
        add.setPrice(new BigDecimal("10.3"));

        List<SubitemRelationDevicesDTO> deviceList = new ArrayList<>();
        SubitemRelationDevicesDTO temp = new SubitemRelationDevicesDTO();
        temp.setBizDeviceId("D000000000001");
        temp.setComputerTag("1");
        deviceList.add(temp);
        temp = new SubitemRelationDevicesDTO();
        temp.setBizDeviceId("D000000000002");
        temp.setComputerTag("-1");
        deviceList.add(temp);

        add.setDeviceList(deviceList);

        projectCnfGasFeeController.save(add);
    }

    @Test
    public void testDeviceSta() {
        LocalDateTime startDate = LocalDateTime.of(2023, 8, 26, 0, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        while (startDate.compareTo(now) < 0) {
            JobRpcRequest request = new JobRpcRequest();
            request.setJobId(1L).setExecUser(1L).setExecType(1);
            deviceStaJobController.staHour(startDate, request);
            if (startDate.getHour() == 0) {
                deviceStaJobController.staDay(startDate, request);
            }
            if (startDate.getDayOfMonth() == 1 && startDate.getHour() == 0) {
                deviceStaJobController.staMonth(startDate, request);
            }
            startDate = startDate.plusHours(1L);
        }
    }

}
