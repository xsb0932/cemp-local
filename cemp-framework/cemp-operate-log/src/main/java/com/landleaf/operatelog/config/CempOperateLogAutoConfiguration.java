package com.landleaf.operatelog.config;

import com.landleaf.operatelog.core.aop.OperateLogAspect;
import com.landleaf.operatelog.core.dal.OperateLogMapper;
import com.landleaf.operatelog.core.dal.OprUserEntityMapper;
import com.landleaf.operatelog.core.service.OperateLogFrameworkService;
import com.landleaf.operatelog.core.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 日志自动化配置类
 *
 * @author 张力方
 * @since 2023/6/13
 **/
@AutoConfiguration
@MapperScan("com.landleaf.operatelog.core.dal")
public class CempOperateLogAutoConfiguration {
    @Bean
    public OperateLogAspect operateLogAspect(OperateLogMapper operateLogMapper, OprUserEntityMapper userEntityMapper) {
        return new OperateLogAspect(operateLogFrameworkService(operateLogMapper),userService(userEntityMapper));
    }

    @Bean
    public OperateLogFrameworkService operateLogFrameworkService(OperateLogMapper operateLogMapper) {
        return new OperateLogFrameworkService(operateLogMapper);
    }

    @Bean
    public UserService userService(OprUserEntityMapper userEntityMapper) {
        return new UserService(userEntityMapper);
    }

}
