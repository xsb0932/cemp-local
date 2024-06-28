package com.landleaf.monitor.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceManagerEventHistoryPageDTO {
    private long total = 0;
    private long size = 10;
    private long current = 1;
    private List<DeviceManagerEventHistoryDTO> records;

    public long getPages() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }
}
