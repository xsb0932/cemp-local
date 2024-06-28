package com.landleaf.bms.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.bms.domain.request.ManagementNodeAddRequest;
import com.landleaf.bms.domain.request.ManagementNodeAddRootRequest;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.core.BizSequenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * ManagementNodeService
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@SpringBootTest
class ManagementNodeServiceTest {
    @Autowired
    ManagementNodeService managementNodeService;
    @Autowired
    BizSequenceService bizSequenceService;
    @Autowired
    ManagementNodeMapper managementNodeMapper;

    @Test
    void addTestData() {
        TenantContext.setTenantId(2L);
        ManagementNodeAddRootRequest managementNodeAddRootRequest = new ManagementNodeAddRootRequest();
        managementNodeAddRootRequest.setName("xxxx企业");
        managementNodeAddRootRequest.setCode("xxxx");
        String tenantRootNodeBizId = managementNodeService.createTenantRootNode(managementNodeAddRootRequest);

        ManagementNodeAddRequest managementNodeAddRequest1 = new ManagementNodeAddRequest();
        managementNodeAddRequest1.setCode("xxxx-001");
        managementNodeAddRequest1.setName("xxxx企业南京分公司");
        managementNodeAddRequest1.setParentBizNodeId(tenantRootNodeBizId);
//        managementNodeAddRequest1.setType(ManagementNodeTypeEnum.CITY.getType());
        String bizNodeId1 = managementNodeService.addManagementNode(managementNodeAddRequest1);
        ManagementNodeAddRequest managementNodeAddRequest11 = new ManagementNodeAddRequest();
        managementNodeAddRequest11.setCode("xxxx-project-001");
        managementNodeAddRequest11.setName("南京-项目-001");
        managementNodeAddRequest11.setParentBizNodeId(bizNodeId1);
        managementNodeAddRequest11.setType(ManagementNodeTypeEnum.PROJECT.getType());
        managementNodeService.addManagementNode(managementNodeAddRequest11);
        ManagementNodeAddRequest managementNodeAddRequest12 = new ManagementNodeAddRequest();
        managementNodeAddRequest12.setCode("xxxx-project-002");
        managementNodeAddRequest12.setName("南京-项目-002");
        managementNodeAddRequest12.setParentBizNodeId(bizNodeId1);
        managementNodeAddRequest12.setType(ManagementNodeTypeEnum.PROJECT.getType());
        managementNodeService.addManagementNode(managementNodeAddRequest12);
        ManagementNodeAddRequest managementNodeAddRequest13 = new ManagementNodeAddRequest();
        managementNodeAddRequest13.setCode("xxxx-project-003");
        managementNodeAddRequest13.setName("南京-项目-003");
        managementNodeAddRequest13.setParentBizNodeId(bizNodeId1);
        managementNodeAddRequest13.setType(ManagementNodeTypeEnum.PROJECT.getType());
        managementNodeService.addManagementNode(managementNodeAddRequest13);

        ManagementNodeAddRequest managementNodeAddRequest2 = new ManagementNodeAddRequest();
        managementNodeAddRequest2.setCode("xxxx-002");
        managementNodeAddRequest2.setName("xxxx企业苏州分公司");
        managementNodeAddRequest2.setParentBizNodeId(tenantRootNodeBizId);
//        managementNodeAddRequest2.setType(ManagementNodeTypeEnum.CITY.getType());
        String bizNodeId2 = managementNodeService.addManagementNode(managementNodeAddRequest2);
    }

    @Test
    void testAreaUserNode() {
        TenantContext.setIgnore(true);
        List<Long> nodeIds = Arrays.asList(35L, 37L);
        // 权限类型
        Short type = UserNodeTypeEnum.AREA.getType();
        List<Long> resultNodeIds = new ArrayList<>();
        // 区域权限类型处理，只保留最外层权限
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            Set<Long> allCNodeIds = new HashSet<>();
            for (Long nodeId : nodeIds) {
                List<Long> cNodeIds = new ArrayList<>(managementNodeMapper.recursiveDownListByIds(Collections.singletonList(nodeId)).stream().map(ManagementNodeEntity::getId).toList());
                cNodeIds.remove(nodeId);
                allCNodeIds.addAll(cNodeIds);
            }
            for (Long nodeId : nodeIds) {
                if (!allCNodeIds.contains(nodeId)) {
                    resultNodeIds.add(nodeId);
                }
            }
        }
        System.out.println(resultNodeIds);
    }

    @Test
    void testProjectAreaUserNode() {
        TenantContext.setIgnore(true);
        List<Long> nodeIds = Arrays.asList(35L, 37L, 36L, 38L);
        // 权限类型
        Short type = UserNodeTypeEnum.PROJECT.getType();
        List<Long> resultNodeIds = new ArrayList<>();
        // 站点权限类型处理，只认最后一级项目权限
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            List<ManagementNodeEntity> managementNodeEntities = managementNodeMapper.selectList(Wrappers.<ManagementNodeEntity>lambdaQuery()
                    .eq(ManagementNodeEntity::getType, ManagementNodeTypeEnum.PROJECT.getType())
                    .in(ManagementNodeEntity::getId, nodeIds));
            if (!CollectionUtils.isEmpty(managementNodeEntities)) {
                resultNodeIds = managementNodeEntities.stream().map(ManagementNodeEntity::getId).toList();
            }
        }
        System.out.println(resultNodeIds);
    }
}
