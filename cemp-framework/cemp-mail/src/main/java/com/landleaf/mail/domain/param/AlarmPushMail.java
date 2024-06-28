package com.landleaf.mail.domain.param;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmPushMail extends AbstractMail {
    private String projectName;
    private String objName;
    private LocalDateTime eventTime;
    private String alarmDesc;

    @Override
    public String format() {
        return getMailFormat().formatted(projectName, objName, LocalDateTimeUtil.format(eventTime, "yyyy-MM-dd HH:mm:ss"), alarmDesc, getImage());
    }

    public static AbstractMail alarmToMail(String to, String projectName, String objName, LocalDateTime eventTime, String alarmDesc) {
        AlarmPushMail mail = new AlarmPushMail();
        mail.setTo(to);
        mail.setImage(IMAGE_BASE64);
        mail.setMailFormatType(7L);
        mail.setSubject("告警通知");
        mail.setProjectName(projectName);
        mail.setObjName(objName);
        mail.setEventTime(eventTime);
        mail.setAlarmDesc(alarmDesc);
        return mail;
    }
}
