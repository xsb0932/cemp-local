package com.landleaf.mail.domain.param;

import lombok.Data;

/**
 * 用户口令邮件
 *
 * @author yue lin
 * @since 2023/7/26 15:32
 */
@Data
public class UserCodeMail extends AbstractMail{

    /**
     * 忘记密码口令
     */
    private String code;

    @Override
    public String format() {
        return getMailFormat().formatted(code, getImage());
    }

    /**
     * 忘记密码口令邮件
     *
     * @param to       目标邮箱
     * @param code      口令
     * @return 结果
     */
    public static AbstractMail forgotPasswordCode(String to, String code) {
        UserCodeMail userCodeMail = new UserCodeMail();
        userCodeMail.setTo(to);
        userCodeMail.setImage(IMAGE_BASE64);
        userCodeMail.setMailFormatType(4L);
        userCodeMail.setSubject("忘记密码-口令");
        userCodeMail.setCode(code);
        return userCodeMail;
    }

}
