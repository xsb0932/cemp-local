package com.landleaf.operatelog.core.service;

import com.landleaf.operatelog.core.dal.OperateLogEntity;
import com.landleaf.operatelog.core.dal.OperateLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志 Framework Service 接口
 *
 * @author 粒方
 */
@Service
@RequiredArgsConstructor
public class OperateLogFrameworkService {

    private final OperateLogMapper operateLogMapper;

    /**
     * 异步记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    @Async
    public void createOperateLog(OperateLogEntity operateLog) {
        operateLogMapper.insert(operateLog);
    }

}
