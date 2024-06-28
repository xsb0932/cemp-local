package com.landleaf.oauth.password;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.landleaf.oauth.domain.entity.UserEntity;

/**
 * 密码工具类
 *
 * @author yue lin
 * @since 2023/6/15 14:39
 */
public class PasswordUtil {

    /**
     * 随机生成用户密码，并返回加密前的用户密码
     * @param userEntity    用户实体
     * @return  加密前用户密码
     */
    public static String generatePassword(UserEntity userEntity) {
        Assert.notNull(userEntity, "用户不能为空");
        Assert.notBlank(userEntity.getSalt(), "密码盐不能为空");
        String plaintextPassword = RandomUtil.randomString(8);
        String md5Password = SecureUtil.md5(userEntity.getSalt() + SecureUtil.md5(plaintextPassword));
        userEntity.setPassword(md5Password);
        return plaintextPassword;
    }

}
