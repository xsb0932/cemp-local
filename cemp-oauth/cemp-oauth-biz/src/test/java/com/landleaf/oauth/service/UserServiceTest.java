package com.landleaf.oauth.service;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.mail.domain.param.UserPasswordMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.dal.mapper.TenantEntityMapper;
import com.landleaf.oauth.dal.mapper.UserEntityMapper;
import com.landleaf.oauth.domain.entity.TenantEntity;
import com.landleaf.oauth.domain.request.UserTabulationRequest;
import com.landleaf.oauth.domain.response.UserTabulationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UserServiceTest
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@SpringBootTest
class UserServiceTest {
    @Autowired
    UserEntityMapper userEntityMapper;
    @Autowired
    MailService mailService;
    @Autowired
    UserService userService;
    @Autowired
    TenantEntityMapper tenantEntityMapper;


    @Test
    void sendMail() {
        mailService.sendMailAsync(UserPasswordMail.resetPassword("hebin@landleaf-tech.com", "hebin@landleaf-tech.com",
                "15150554347", "123456"));
    }

    @Test
    void addUser() {
        TenantContext.setIgnore(true);
        IPage<UserTabulationResponse> userTabulationResponseIPage = userService.searchUsers(new UserTabulationRequest());
        System.out.println(JSON.toJSONString(userTabulationResponseIPage));
    }

    @Test
    void addUser11() {
        boolean exists = tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                .eq(TenantEntity::getCode, "34234213")
                .ne(Objects.nonNull(null), TenantEntity::getId, null)
        );
        System.out.println(exists);
    }


    public static void main(String[] args) {
        boolean numeric = CharSequenceUtil.isNumeric("2.2");
        System.out.println(numeric);
        final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher matcher = NUMERIC_PATTERN.matcher("2.52");
        System.out.println(matcher.matches());
    }

}
