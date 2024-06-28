package com.landleaf.monitor;

import cn.hutool.json.JSONUtil;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.controller.DeviceHistoryController;
import com.landleaf.monitor.domain.dto.DeviceControlDTO;
import com.landleaf.monitor.domain.dto.HistoryQueryDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.DeviceWriteService;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class ProjectInitTest {
    @Resource
    private DeviceMonitorService deviceMonitorService;

    @Resource
    private DeviceWriteService deviceWriteServiceImpl;

    @Resource
    private DeviceHistoryController deviceHistoryController;
    @Resource
    private BizSequenceService bizSequenceService;

    @Test
    public void test1() {
        System.out.println(bizSequenceService.next(BizSequenceEnum.AREA));
    }

    @Test
    public void queryHistory() {
        TenantContext.setTenantId(2L);
        HistoryQueryDTO queryDTO = new HistoryQueryDTO();
        queryDTO.setBizDeviceIds("D000000000003,D000000000004,D000000000005,D000000000006,D000000000061");
        queryDTO.setAttrCode("Uca,CST");
        queryDTO.setTimes(new String[]{"2023-06-19 00:00:00", "2023-06-25 23:59:59"});
//        queryDTO.setStartTime("2023-06-08 00:00:00");
//        queryDTO.setEndTime("2023-06-20 00:00:00");
        queryDTO.setPeriodType(1);
        System.out.println(JSONUtil.toJsonStr(deviceHistoryController.history(queryDTO)));
    }

    @Test
    public void cmdTest() throws InterruptedException {
        DeviceControlDTO dto = new DeviceControlDTO();
        String msgId = UUID.randomUUID().toString();
        dto.setMsgId(msgId);
        dto.setBizDeviceId("D000000000056");

        new Thread(() -> {
            TenantContext.setTenantId(2L);
            System.out.println(deviceWriteServiceImpl.writeCmd(dto));
        }).start();
        Thread.sleep(2000L);
        deviceWriteServiceImpl.cmdAck(msgId);
        Thread.sleep(10000L);
    }

    @Test
    public void initTest() {
        //手动插入锦江设备
        TenantContext.setTenantId(2L);
//        deviceMonitorService.remove(Wrappers.emptyWrapper());
        // excel 函数 =CONCATENATE("list.add(build(""PJ00000001"", """,E2,""", """,D2,""", ""PC0003"", """,A2,""", """,B2,"""));")
        List<DeviceMonitorEntity> list = new ArrayList<>();
        list.add(build("PJ00000001", "D000000000001", "PK00000003", "PC0003", "总水表", "B01-ZPD-WM-001"));
        list.add(build("PJ00000001", "D000000000002", "PK00000001", "PC0001", "1#楼三、四层总电表", "B01-ZPD-EM-3F4F"));
        list.add(build("PJ00000001", "D000000000003", "PK00000001", "PC0001", "1#楼一、二层总电表", "B01-ZPD-EM-1F2F"));
        list.add(build("PJ00000001", "D000000000004", "PK00000001", "PC0001", "1#楼电梯电表", "B01-ZPD-EM-DT"));
        list.add(build("PJ00000001", "D000000000005", "PK00000001", "PC0001", "生活水泵电表", "B01-ZPD-EM-SB"));
        list.add(build("PJ00000001", "D000000000006", "PK00000001", "PC0001", "1#楼2F总电表", "B01-KCPD-EM-2F"));
        list.add(build("PJ00000001", "D000000000061", "PK00000001", "PC0001", "1#楼2F空调东电表", "B01-KCPD-EM-2FKT1"));
        list.add(build("PJ00000001", "D000000000062", "PK00000001", "PC0001", "1#楼2F空调西电表", "B01-KCPD-EM-2FKT2"));
        list.add(build("PJ00000001", "D000000000008", "PK00000001", "PC0001", "1#楼3F总电表", "B01-KCPD-EM-3F"));
        list.add(build("PJ00000001", "D000000000063", "PK00000001", "PC0001", "1#楼3F空调东电表", "B01-KCPD-EM-3FKT1"));
        list.add(build("PJ00000001", "D000000000064", "PK00000001", "PC0001", "1#楼3F空调西电表", "B01-KCPD-EM-3FKT2"));
        list.add(build("PJ00000001", "D000000000010", "PK00000001", "PC0001", "1#楼4F总电表", "B01-KCPD-EM-4F"));
        list.add(build("PJ00000001", "D000000000065", "PK00000001", "PC0001", "1#楼4F空调东电表", "B01-KCPD-EM-4FKT1"));
        list.add(build("PJ00000001", "D000000000066", "PK00000001", "PC0001", "1#楼4F空调西电表", "B01-KCPD-EM-4FKT2"));
        list.add(build("PJ00000001", "D000000000012", "PK00000001", "PC0001", "热泵总电表", "RSPD-EM"));
        list.add(build("PJ00000001", "D000000000013", "PK00000001", "PC0001", "热泵电表", "RSPD-EM-RB"));
        list.add(build("PJ00000001", "D000000000014", "PK00000001", "PC0001", "二次泵1（1#楼）电表", "RSPD-EM-RB-B1"));
        list.add(build("PJ00000001", "D000000000015", "PK00000001", "PC0001", "二次泵2（2#楼）电表", "RSPD-EM-RB-B2"));
        list.add(build("PJ00000001", "D000000000016", "PK00000001", "PC0001", "二次泵3（3#楼）电表", "RSPD-EM-RB-B3"));
        list.add(build("PJ00000001", "D000000000017", "PK00000001", "PC0001", "2#楼厨房电表", "B02-ZPD1-EM-CF"));
        list.add(build("PJ00000001", "D000000000018", "PK00000001", "PC0001", "2#楼一层总电表", "B02-ZPD1-EM-1F"));
        list.add(build("PJ00000001", "D000000000019", "PK00000001", "PC0001", "2#楼电梯电表", "B02-ZPD1-EM-DT"));
        list.add(build("PJ00000001", "D000000000020", "PK00000001", "PC0001", "2#楼三、四层总电表", "B02-ZPD2-EM-3F4F"));
        list.add(build("PJ00000001", "D000000000021", "PK00000001", "PC0001", "2#楼二层总电表", "B02-ZPD2-EM-2F"));
        list.add(build("PJ00000001", "D000000000022", "PK00000001", "PC0001", "地下室照明、员工餐厅电表", "B02-ZPD2-EM-BF"));
        list.add(build("PJ00000001", "D000000000023", "PK00000001", "PC0001", "2#楼办公室空调电表", "B02-ZPD2-EM-KT"));
        list.add(build("PJ00000001", "D000000000024", "PK00000001", "PC0001", "2#楼2F总表", "B02-KCPD-EM-2F"));
        list.add(build("PJ00000001", "D000000000025", "PK00000001", "PC0001", "2#楼2F空调电表", "B02-KCPD-EM-2FKT"));
        list.add(build("PJ00000001", "D000000000026", "PK00000001", "PC0001", "2#楼3F总表", "B02-KCPD-EM-3F"));
        list.add(build("PJ00000001", "D000000000027", "PK00000001", "PC0001", "2#楼3F空调电表", "B02-KCPD-EM-3FKT"));
        list.add(build("PJ00000001", "D000000000028", "PK00000001", "PC0001", "2#楼4F总表", "B02-KCPD-EM-4F"));
        list.add(build("PJ00000001", "D000000000029", "PK00000001", "PC0001", "2#楼4F空调电表", "B02-KCPD-EM-4FKT"));
        list.add(build("PJ00000001", "D000000000030", "PK00000001", "PC0001", "大堂后空调电表", "B02-1F-EM-KT01"));
        list.add(build("PJ00000001", "D000000000031", "PK00000001", "PC0001", "大堂前空调电表", "B02-1F-EM-KT02"));
        list.add(build("PJ00000001", "D000000000032", "PK00000001", "PC0001", "休息室右空调电表", "B02-1F-EM-KT03"));
        list.add(build("PJ00000001", "D000000000033", "PK00000001", "PC0001", "行李房空调电表", "B02-1F-EM-KT04"));
        list.add(build("PJ00000001", "D000000000034", "PK00000001", "PC0001", "休息室左空调电表", "B02-1F-EM-KT05"));
        list.add(build("PJ00000001", "D000000000035", "PK00000001", "PC0001", "电梯口空调电表", "B02-1F-EM-KT06"));
        list.add(build("PJ00000001", "D000000000036", "PK00000001", "PC0001", "大堂照明电表", "B02-1F-EM-ZM01"));
        list.add(build("PJ00000001", "D000000000037", "PK00000001", "PC0001", "餐厅1照明电表", "B02-1F-EM-ZM02"));
        list.add(build("PJ00000001", "D000000000038", "PK00000001", "PC0001", "餐厅2照明电表", "B02-1F-EM-ZM03"));
        list.add(build("PJ00000001", "D000000000039", "PK00000001", "PC0001", "8505室电表", "B02-4F-EM-505"));
        list.add(build("PJ00000001", "D000000000040", "PK00000001", "PC0001", "8503室电表", "B02-4F-EM-503"));
        list.add(build("PJ00000001", "D000000000041", "PK00000001", "PC0001", "8303室电表", "B02-3F-EM-303"));
        list.add(build("PJ00000001", "D000000000042", "PK00000001", "PC0001", "8312室电表", "B02-3F-EM-312"));
        list.add(build("PJ00000001", "D000000000043", "PK00000001", "PC0001", "8203室电表", "B02-2F-EM-203"));
        list.add(build("PJ00000001", "D000000000044", "PK00000002", "PC0002", "燃气表", "SHLL30525S1"));
        list.add(build("PJ00000001", "D000000000045", "PK00000001", "PC0001", "1号楼总电表", "SHLL30525S4"));
        list.add(build("PJ00000001", "D000000000046", "PK00000001", "PC0001", "2号楼总电表1", "SHLL30525S3"));
        list.add(build("PJ00000001", "D000000000047", "PK00000001", "PC0001", "2号楼总电表2", "SHLL30525S2"));
        list.add(build("PJ00000001", "D000000000048", "PK00000004", "PC0004", "8203空调遥控器", "68799"));
        list.add(build("PJ00000001", "D000000000049", "PK00000004", "PC0004", "8303空调遥控器", "78015"));
        list.add(build("PJ00000001", "D000000000050", "PK00000004", "PC0004", "8312空调遥控器", "78084"));
        list.add(build("PJ00000001", "D000000000051", "PK00000004", "PC0004", "8503空调遥控器", "80670"));
        list.add(build("PJ00000001", "D000000000052", "PK00000004", "PC0004", "8505空调遥控器", "80607"));
        list.add(build("PJ00000001", "D000000000053", "PK00000004", "PC0004", "三层东空调遥控器", "80602"));
        list.add(build("PJ00000001", "D000000000054", "PK00000004", "PC0004", "三层西空调遥控器", "80599"));
        list.add(build("PJ00000001", "D000000000055", "PK00000004", "PC0004", "层东空调遥控器", "80603"));
        list.add(build("PJ00000001", "D000000000056", "PK00000004", "PC0004", "二层西空调遥控器", "80600"));
        list.add(build("PJ00000001", "D000000000057", "PK00000004", "PC0004", "五层东空调遥控器", "80601"));
        list.add(build("PJ00000001", "D000000000058", "PK00000004", "PC0004", "五层西空调遥控器", "80598"));
        list.add(build("PJ00000001", "D000000000059", "PK00000004", "PC0004", "大堂空调遥控器", "80604"));
        list.add(build("PJ00000001", "D000000000060", "PK00000004", "PC0004", "大堂空调遥控器", "80605"));
        deviceMonitorService.saveBatch(list);
    }

    private DeviceMonitorEntity build(String projectId, String deviceId, String productId, String categoryId, String name, String code) {
        DeviceMonitorEntity entity = new DeviceMonitorEntity();
        entity.setBizProjectId(projectId)
                .setBizDeviceId(deviceId)
                .setBizProductId(productId)
                .setBizCategoryId(categoryId)
                .setCode(code)
                .setName(name);
        return entity;
    }
}
