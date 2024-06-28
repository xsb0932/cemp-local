package com.landleaf.bms.service;

import com.landleaf.bms.api.DictApiImpl;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.StaTenantDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * DictTest
 *
 * @author 张力方
 * @since 2023/8/28
 **/
@SpringBootTest
class DictTest {

    @Autowired
    DictApiImpl dictApiImpl;
    @Autowired
    TenantApi tenantApi;

    @Test
    void initTenantDictData() {
        TenantContext.setIgnore(true);
        Response<List<StaTenantDTO>> listResponse = tenantApi.listStaJobTenant();
        for (StaTenantDTO staTenantDTO : listResponse.getResult()) {
            dictApiImpl.initTenantDictData(staTenantDTO.getId());
        }
    }
}
