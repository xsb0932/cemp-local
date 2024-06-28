package com.landleaf.mail.domain.param;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

/**
 * 锦江报表推送邮件
 *
 * @author Yang
 */
@Data
public class JjgjReportPushMail extends AbstractMail {
    @Override
    public String format() {
        return getMailFormat().formatted(getImage());
    }

    /**
     * 周报表推送
     *
     * @param to 目标邮箱
     * @return 结果
     */
    public static AbstractMail weekMail(String to, String filename, InputStreamSource attachment) {
        JjgjReportPushMail mail = new JjgjReportPushMail();
        mail.setTo(to);
        mail.setAttachmentName(filename);
        mail.setAttachmentResource(attachment);
        mail.setImage(IMAGE_BASE64);
        mail.setMailFormatType(5L);
        mail.setSubject("周项目报表");
        return mail;
    }

    /**
     * 月报表推送
     *
     * @param to 目标邮箱
     * @return 结果
     */
    public static AbstractMail monthMail(String to, String filename, InputStreamSource attachment) {
        JjgjReportPushMail mail = new JjgjReportPushMail();
        mail.setTo(to);
        mail.setAttachmentName(filename);
        mail.setAttachmentResource(attachment);
        mail.setImage(IMAGE_BASE64);
        mail.setMailFormatType(5L);
        mail.setSubject("月项目报表");
        return mail;
    }
}
