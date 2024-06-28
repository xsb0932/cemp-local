package com.landleaf.messaging.domain;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 设备最后一次通讯情况
 */
@Data
public class DeviceLastCommunicationInfo implements Delayed {

    /**
     * 网关编号
     */
    private String gateId;

    /**
     * 产品编号
     */
    private String pkId;

    /**
     * 设备编号
     */
    private String bizDeviceId;

    /**
     * 最后通讯时间
     */
    private Long lastCommunicationTime;

    /**
     * 保留时常
     */
    private Long retainTime;

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(getLastCommunicationTime() + getRetainTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return this.getLastCommunicationTime() + this.getRetainTime() >
                ((DeviceLastCommunicationInfo) o).getLastCommunicationTime() + ((DeviceLastCommunicationInfo) o).getRetainTime() ? 1 : -1;
    }
}
