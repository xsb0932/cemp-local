package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.dal.mapper.AlarmPushStatusMapper;
import com.landleaf.bms.domain.entity.AlarmPushStatusEntity;
import com.landleaf.bms.service.AlarmPushStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 告警推送方式和对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushStatusServiceImpl extends ServiceImpl<AlarmPushStatusMapper, AlarmPushStatusEntity> implements AlarmPushStatusService {

}