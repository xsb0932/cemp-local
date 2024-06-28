package com.landleaf.bms.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.bms.dal.mapper.DeviceIotMapper;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign 服务 - 物联平台设备
 *
 * @author xushibai
 * @since 2023/07/30
 **/
@RestController
@RequiredArgsConstructor
public class IotApiImpl implements IotApi {
    private final DeviceIotMapper deviceIotMapper;


    @Override
    public Response<Void> edit(DeviceMonitorVO deviceMonitorVO) {
        TenantContext.setIgnore(true);
        DeviceIotEntity entity = new DeviceIotEntity();
        BeanUtils.copyProperties(deviceMonitorVO,entity);
        Long id = deviceIotMapper.selectOne(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId,deviceMonitorVO.getBizDeviceId())).getId();
        entity.setId(id);
        deviceIotMapper.updateById(entity);
        return Response.success();
    }
}
