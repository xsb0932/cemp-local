package com.landleaf.bms.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * DictApiTest
 *
 * @author 张力方
 * @since 2023/6/19
 **/
@SpringBootTest
class DictApiTest {
    @Autowired
    DictApiImpl dictApi;

    @Test
    void testInitTenant() {
        dictApi.initTenantDictData(2L);
    }
}
