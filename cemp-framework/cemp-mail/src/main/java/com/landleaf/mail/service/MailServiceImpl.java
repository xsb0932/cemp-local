package com.landleaf.mail.service;

import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.mail.domain.param.AbstractMail;
import com.landleaf.mail.mapper.MailEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 邮件业务实现
 *
 * @author yue lin
 * @since 2023/6/14 11:39
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javamailSender;
    private final MailEntityMapper mailEntityMapper;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMail(AbstractMail mail) {
        try {
            mail.setMailFormat(mailEntityMapper.searchFormat(mail.getMailFormatType()));
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javamailSender.createMimeMessage(), true);
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setText(mail.format(), true);
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setFrom(from);
            if (null != mail.getAttachmentName() && null != mail.getAttachmentResource()) {
                mimeMessageHelper.addAttachment(mail.getAttachmentName(), mail.getAttachmentResource());
            }
            javamailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("用户【{}】邮件发送失败", mail.getTo(), e);
        }
    }

    @Override
    public void sendMailAsync(AbstractMail mail) {
        try {
            TenantContext.setIgnore(true);
            mail.setMailFormat(mailEntityMapper.searchFormat(mail.getMailFormatType()));
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javamailSender.createMimeMessage(), true);
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setText(mail.format(), true);
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setFrom(from);
            if (null != mail.getAttachmentName() && null != mail.getAttachmentResource()) {
                mimeMessageHelper.addAttachment(mail.getAttachmentName(), mail.getAttachmentResource());
            }
//            CompletableFuture.runAsync(() ->  javamailSender.send(mimeMessageHelper.getMimeMessage()));
            javamailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("用户【{}】邮件发送失败", mail.getTo(), e);
        }
    }
}
