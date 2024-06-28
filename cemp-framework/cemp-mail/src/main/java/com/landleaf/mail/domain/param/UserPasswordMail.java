package com.landleaf.mail.domain.param;

import lombok.Data;

/**
 * @author yue lin
 * @since 2023/6/14 16:24
 */
@Data
public class UserPasswordMail extends AbstractMail{


    /**
     * 邮件模板参数-邮箱
     */
    private String email;

    /**
     * 邮件模板参数-手机号
     */
    private String mobile;

    /**
     * 邮件模板参数-密码
     */
    private String password;


    /**
     * 将实体转为邮件文本
     *
     * @return 邮件文本
     */
    @Override
    public String format() {
        return getMailFormat().formatted(email, mobile, password, getImage());
    }
    private UserPasswordMail() {
    }

    /**
     * 用户创建邮件内容
     *
     * @param to       目标邮箱
     * @param email    邮箱
     * @param mobile   手机号
     * @param password 密码
     * @return 结果
     */
    public static AbstractMail userCreate(String to, String email, String mobile, String password) {
        UserPasswordMail userPasswordMail = new UserPasswordMail();
        userPasswordMail.setTo(to);
        userPasswordMail.setEmail(email);
        userPasswordMail.setMobile(mobile);
        userPasswordMail.setPassword(password);
        userPasswordMail.setImage(IMAGE_BASE64);
        userPasswordMail.setMailFormatType(1L);
        userPasswordMail.setSubject("用户创建");
        return userPasswordMail;
    }


    /**
     * 密码重置邮件内容
     *
     * @param to       目标邮箱
     * @param email    邮箱
     * @param mobile   手机号
     * @param password 密码
     * @return 结果
     */
    public static AbstractMail resetPassword(String to, String email, String mobile, String password) {
        UserPasswordMail userPasswordMail = new UserPasswordMail();
        userPasswordMail.setTo(to);
        userPasswordMail.setEmail(email);
        userPasswordMail.setMobile(mobile);
        userPasswordMail.setPassword(password);
        userPasswordMail.setImage(IMAGE_BASE64);
        userPasswordMail.setMailFormatType(2L);
        userPasswordMail.setSubject("重置密码");
        return userPasswordMail;
    }


    /**
     * 租户创建邮件内容
     *
     * @param to       目标邮箱
     * @param email    邮箱
     * @param mobile   手机号
     * @param password 密码
     * @return 结果
     */
    public static AbstractMail tenantCreate(String to, String email, String mobile, String password) {
        UserPasswordMail userPasswordMail = new UserPasswordMail();
        userPasswordMail.setTo(to);
        userPasswordMail.setEmail(email);
        userPasswordMail.setMobile(mobile);
        userPasswordMail.setPassword(password);
        userPasswordMail.setImage(IMAGE_BASE64);
        userPasswordMail.setMailFormatType(3L);
        userPasswordMail.setSubject("租户创建");
        return userPasswordMail;
    }



}
