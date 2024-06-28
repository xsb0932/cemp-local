package com.landleaf.mail.domain.param;

import lombok.Data;

@Data
public class LicenseExpireAlarmMail extends AbstractMail {

    private String time;

    @Override
    public String format() {
        return getMailFormat().formatted(time, getImage());
    }

    /**
     * 消息推送
     *
     * @param to 目标邮箱
     * @return 结果
     */
    public static AbstractMail mail(String to, String title, String time) {
        LicenseExpireAlarmMail mail = new LicenseExpireAlarmMail();
        mail.setTo(to);
        mail.setTime(time);
        mail.setImage(IMAGE_BASE64);
        mail.setMailFormatType(8L);
        mail.setSubject(title);
        return mail;
    }
}
