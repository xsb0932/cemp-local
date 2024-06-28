package com.landleaf.kafka.conf;

/**
 * topic的定义
 */
public interface TopicDefineConst {
    /**
     * 设备写操作的topic,为device_write_${biz_project_id}
     */
    String DEVICE_WRITE_TOPIC = "device_write_";

    /**
     * 设备服务下行软网关topic：device_service_write_gateway_{bizGatewayId}
     */
    String DEVICE_SERVICE_WRITE_GATEWAY_TOPIC = "device_service_write_gateway_";

    /**
     * 锦江设备智能写操作的topic,为jj_device_intelligence_write_${biz_project_id}
     */
    String JJ_DEVICE_INTELLIGENCE_WRITE_TOPIC = "jj_device_intelligence_write_";

    /**
     * 设备写操作的返回
     */
    String DEVICE_WRITE_ACK_TOPIC = "device_write_ack";

    /**
     * 设备写操作的返回
     */
    String JJ_DEVICE_INTELLIGENCE_WRITE_ACK_TOPIC = "jj_device_intelligence_write_ack";

    /**
     * 设备状态上报的topic,device_status_upload_topic_{gateId}
     */
    String DEVICE_STATUS_UPLOAD_TOPIC = "device_status_upload_topic_";

    /**
     * 设备状态上报的topic的ack,device_status_upload_topic_ack_{gateId}
     */
    String DEVICE_STATUS_UPLOAD_TOPIC_ACK = "device_status_write_topic_";
}
