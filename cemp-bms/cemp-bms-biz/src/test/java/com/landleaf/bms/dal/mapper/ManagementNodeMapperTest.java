package com.landleaf.bms.dal.mapper;

import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.comm.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * ManagementNodeMapperTest
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@SpringBootTest
class ManagementNodeMapperTest {

    @Autowired
    ManagementNodeMapper managementNodeMapper;

    @Test
    void test() {
        TenantContext.setIgnore(true);
        List<NodeProjectTreeResponse> nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(Arrays.asList(3L, 2L));
        for (NodeProjectTreeResponse nodeProjectTreeRespons : nodeProjectTreeResponses) {
            System.out.println(nodeProjectTreeRespons);
        }
    }
}
