package com.landleaf.mail.service;

import com.landleaf.mail.domain.param.AbstractMail;

/**
 * 邮件操作
 *
 * @author yue lin
 * @since 2023/6/14 11:38
 */
public interface MailService {

    /**
     * 邮件发送
     * @param mail 邮件内容
     */
    void sendMail(AbstractMail mail);

    /**
     * 邮件发送（异步）
     * @param mail 邮件内容
     */
    void sendMailAsync(AbstractMail mail);

}
