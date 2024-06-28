package com.landleaf.bms.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.bms.domain.request.OperateLogQueryRequest;
import com.landleaf.bms.domain.response.OperateLogResponse;

/**
 * OperateLogService
 *
 * @author xushibai
 * @since 2024/4/25
 **/
public interface OperateLogService {


    /**
     * 分页查询日志
      * @param request
     * @return
     */
    IPage<OperateLogResponse> list(OperateLogQueryRequest request);

}
