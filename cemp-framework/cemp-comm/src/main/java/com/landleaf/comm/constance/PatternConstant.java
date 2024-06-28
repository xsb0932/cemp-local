package com.landleaf.comm.constance;

/**
 * 正则表达式
 *
 * @author yue lin
 * @since 2023/6/21 9:38
 */
public class PatternConstant {

    /**
     * 手机号正则表达式
     */
    public static final String PHONE_PATTERN = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";

    /**
     * 租户code正则表达式
     */
    public static final String TENANT_CODE_PATTERN = "^[a-zA-Z][a-zA-Z0-9]{0,7}$";

}
