package com.landleaf.jjgj;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.api.ReportPushApi;
import com.landleaf.energy.api.dto.ProjectReportPushDTO;
import com.landleaf.jjgj.domain.dto.CheckinDayQueryDTO;
import com.landleaf.jjgj.service.CheckinDayService;
import com.landleaf.mail.domain.param.JjgjReportPushMail;
import com.landleaf.mail.service.MailService;
import feign.Response;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

@SpringBootTest
class CempCusJjgjApplicationTests {

    @Resource
    private CheckinDayService checkinDayService;

    @Test
    public void quertChechkinDay() {
        CheckinDayQueryDTO queryDTO = new CheckinDayQueryDTO();
        queryDTO.setStartTime("2023-10-19");
        queryDTO.setEndTime("2023-10-19");
        queryDTO.setBizProjectId("PJ00000001");
        TenantContext.setTenantId(2L);
        System.out.println(checkinDayService.list(queryDTO));
    }

    @Test
    public void delChechkinDay() {
        TenantContext.setTenantId(2L);
        checkinDayService.updateIsDeleted("1", CommonConstant.DELETED_FLAG_DELETED);
    }


    @Autowired
    private ReportPushApi reportPushApi;
    @Autowired
    private MailService mailService;

    @Test
    public void testWeekReportPush() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusWeeks(1L);
        DayOfWeek dayOfWeek = lastWeek.getDayOfWeek();
        LocalDateTime end = now.minusDays(dayOfWeek.getValue());
        LocalDateTime start = now.minusDays(6 + dayOfWeek.getValue());

        Response response = reportPushApi.projectReportPushData(
                new ProjectReportPushDTO("PJ00000001",
                        LocalDateTimeUtil.format(start, "yyyy-MM-dd"),
                        LocalDateTimeUtil.format(end, "yyyy-MM-dd"),
                        CollectionUtil.newArrayList("project.electricity.energyUsageFee.flat",
                                "project.electricity.energyUsageFee.valley",
                                "project.electricity.energyUsageFee.total",
                                "project.electricity.energyUsageFee.tip",
                                "project.electricity.energyUsageFee.peak"))
        );

        if (response.status() == 200) {
            try {
                mailService.sendMail(JjgjReportPushMail.weekMail("yangyang@landleaf-tech.com", "项目报表.xlsx", new ByteArrayResource(IoUtil.readBytes(response.body().asInputStream()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMonthReportPush() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1L);
        int dayOfMonth = now.getDayOfMonth();
        LocalDateTime end = now.minusDays(dayOfMonth);
        LocalDateTime start = now.minusDays(dayOfMonth + lastMonth.getMonth().maxLength() - 1);
        Response response = reportPushApi.projectReportPushData(
                new ProjectReportPushDTO("PJ00000001",
                        LocalDateTimeUtil.format(start, "yyyy-MM-dd"),
                        LocalDateTimeUtil.format(end, "yyyy-MM-dd"),
                        CollectionUtil.newArrayList("project.electricity.energyUsageFee.flat",
                                "project.electricity.energyUsageFee.valley",
                                "project.electricity.energyUsageFee.total",
                                "project.electricity.energyUsageFee.tip",
                                "project.electricity.energyUsageFee.peak"))
        );
        if (response.status() == 200) {
            try {
                mailService.sendMail(JjgjReportPushMail.monthMail("yangyang@landleaf-tech.com", "项目报表.xlsx", new ByteArrayResource(IoUtil.readBytes(response.body().asInputStream()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
