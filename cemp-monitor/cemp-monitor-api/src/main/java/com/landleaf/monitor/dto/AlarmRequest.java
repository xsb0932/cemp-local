package com.landleaf.monitor.dto;

import lombok.Data;

import java.util.List;

/**
 * 告警响应对象
 *
 * @author 张力方
 * @since 2023/8/22
 **/
@Data
public class AlarmRequest {
    private List<String> deviceBizIds;
}
