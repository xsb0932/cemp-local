package com.landleaf.mail.domain.param;

import lombok.Data;

@Data
public class MsgPushMail extends AbstractMail {
    private String context;

    @Override
    public String format() {
        return getMailFormat().formatted(context, getImage());
    }

    /**
     * 消息推送
     *
     * @param to 目标邮箱
     * @return 结果
     */
    public static AbstractMail mail(String to, String title, String context) {
        MsgPushMail mail = new MsgPushMail();
        mail.setTo(to);
        mail.setContext(context);
        mail.setImage(IMAGE_BASE64);
        mail.setMailFormatType(6L);
        mail.setSubject(title);
        return mail;
    }
}
