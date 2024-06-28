package com.landleaf.bms.service;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.landleaf.bms.api.ProductApiImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductApiImplTest {

    @Resource
    private ProductApiImpl productApiImpl;

    @Test
    public void testProdAttrGet() {
        System.out.println(JSONUtil.toJsonStr(productApiImpl.getProductAttrsMapByProdId(Lists.newArrayList("PK00000001"))));
    }
}
