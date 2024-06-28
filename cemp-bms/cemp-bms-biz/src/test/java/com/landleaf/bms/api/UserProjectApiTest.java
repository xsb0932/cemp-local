package com.landleaf.bms.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * UserProjectApiTest
 *
 * @author 张力方
 * @since 2023/6/8
 **/
@SpringBootTest
class UserProjectApiTest {
    @Autowired
    UserProjectApiImpl userProjectApi;

    @Test
    void getUserProjectBizIds() {
        TenantContext.setIgnore(true);
        Response<List<String>> userProjectBizIds = userProjectApi.getUserProjectBizIds(2L);
        System.out.println(userProjectBizIds);
    }
}
