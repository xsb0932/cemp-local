package com.landleaf.monitor.service;

import com.landleaf.monitor.domain.request.AlarmConfirmRequest;

/**
 * AlarmConfirmService
 *
 * @author 张力方
 * @since 2023/8/15
 **/
public interface AlarmConfirmService {
    void confirm(AlarmConfirmRequest request);

    /**
     * 系统确定,超过30天未确认的给他确认了
     */
    void sysConfirm();
}
