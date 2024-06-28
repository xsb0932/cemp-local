package com.landleaf.bms.api;

import cn.hutool.json.JSONUtil;
import com.landleaf.bms.api.dto.ProjectAreaProjectsDetailResponse;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.comm.base.pojo.Response;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ProjectApiTest
 *
 * @author 张力方
 * @since 2023/6/26
 **/
@SpringBootTest
class ProjectApiTest {

    @Autowired
    ProjectApi projectApi;

    @Resource
    private GatewayApi gatewayApi;

    @Test
    public void testProjectInfo() {
        long t1 = System.nanoTime();
        Response<List<ProjectAreaProjectsDetailResponse>> resp = projectApi.getAreaProjectIds("N00001210");
        System.out.println(System.nanoTime() - t1);
    }

    @Test
    public void testGatewayApi() {
        System.out.println(JSONUtil.toJsonStr(gatewayApi.findBizIdByProjAndProdId("PJ00001021", "PK00001052")));
    }

    @Test
    void testProjectDetails() {
        Response<ProjectDetailsResponse> projectDetails = projectApi.getProjectDetails("PJ00000001");
        System.out.println(projectDetails.getResult());
    }
}
