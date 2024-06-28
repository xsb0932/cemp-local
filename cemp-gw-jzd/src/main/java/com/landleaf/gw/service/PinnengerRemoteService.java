package com.landleaf.gw.service;


import org.springframework.stereotype.Service;

public interface PinnengerRemoteService {

    /**
     * 登录
     * @param
     * @return
     */
    String login();

    /**
     * 电站列表
     * @return
     */
    String getStationList();

    /**
     * 设备列表
     * @return
     */
    String getDevList();

    /**
     * 实时数据
     * @return
     */
    String getDevRealKpi(boolean reSend);
}
