package com.landleaf.gw.service;

public interface LgcDeviceService {
    /**
     * 根据供应商和外部设备编号，获取内部编号
     *
     * @param supplierId
     * @param outerDeviceId
     * @return
     */
    String getBizDeviceIdBySupplierAndOuterId(String supplierId, String outerDeviceId);

    /**
     * 根据bizDeviceid，获取bizProdId，此方法理论上仅用于抄表数据
     *
     * @param bizDeviceId
     * @return
     */
    String getBizProdIdByBizDeviceId(String bizDeviceId);

    /**
     * 根据bizDevcieid，获取外部设备编号
     * @param bizDeviceId
     * @return
     */
    String getOuterIdByBizDeviceId(String bizDeviceId);
}
