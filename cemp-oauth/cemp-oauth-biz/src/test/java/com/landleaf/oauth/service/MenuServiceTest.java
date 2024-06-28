package com.landleaf.oauth.service;

import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.oauth.domain.response.ModuleMenuTabulationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * MenuServiceTest
 *
 * @author 张力方
 * @since 2023/6/8
 **/
@SpringBootTest
class MenuServiceTest {

    @Autowired
    MenuService menuService;

    @Test
    void test(){
        TenantContext.setTenantId(2L);
        List<ModuleMenuTabulationResponse> moduleMenuTabulationResponses = menuService.searchMenuTabulationByTenant();
        for (ModuleMenuTabulationResponse moduleMenuTabulationRespons : moduleMenuTabulationResponses) {
            System.out.println(moduleMenuTabulationRespons);
        }
    }
}
