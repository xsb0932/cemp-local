package com.landleaf.monitor.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.monitor.dal.mapper.DeviceModeMapper;
import com.landleaf.monitor.domain.dto.DeviceControlDTO;
import org.springframework.stereotype.Service;

import com.landleaf.monitor.domain.entity.DeviceModeEntity;
import com.landleaf.monitor.service.DeviceModeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * DeviceModeEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-09-14
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceModeServiceImpl extends ServiceImpl<DeviceModeMapper, DeviceModeEntity> implements DeviceModeService {


    @Override
    public void setMode(DeviceControlDTO cmd) {
        DeviceModeEntity mode = this.baseMapper.selectOne(new LambdaQueryWrapper<DeviceModeEntity>().eq(DeviceModeEntity::getBizDeviceId,cmd.getBizDeviceId()));
        if(mode != null){
            mode.setModeCode(cmd.getMode());
            this.baseMapper.updateById(mode);
        }else {
            DeviceModeEntity modeNew = new DeviceModeEntity();
            modeNew.setModeCode(cmd.getMode());
            modeNew.setBizDeviceId(cmd.getBizDeviceId());
            this.save(modeNew);
        }


    }

    @Override
    public DeviceModeEntity getMode(String bizDeviceId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<DeviceModeEntity>().eq(DeviceModeEntity::getBizDeviceId,bizDeviceId));
    }

    @Override
    public List<DeviceModeEntity> getModeByCode(String modeCode) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<DeviceModeEntity>().eq(DeviceModeEntity::getModeCode,modeCode));
    }

    @Override
    public List<DeviceModeEntity> getModeByCode(String modeCode, List<String> devices) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<DeviceModeEntity>().eq(DeviceModeEntity::getModeCode,modeCode).in(DeviceModeEntity::getBizDeviceId,devices));
    }
}
