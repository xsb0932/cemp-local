package com.landleaf.bms.context;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.DeviceParameterDetailMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.dal.mapper.ProductProductParameterMapper;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.entity.ProductProductParameterEntity;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.dao.ProductCacheDao;
import com.landleaf.redis.dao.dto.DeviceParameterValueCacheDTO;
import com.landleaf.redis.dao.dto.ProductCacheDTO;
import com.landleaf.redis.dao.dto.ProductProductParameterCacheDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ProductCacheInit implements ApplicationRunner {
    private ProductCacheDao productCacheDao;
    private ProductMapper productMapper;
    private ProductProductParameterMapper productProductParameterMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TenantContext.setIgnore(true);
        List<ProductEntity> productList = productMapper.selectList(Wrappers.emptyWrapper());
        List<ProductProductParameterCacheDTO> productParameterValueCacheDTOList = new ArrayList<>();
        for (ProductEntity prod : productList) {
            ProductCacheDTO productCacheDTO = BeanUtil.copyProperties(prod, ProductCacheDTO.class);
            log.info("[start]>>>初始化产品信息缓存 {}", prod.getBizId());
            productCacheDao.saveProdInfoCache(productCacheDTO);

            // 产品属性
            List<ProductProductParameterEntity> parameterList = productProductParameterMapper.selectList(Wrappers.emptyWrapper());
            for (ProductProductParameterEntity parameter : parameterList) {
                ProductProductParameterCacheDTO cacheDTO = BeanUtil.copyProperties(parameter, ProductProductParameterCacheDTO.class);
                productParameterValueCacheDTOList.add(cacheDTO);
            }
            if (productParameterValueCacheDTOList.isEmpty()) {
                continue;
            }
            productCacheDao.saveProductParameterValueCache(prod.getBizId(), productParameterValueCacheDTOList);
        }
    }
}
