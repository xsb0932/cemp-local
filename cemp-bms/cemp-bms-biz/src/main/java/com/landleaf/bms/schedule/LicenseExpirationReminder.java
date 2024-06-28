package com.landleaf.bms.schedule;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.util.date.DateUtils;
import com.landleaf.mail.domain.param.LicenseExpireAlarmMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserEmailDTO;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LicenseExpirationReminder {

    @Resource
    private LicenseCheck licenseCheck;

    @Resource
    private MailService mailService;

    @Resource
    private UserRpcApi userRpcApi;

    @Resource
    private TenantApi tenantApi;

    /**
     * 每天执行一次，判断license,如果到期仅剩30天内，则邮件提醒
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void check() {
        String endTime = licenseCheck.getEndTime();
        if (StringUtils.hasText(endTime)) {
            // check
            LocalDate now = LocalDate.now();
            LocalDate endDate = LocalDate.parse(endTime);
            long diff = ChronoUnit.DAYS.between(now, endDate);
            diff = Math.abs(diff);
            if (diff < 30 && diff > 0) {
                // 获取平台管理员账号
                Long tenantId = tenantApi.getTenantAdmin().getCheckedData();
                UserEmailDTO userEmailDTO = userRpcApi.getAdminUserEmail(tenantId).getResult();
                // 发送邮件通知
                mailService.sendMailAsync(LicenseExpireAlarmMail.mail(userEmailDTO.getEmail(), "license过期提醒", String.valueOf(diff)));
            }
        }
    }
}
