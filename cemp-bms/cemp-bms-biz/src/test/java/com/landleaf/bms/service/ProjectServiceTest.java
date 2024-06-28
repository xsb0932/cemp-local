package com.landleaf.bms.service;

import com.alibaba.fastjson2.JSON;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.ProductApiImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ProjectServiceTest
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@SpringBootTest
class ProjectServiceTest {
    @Autowired
    ProjectService projectService;

    @Resource
    ProductApi productApiImpl;

    @Test
    void addProject() {


    }

    @Test
    void queryProd() {
        System.out.println(JSON.toJSONString(productApiImpl.getProductAlarm("PK00000001")));
    }
}
