package com.landleaf.bms.api;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.dto.DeviceConnResponse;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.dal.mapper.DeviceIotMapper;
import com.landleaf.bms.dal.mapper.DeviceParameterDetailMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceParameterDetailResponse;
import com.landleaf.bms.service.*;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DeviceIotApiImpl
 *
 * @author xushibai
 * @since 2023/7/27
 **/
@RestController
@RequiredArgsConstructor
public class DeviceIotApiImpl implements DeviceIotApi {

    private final DeviceIotMapper deviceIotMapper;

    private final ProductMapper productMapper;

    private final ProductManageService productManageServiceImpl;

    private final DeviceParameterDetailService deviceParameterDetailServiceImpl;

    private final ProductDeviceParameterService productDeviceParameterServiceImpl;

    @Override
    public Response<Void> edit(DeviceMonitorVO deviceMonitorVO) {
        DeviceIotEntity entity = new DeviceIotEntity();
        BeanUtils.copyProperties(deviceMonitorVO, entity);
        deviceIotMapper.updateById(entity);
        return Response.success();
    }

    @Override
    public Response<List<String>> information(String bizDeviceId) {
        DeviceIotEntity entity = deviceIotMapper.selectOne(Wrappers.<DeviceIotEntity>lambdaQuery().eq(DeviceIotEntity::getBizDeviceId, bizDeviceId));
        Assert.notNull(entity, "设备不存在");
        return Response.success(List.of(entity.getBizProjectId(), entity.getBizCategoryId(), entity.getBizProductId()));
    }

    @Override
    public Response<List<DeviceIoResponse>> searchDeviceIot(List<String> bizDeviceIds) {
        List<DeviceIoResponse> ioResponses = deviceIotMapper.selectList(
                        Wrappers.<DeviceIotEntity>lambdaQuery().in(DeviceIotEntity::getBizDeviceId, bizDeviceIds)
                ).stream()
                .map(it -> {
                    DeviceIoResponse response = new DeviceIoResponse();
                    response.setBizDeviceId(it.getBizDeviceId());
                    response.setDeviceName(it.getName());
                    response.setBizProjectId(it.getBizProjectId());
                    response.setProjectName(it.getProjectName());
                    return response;
                }).toList();
        return Response.success(ioResponses);
    }

    @Override
    public Response<DeviceConnResponse> queryBizDeviceIdByOuterId(String gateId, String pkId, String sourceDevId) {
        TenantContext.setIgnore(true);
        String bizDeviceId = deviceIotMapper.queryBizDeviceIdByOuterId(gateId, pkId, sourceDevId);
        if (StringUtils.isEmpty(bizDeviceId)) {
            return Response.success(null);
        }
        DeviceConnResponse resp = new DeviceConnResponse();
        resp.setBizDeviceId(bizDeviceId);
        ProductEntity product = productMapper.selectOne(new LambdaQueryWrapper<ProductEntity>().in(ProductEntity::getBizId, pkId));
        resp.setTimeout(product.getTimeout());
        resp.setProdId(product.getId());
        return Response.success(resp);
    }

    @Override
    @Transactional
    public Response<Boolean> updateDeviceParameterVal(Long tenantId, String projectBizId, String bizProdId, String bizDeviceId, Map<String, String> valMap) {
        // 根据bizDeviceId和对应的identifier,先把数据删了
        TenantContext.setIgnore(true);
        if (MapUtil.isEmpty(valMap)) {
            return Response.success(Boolean.TRUE);
        }
        List<String> identifiers = valMap.keySet().stream().collect(Collectors.toList());
        deviceParameterDetailServiceImpl.deleteByIdentifiers(bizDeviceId, identifiers);

        // 补全数据并新增
        List<DeviceParameterDetailEntity> entityList = Lists.newArrayList();

        // 获取prodId
        ProductEntity prodEntity = productMapper.selectOne(new LambdaQueryWrapper<ProductEntity>().eq(ProductEntity::getBizId, bizProdId));
        Long prodId = prodEntity.getId();

        // 通过prodId,和parameter的identifier,获取functionName
        List<DeviceParameterDetailResponse> parameterList =  productDeviceParameterServiceImpl.listByProduct(String.valueOf(prodId));
        Map<String, String> funNameMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(parameterList)) {
            parameterList.forEach(i->{
                funNameMap.put(i.getIdentifier(), i.getFunctionName());
            });
        }

        valMap.forEach((k, v) -> {
            DeviceParameterDetailEntity temp = new DeviceParameterDetailEntity();
            temp.setTenantId(tenantId);
            temp.setBizDeviceId(bizDeviceId);
            temp.setValue(v);
            temp.setIdentifier(k);
            temp.setProductId(prodId);
            temp.setFunctionName(funNameMap.containsKey(k) ? funNameMap.get(k) : null);
            entityList.add(temp);
        });
        deviceParameterDetailServiceImpl.saveBatch(entityList);
        return Response.success(Boolean.TRUE);
    }
}
