package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.dal.mapper.AlarmPushConditionMapper;
import com.landleaf.bms.domain.entity.AlarmPushConditionEntity;
import com.landleaf.bms.service.AlarmPushConditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 告警推送条件的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushConditionServiceImpl extends ServiceImpl<AlarmPushConditionMapper, AlarmPushConditionEntity> implements AlarmPushConditionService {

}