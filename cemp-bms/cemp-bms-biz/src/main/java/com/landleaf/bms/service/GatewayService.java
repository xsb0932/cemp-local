package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.GatewayAddRequest;
import com.landleaf.bms.domain.request.GatewayEditRequest;
import com.landleaf.bms.domain.request.GatewayListRequest;
import com.landleaf.bms.domain.response.*;

import java.util.List;

/**
 * GatewayService
 *
 * @author 张力方
 * @since 2023/8/15
 **/
public interface GatewayService {
    /**
     * 新增MQTT网关
     *
     * @param request 新增网关请求
     */
    void addMqttGateway(GatewayAddRequest request);

    /**
     * 编辑MQTT网关
     *
     * @param request 编辑网关请求
     */
    void editMqttGateway(GatewayEditRequest request);

    /**
     * 删除网关
     *
     * @param bizId 网关业务id
     */
    void deleteGateway(String bizId);

    /**
     * 获取网关详情
     *
     * @param bizId 网关业务id
     * @return GatewayDetailsResponse
     */
    GatewayDetailsResponse getDetails(String bizId);

    /**
     * 分页查询网关列表
     *
     * @param request 网关请求
     * @return 网关列表
     */
    Page<GatewayListResponse> pageQuery(GatewayListRequest request);

    /**
     * 启动前校验JS初始化
     *
     * @param bizId 网关业务id
     * @return Boolean
     */
    Boolean checkJsBeforeStart(String bizId);

    /**
     * 获取网关可选产品
     *
     * @return
     */
    List<ProductEntity> gatewayProducts();

    /**
     * 获取网关上行js
     *
     * @param bizId 网关业务id
     * @return upJs
     */
    String getGatewayUpJs(String bizId);

    /**
     * 获取网关下行js
     *
     * @param bizId 网关业务id
     * @return downJs
     */
    String getGatewayDownJs(String bizId);

    /**
     * 保存网关上行js
     *
     * @param bizId 网关业务id
     * @param upJs  上行js
     */
    void saveGatewayUpJs(String bizId, String upJs);

    /**
     * 保存网关下行js
     *
     * @param bizId  网关业务id
     * @param downJs 下行js
     */
    void saveGatewayDownJs(String bizId, String downJs);

    /**
     * 模拟运行上行js
     *
     * @param bizId   网关业务id
     * @param topic   topic
     * @param payload 消息体
     * @return 转换结果
     */
    SimulateJsResponse simulateGatewayUpJs(String bizId, String topic, String payload);

    /**
     * 模拟运行下行js
     *
     * @param bizId 网关业务id
     * @param cmd   消息体
     * @return 转换结果
     */
    SimulateJsResponse simulateGatewayDownJs(String bizId, String cmd);

    /**
     * 网关产品上行js参考
     *
     * @param bizId 网关业务id
     * @return 产品格式化json
     */
    List<ProductUpPayloadResponse> formatGatewayUpJs(String bizId);

    /**
     * 网关产品下行js参考
     *
     * @param bizId 网关业务id
     * @return 产品格式化json
     */
    List<ProductDownPayloadResponse> formatGatewayDownJs(String bizId);

    /**
     * 启动网关
     *
     * @param bizId 网关业务id
     */
    void startGateway(String bizId);

    /**
     * 停止网关
     *
     * @param bizId 网关业务id
     */
    void stopGateway(String bizId);

    /**
     * 根据网关业务id获取项目信息
     *
     * @param bizId 网关业务id
     * @return GatewayProjectResponse
     */
    GatewayProjectResponse getProjectInfoByBizId(String bizId);

    /**
     * 根据bizProdId和bizProdId获取对应的网关编号列表
     *
     * @param bizProjId
     * @param bizProdId
     * @return
     */
    List<String> findBizIdByProjAndProdId(String bizProjId, String bizProdId);
}
