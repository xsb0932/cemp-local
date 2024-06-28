package com.landleaf.bms.dal.mqtt;

import lombok.Data;

/**
 * AclRequest
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@Data
public class AclClientIdRequest {

    private String clientid;
    private String topic;
    private String action;
    private String access;
}
