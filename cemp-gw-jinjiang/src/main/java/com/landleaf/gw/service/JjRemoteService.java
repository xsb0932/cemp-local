package com.landleaf.gw.service;

import com.landleaf.gw.domain.dto.DeviceControlDTO;
import com.landleaf.gw.domain.dto.DeviceIntelligenceControlDTO;

public interface JjRemoteService {
    /**
     * 获取token，成功返回true,否则返回false
     *
     * @return
     */
    boolean freshToken();

    /**
     * 获取空调信息
     * @param times
     * @return
     */
    boolean getAirConditionList(int times);

    /**
     * 获取token
     *
     * @return
     */
    String getToken();

    boolean writeCmd(DeviceControlDTO cmd, String topic);

    /**
     * 智能下发
     * @param cmd
     * @return
     */
    boolean writeIntelligenceCmd(DeviceIntelligenceControlDTO cmd, int times);
}
