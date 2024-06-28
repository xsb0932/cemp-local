package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.dal.mapper.AlarmPushUserMapper;
import com.landleaf.bms.domain.entity.AlarmPushUserEntity;
import com.landleaf.bms.service.AlarmPushUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AlarmPushUserEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushUserServiceImpl extends ServiceImpl<AlarmPushUserMapper, AlarmPushUserEntity> implements AlarmPushUserService {

}