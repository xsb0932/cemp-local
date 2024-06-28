package com.landleaf.monitor.service;

import com.landleaf.monitor.domain.dto.DeviceControlDTO;

/**
 * 设备写入的逻辑接口定义
 */
public interface DeviceWriteService {
    /**
     * 写入控制
     *
     * @param cmd
     * @return 返回控制结果
     */
    boolean writeCmd(DeviceControlDTO cmd);

    /**
     * 返回写操作结果
     * @param msgId
     */
    void cmdAck(String msgId);
}
