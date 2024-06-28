package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.bms.constance.ValueConstance;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.dal.mqtt.AclUsernameRequest;
import com.landleaf.bms.dal.mqtt.AuthRequest;
import com.landleaf.bms.dal.mqtt.MqttDao;
import com.landleaf.bms.domain.bo.ProductDownPayloadBO;
import com.landleaf.bms.domain.bo.ProductUpPayloadBO;
import com.landleaf.bms.domain.bo.Topic;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.enums.GatewayJsSimulateStatus;
import com.landleaf.bms.domain.enums.GatewayProtocolTypeEnum;
import com.landleaf.bms.domain.enums.GatewayStatusEnum;
import com.landleaf.bms.domain.request.GatewayAddRequest;
import com.landleaf.bms.domain.request.GatewayEditRequest;
import com.landleaf.bms.domain.request.GatewayListRequest;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.GatewayService;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import com.landleaf.redis.share.GatewayCache;
import com.landleaf.redis.share.ProductAlarmConfCache;
import com.landleaf.redis.share.ProductEventConfCache;
import com.landleaf.redis.share.ProductServiceConfCache;
import com.landleaf.script.CempScriptUtil;
import com.landleaf.script.GwConfigBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GatewayServiceImpl
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {
    /**
     * 软网关服务器地址
     */
    @Value("${cemp.gw.host}")
    private String host;
    /**
     * 软网关服务器ssh端口
     */
    @Value("${cemp.gw.port}")
    private Integer port;
    /**
     * 软网关服务器用户名
     */
    @Value("${cemp.gw.username}")
    private String username;
    /**
     * 软网关服务器密码
     */
    @Value("${cemp.gw.password}")
    private String password;
    /**
     * 配置文件json路径
     */
    @Value("${cemp.gw.jsonFilePath}")
    private String jsonFilePath;
    /**
     * 启动脚本命令
     */
    @Value("${cemp.gw.startCmd}")
    private String startCmdFormat;
    /**
     * 停止脚本命令
     */
    @Value("${cemp.gw.stopCmd}")
    private String stopCmdFormat;

    private final GatewayMapper gatewayMapper;
    private final GatewayJsMapper gatewayJsMapper;
    private final BizSequenceService bizSequenceService;
    private final MqttDao mqttDao;
    private final ProductMapper productMapper;
    private final ProductRefMapper productRefMapper;
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductDeviceEventMapper productDeviceEventMapper;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final ProjectMapper projectMapper;
    private final CempScriptUtil cempScriptUtil;
    private final TenantApi tenantApi;
    private final ProjectService projectService;
    private final GatewayCache gatewayCache;
    private final ProductAlarmConfCache productAlarmConfCache;
    private final ProductEventConfCache productEventConfCache;
    private final ProductServiceConfCache productServiceConfCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMqttGateway(GatewayAddRequest request) {
        // check mqtt username
        if (StrUtil.equals("admin", request.getUsername())) {
            throw new BusinessException("网关用户名不能用admin");
        }
        boolean usernameMatch;
        try {
            TenantContext.setIgnore(true);
            usernameMatch = gatewayMapper.selectList(new LambdaQueryWrapper<GatewayEntity>()
                            .eq(GatewayEntity::getProtocolType, GatewayProtocolTypeEnum.MQTT.getCode()))
                    .stream()
                    .anyMatch(o -> StrUtil.equals(o.getUsername(), request.getUsername()));
        } finally {
            TenantContext.setIgnore(false);
        }
        if (usernameMatch) {
            throw new BusinessException("网关用户名已存在");
        }

        GatewayEntity gatewayEntity = new GatewayEntity();
        BeanUtils.copyProperties(request, gatewayEntity);
        gatewayEntity.setBizId(bizSequenceService.next(BizSequenceEnum.GATEWAY));
        gatewayEntity.setStatus(GatewayStatusEnum.STOP.getCode());
        // 创建用户
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(request.getUsername());
        authRequest.setPassword(request.getPassword());
        // 创建ACL
        List<AclUsernameRequest> aclUsernameRequests = new ArrayList<>();
        // 上行处理
        List<Topic> upTopics = request.getUpTopic();
        for (Topic upTopic : upTopics) {
            if (StrUtil.isBlank(upTopic.getTopic())) {
                continue;
            }
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(request.getUsername());
            aclUsernameRequest.setTopic(upTopic.getTopic());
            aclUsernameRequest.setAction("pub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }
        // 下行处理
        List<Topic> downTopics = request.getDownTopic();
        for (Topic downTopic : downTopics) {
            if (StrUtil.isBlank(downTopic.getTopic())) {
                continue;
            }
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(request.getUsername());
            aclUsernameRequest.setTopic(downTopic.getTopic());
            aclUsernameRequest.setAction("sub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }

        gatewayMapper.insert(gatewayEntity);
        mqttDao.addUser(authRequest);
        mqttDao.addAclUsernameRule(aclUsernameRequests);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editMqttGateway(GatewayEditRequest request) {
        // check mqtt username
        boolean usernameMatch;
        try {
            TenantContext.setIgnore(true);
            usernameMatch = gatewayMapper.selectList(new LambdaQueryWrapper<GatewayEntity>()
                            .eq(GatewayEntity::getProtocolType, GatewayProtocolTypeEnum.MQTT.getCode())
                            .ne(GatewayEntity::getId, request.getId()))
                    .stream()
                    .anyMatch(o -> StrUtil.equals(o.getUsername(), request.getUsername()));
        } finally {
            TenantContext.setIgnore(false);
        }
        if (usernameMatch) {
            throw new BusinessException("网关用户名已存在");
        }

        GatewayEntity gatewayEntity = gatewayMapper.getById(request.getId());
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }
        // check gateway status
        if (!GatewayStatusEnum.STOP.getCode().equals(gatewayEntity.getStatus())) {
            throw new BusinessException("只有停止状态的网关，才可修改");
        }

        deleteMqtt(gatewayEntity);
        BeanUtils.copyProperties(request, gatewayEntity);

        // 创建用户
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(request.getUsername());
        authRequest.setPassword(request.getPassword());
        // 创建ACL
        List<AclUsernameRequest> aclUsernameRequests = new ArrayList<>();
        // 上行处理
        List<Topic> upTopics = request.getUpTopic();
        for (Topic upTopic : upTopics) {
            if (StrUtil.isBlank(upTopic.getTopic())) {
                continue;
            }
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(request.getUsername());
            aclUsernameRequest.setTopic(upTopic.getTopic());
            aclUsernameRequest.setAction("pub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }
        // 下行处理
        List<Topic> downTopics = request.getDownTopic();
        for (Topic downTopic : downTopics) {
            if (StrUtil.isBlank(downTopic.getTopic())) {
                continue;
            }
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(request.getUsername());
            aclUsernameRequest.setTopic(downTopic.getTopic());
            aclUsernameRequest.setAction("sub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }

        gatewayMapper.updateById(gatewayEntity);
        mqttDao.addUser(authRequest);
        mqttDao.addAclUsernameRule(aclUsernameRequests);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGateway(String bizId) {
        GatewayEntity gatewayEntity = gatewayMapper.selectByBizId(bizId);
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }
        // check gateway status
        if (!GatewayStatusEnum.STOP.getCode().equals(gatewayEntity.getStatus())) {
            throw new BusinessException("只有停止状态的网关，才可删除");
        }
        gatewayMapper.deleteById(gatewayEntity);
        gatewayJsMapper.delete(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, gatewayEntity.getBizId()));

        if (GatewayProtocolTypeEnum.MQTT.getCode().equals(gatewayEntity.getProtocolType())) {
            deleteMqtt(gatewayEntity);
        }
    }

    private void deleteMqtt(GatewayEntity gatewayEntity) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(gatewayEntity.getUsername());
        authRequest.setPassword(gatewayEntity.getPassword());
        mqttDao.deleteUser(authRequest);

        List<AclUsernameRequest> aclUsernameRequests = new ArrayList<>();
        // 上行处理
        List<Topic> upTopics = gatewayEntity.getUpTopic();
        for (Topic upTopic : upTopics) {
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(gatewayEntity.getUsername());
            aclUsernameRequest.setTopic(upTopic.getTopic());
            aclUsernameRequest.setAction("pub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }
        // 下行处理
        List<Topic> downTopics = gatewayEntity.getDownTopic();
        for (Topic upTopic : downTopics) {
            AclUsernameRequest aclUsernameRequest = new AclUsernameRequest();
            aclUsernameRequest.setUsername(gatewayEntity.getUsername());
            aclUsernameRequest.setTopic(upTopic.getTopic());
            aclUsernameRequest.setAction("sub");
            aclUsernameRequest.setAccess("allow");
            aclUsernameRequests.add(aclUsernameRequest);
        }
        mqttDao.deleteAclUsernameRule(aclUsernameRequests);
    }

    @Override
    public GatewayDetailsResponse getDetails(String bizId) {
        GatewayDetailsResponse response = gatewayMapper.getDetailByBizId(bizId);
        if (null == response) {
            throw new BusinessException("网关不存在");
        }
        GatewayStatusEnum gatewayStatusEnum = GatewayStatusEnum.fromCode(response.getStatus());
        if (null != gatewayStatusEnum) {
            response.setStatusName(gatewayStatusEnum.getName());
        }
        GatewayProtocolTypeEnum gatewayProtocolTypeEnum = GatewayProtocolTypeEnum.fromCode(response.getProtocolType());
        if (null != gatewayProtocolTypeEnum) {
            response.setProtocolTypeName(gatewayProtocolTypeEnum.getName());
        }
        ProjectDetailsResponse projectDetails = projectMapper.selectProjectDetails(response.getProjectBizId());
        if (null != projectDetails) {
            response.setProjectBizName(projectDetails.getName());
        }
        TenantContext.setIgnore(true);
        try {
            List<ProductEntity> products = productMapper.selectList(new LambdaQueryWrapper<ProductEntity>().in(ProductEntity::getBizId, response.getProductBizIds()));
            response.setProductNames(products.stream().map(ProductEntity::getName).toList());
        } finally {
            TenantContext.setIgnore(false);
        }
        return response;
    }

    @Override
    public Page<GatewayListResponse> pageQuery(GatewayListRequest request) {
        List<String> projectBizIds = projectService.getUserProjectBizIds(LoginUserUtil.getLoginUserId());
        if (CollectionUtil.isEmpty(projectBizIds)) {
            return Page.of(request.getPageNo(), request.getPageSize());
        }
        Page<GatewayListResponse> gatewayListResponsePage = gatewayMapper.pageQuery(Page.of(request.getPageNo(), request.getPageSize()), request, projectBizIds);
        for (GatewayListResponse gatewayListResponse : gatewayListResponsePage.getRecords()) {
            GatewayStatusEnum gatewayStatusEnum = GatewayStatusEnum.fromCode(gatewayListResponse.getStatus());
            if (null != gatewayStatusEnum) {
                gatewayListResponse.setStatusName(gatewayStatusEnum.getName());
            }
            GatewayProtocolTypeEnum gatewayProtocolTypeEnum = GatewayProtocolTypeEnum.fromCode(gatewayListResponse.getProtocolType());
            if (null != gatewayProtocolTypeEnum) {
                gatewayListResponse.setProtocolTypeName(gatewayProtocolTypeEnum.getName());
            }
            ProjectDetailsResponse projectDetails = projectMapper.selectProjectDetails(gatewayListResponse.getProjectBizId());
            if (null != projectDetails) {
                gatewayListResponse.setProjectBizName(projectDetails.getName());
            }
            List<ProductEntity> products = productMapper.selectList(new LambdaQueryWrapper<ProductEntity>().in(ProductEntity::getBizId, gatewayListResponse.getProductBizIds()));
            gatewayListResponse.setProductNames(products.stream().map(ProductEntity::getName).toList());
        }
        return gatewayListResponsePage;
    }

    @Override
    public Boolean checkJsBeforeStart(String bizId) {
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        return null != gatewayJsEntity && (StrUtil.isNotBlank(gatewayJsEntity.getUpJs()) || StrUtil.isNotBlank(gatewayJsEntity.getDownJs()));
    }

    @Override
    public List<ProductEntity> gatewayProducts() {
        TenantContext.setIgnore(true);
        return productMapper.selectTenantProductList(TenantContext.getTenantId());
    }

    @Override
    public String getGatewayUpJs(String bizId) {
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity || StrUtil.isBlank(gatewayJsEntity.getUpJs())) {
            return "function uphandlefunction(topic,payload) {" +
                    "   var obj = null;" +
                    "   return obj;" +
                    "}";
        }
        return gatewayJsEntity.getUpJs();
    }

    @Override
    public String getGatewayDownJs(String bizId) {
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity || StrUtil.isBlank(gatewayJsEntity.getDownJs())) {
            return "function downhandlefunction(cmd) {" +
                    "   var topic = null;" +
                    "   var payload = null;" +
                    "   return {topic, payload};" +
                    "}";
        }
        return gatewayJsEntity.getDownJs();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGatewayUpJs(String bizId, String upJs) {
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity) {
            TenantContext.setIgnore(true);
            gatewayJsEntity = new GatewayJsEntity();
            gatewayJsEntity.setGatewayBizId(bizId)
                    .setUpJs(upJs)
                    .setTenantId(TenantContext.getTenantId())
                    .setCreator(LoginUserUtil.getLoginUserId())
                    .setUpdater(LoginUserUtil.getLoginUserId());
            int row = gatewayJsMapper.insertIfNotExists(gatewayJsEntity);
            if (row != 1) {
                throw new BusinessException("保存失败，请重试");
            }
        } else {
            gatewayJsEntity.setUpJs(upJs);
            gatewayJsMapper.updateById(gatewayJsEntity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGatewayDownJs(String bizId, String downJs) {
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity) {
            TenantContext.setIgnore(true);
            gatewayJsEntity = new GatewayJsEntity();
            gatewayJsEntity.setGatewayBizId(bizId)
                    .setDownJs(downJs)
                    .setTenantId(TenantContext.getTenantId())
                    .setCreator(LoginUserUtil.getLoginUserId())
                    .setUpdater(LoginUserUtil.getLoginUserId());
            int row = gatewayJsMapper.insertIfNotExists(gatewayJsEntity);
            if (row != 1) {
                throw new BusinessException("保存失败，请重试");
            }
        } else {
            gatewayJsEntity.setDownJs(downJs);
            gatewayJsMapper.updateById(gatewayJsEntity);
        }
    }

    @Override
    public SimulateJsResponse simulateGatewayUpJs(String bizId, String topic, String payload) {
        SimulateJsResponse response = new SimulateJsResponse();
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity || StrUtil.isBlank(gatewayJsEntity.getUpJs())) {
            throw new BusinessException("上行js脚本不存在");
        }
        gatewayJsEntity.setLastUpPayload(JSONUtil.createObj().putOpt("topic", topic).putOpt("payload", payload).toString())
                .setLastUpRuntime(LocalDateTime.now());
        try {
            Object obj = cempScriptUtil.simulateGatewayUpJs(gatewayJsEntity.getUpJs(), topic, payload);
            String result = JSONUtil.toJsonStr(obj);
            // 校验转换结构
            boolean success = checkUpJsConvertResult(result);
            response.setResult(JSONUtil.parse(result));
            response.setStatus(success ? GatewayJsSimulateStatus.SUCCESS.getCode() : GatewayJsSimulateStatus.FAILED.getCode());
            gatewayJsEntity.setLastUpResult(result).setLastUpStatus(success ? GatewayJsSimulateStatus.SUCCESS.getCode() : GatewayJsSimulateStatus.FAILED.getCode());
        } catch (ScriptException | NoSuchMethodException e) {
            log.error("模拟运行js脚本异常", e);
            response.setResult("error").setStatus(GatewayJsSimulateStatus.FAILED.getCode());
            gatewayJsEntity.setLastUpResult("error").setLastUpStatus(GatewayJsSimulateStatus.FAILED.getCode());
        }
        gatewayJsMapper.updateById(gatewayJsEntity);
        return response;
    }

    private boolean checkUpJsConvertResult(String result) {
        if (JSONUtil.isTypeJSONObject(result)) {
            ProductUpPayloadBO convertBO = JSONUtil.toBean(result, ProductUpPayloadBO.class);
            return checkUpJsConvertBO(convertBO);
        }
        if (JSONUtil.isTypeJSONArray(result)) {
            JSONArray jsonArray = JSONUtil.parseArray(result);
            boolean flag = true;
            for (JSONObject obj : jsonArray.jsonIter()) {
                ProductUpPayloadBO convertBO = obj.toBean(ProductUpPayloadBO.class);
                boolean singleFlag = checkUpJsConvertBO(convertBO);
                flag = flag && singleFlag;
            }
            return flag;
        }
        return false;
    }

    private boolean checkUpJsConvertBO(ProductUpPayloadBO convertBO) {
        if (StrUtil.isBlank(convertBO.getPkId())) {
            return false;
        }
        TenantContext.setIgnore(true);
        try {
            ProductEntity product = productMapper.selectOne(
                    new LambdaQueryWrapper<ProductEntity>()
                            .eq(ProductEntity::getBizId, convertBO.getPkId())
                            .select(ProductEntity::getId));
            if (null == product) {
                return false;
            }
            ProductUpPayloadBO productBO = new ProductUpPayloadBO();

            productBO.setParameters(new LinkedHashMap<>())
                    .setPropertys(new LinkedHashMap<>())
                    .setEvents(new LinkedHashMap<>())
                    .setServices(new LinkedHashMap<>());

            // 处理参数、属性、事件、服务
            List<ProductDeviceParameterEntity> parameterList = productDeviceParameterMapper.selectList(new LambdaUpdateWrapper<ProductDeviceParameterEntity>().eq(ProductDeviceParameterEntity::getProductId, product.getId()));
            for (ProductDeviceParameterEntity parameter : parameterList) {
                Object value = defaultJsonValue(parameter.getDataType());
                productBO.getParameters().put(parameter.getIdentifier(), value);
            }

            List<ProductDeviceAttributeEntity> attributeList = productDeviceAttributeMapper.selectList(new LambdaQueryWrapper<ProductDeviceAttributeEntity>().eq(ProductDeviceAttributeEntity::getProductId, product.getId()));
            for (ProductDeviceAttributeEntity attribute : attributeList) {
                Object value = defaultJsonValue(attribute.getDataType());
                productBO.getPropertys().put(attribute.getIdentifier(), value);
            }

            List<ProductDeviceEventEntity> eventList = productDeviceEventMapper.selectList(new LambdaQueryWrapper<ProductDeviceEventEntity>().eq(ProductDeviceEventEntity::getProductId, product.getId()));
            for (ProductDeviceEventEntity event : eventList) {
                String identifier = event.getIdentifier();
                List<FunctionParameter> eventParameter = event.getEventParameter();
                LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                productBO.getEvents().put(identifier, secondMap);
                if (CollectionUtil.isEmpty(eventParameter)) {
                    continue;
                }
                for (FunctionParameter functionParameter : eventParameter) {
                    if (StrUtil.equals("devAlarm", identifier)) {
                        secondMap.put(functionParameter.getIdentifier(), Collections.EMPTY_LIST);
                    } else {
                        secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                    }
                }
            }

            List<ProductDeviceServiceEntity> serviceList = productDeviceServiceMapper.selectList(new LambdaQueryWrapper<ProductDeviceServiceEntity>().eq(ProductDeviceServiceEntity::getProductId, product.getId()));
            for (ProductDeviceServiceEntity service : serviceList) {
                String identifier = service.getIdentifier();
                List<FunctionParameter> responseParameter = service.getResponseParameter();
                LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                productBO.getEvents().put(identifier, secondMap);
                if (CollectionUtil.isEmpty(responseParameter)) {
                    continue;
                }
                for (FunctionParameter functionParameter : responseParameter) {
                    secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                }
            }
            // 校验key是否存在产品转换后的模型中
            if (MapUtil.isNotEmpty(convertBO.getParameters())) {
                if (MapUtil.isEmpty(productBO.getParameters()) || !productBO.getParameters().keySet().containsAll(convertBO.getParameters().keySet())) {
                    return false;
                }
            }
            if (MapUtil.isNotEmpty(convertBO.getPropertys())) {
                if (MapUtil.isEmpty(productBO.getPropertys()) || !productBO.getPropertys().keySet().containsAll(convertBO.getPropertys().keySet())) {
                    return false;
                }
            }
            if (MapUtil.isNotEmpty(convertBO.getEvents())) {
                if (MapUtil.isEmpty(productBO.getEvents())) {
                    return false;
                }
                for (Map.Entry<String, LinkedHashMap<String, Object>> event : convertBO.getEvents().entrySet()) {
                    String key = event.getKey();
                    if (!productBO.getEvents().containsKey(key)) {
                        return false;
                    }
                    LinkedHashMap<String, Object> valueMap = event.getValue();
                    if (MapUtil.isEmpty(valueMap)) {
                        continue;
                    }
                    LinkedHashMap<String, Object> productValueMap = productBO.getEvents().get(key);
                    if (MapUtil.isEmpty(productValueMap) || !productValueMap.keySet().containsAll(valueMap.keySet())) {
                        return false;
                    }
                }
            }
            if (MapUtil.isNotEmpty(convertBO.getServices())) {
                if (MapUtil.isEmpty(productBO.getServices())) {
                    return false;
                }
                for (Map.Entry<String, LinkedHashMap<String, Object>> service : convertBO.getServices().entrySet()) {
                    String key = service.getKey();
                    if (!productBO.getServices().containsKey(key)) {
                        return false;
                    }
                    LinkedHashMap<String, Object> valueMap = service.getValue();
                    if (MapUtil.isEmpty(valueMap)) {
                        continue;
                    }
                    LinkedHashMap<String, Object> productValueMap = productBO.getServices().get(key);
                    if (MapUtil.isEmpty(productValueMap) || !productValueMap.keySet().containsAll(valueMap.keySet())) {
                        return false;
                    }
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
        return true;
    }

    @Override
    public SimulateJsResponse simulateGatewayDownJs(String bizId, String cmd) {
        SimulateJsResponse response = new SimulateJsResponse();
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null == gatewayJsEntity || StrUtil.isBlank(gatewayJsEntity.getUpJs())) {
            throw new BusinessException("下行js脚本不存在");
        }
        gatewayJsEntity.setLastDownPayload(cmd)
                .setLastDownRuntime(LocalDateTime.now());
        try {
            Object obj = cempScriptUtil.simulateGatewayDownJs(gatewayJsEntity.getDownJs(), cmd);
            String result = JSONUtil.toJsonStr(obj);
            response.setResult(JSONUtil.parse(result));
            response.setStatus(GatewayJsSimulateStatus.SUCCESS.getCode());
            gatewayJsEntity.setLastDownResult(result).setLastDownStatus(GatewayJsSimulateStatus.SUCCESS.getCode());
        } catch (ScriptException | NoSuchMethodException e) {
            log.error("模拟运行js脚本异常", e);
            response.setResult("error").setStatus(GatewayJsSimulateStatus.FAILED.getCode());
            gatewayJsEntity.setLastDownResult("error").setLastDownStatus(GatewayJsSimulateStatus.FAILED.getCode());
        }
        gatewayJsMapper.updateById(gatewayJsEntity);
        return response;
    }

    @Override
    public List<ProductUpPayloadResponse> formatGatewayUpJs(String bizId) {
        GatewayEntity gatewayEntity = gatewayMapper.selectByBizId(bizId);
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }
        List<ProductUpPayloadResponse> result = new ArrayList<>();

        TenantContext.setIgnore(true);
        try {
            List<ProductEntity> products = productMapper.selectList(
                    new LambdaQueryWrapper<ProductEntity>()
                            .in(ProductEntity::getBizId, gatewayEntity.getProductBizIds())
                            .select(ProductEntity::getId, ProductEntity::getBizId, ProductEntity::getName));

            for (ProductEntity product : products) {
                ProductUpPayloadResponse response = new ProductUpPayloadResponse();
                response.setBizId(product.getBizId()).setName(product.getName());

                ProductUpPayloadBO payload = new ProductUpPayloadBO();
                payload.setPkId(product.getBizId())
                        .setTime(String.valueOf(System.currentTimeMillis()))
                        .setParameters(new LinkedHashMap<>())
                        .setPropertys(new LinkedHashMap<>())
                        .setEvents(new LinkedHashMap<>())
                        .setServices(new LinkedHashMap<>());
                response.setPayload(payload);

                result.add(response);
                // 处理参数、属性、事件、服务
                List<ProductDeviceParameterEntity> parameterList = productDeviceParameterMapper.selectList(new LambdaUpdateWrapper<ProductDeviceParameterEntity>().eq(ProductDeviceParameterEntity::getProductId, product.getId()));
                for (ProductDeviceParameterEntity parameter : parameterList) {
                    Object value = defaultJsonValue(parameter.getDataType());
                    payload.getParameters().put(parameter.getIdentifier(), value);
                }

                List<ProductDeviceAttributeEntity> attributeList = productDeviceAttributeMapper.selectList(new LambdaQueryWrapper<ProductDeviceAttributeEntity>().eq(ProductDeviceAttributeEntity::getProductId, product.getId()));
                for (ProductDeviceAttributeEntity attribute : attributeList) {
                    Object value = defaultJsonValue(attribute.getDataType());
                    payload.getPropertys().put(attribute.getIdentifier(), value);
                }

                List<ProductDeviceEventEntity> eventList = productDeviceEventMapper.selectList(new LambdaQueryWrapper<ProductDeviceEventEntity>().eq(ProductDeviceEventEntity::getProductId, product.getId()));
                for (ProductDeviceEventEntity event : eventList) {
                    String identifier = event.getIdentifier();
                    List<FunctionParameter> eventParameter = event.getEventParameter();
                    LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                    payload.getEvents().put(identifier, secondMap);
                    if (CollectionUtil.isEmpty(eventParameter)) {
                        continue;
                    }
                    for (FunctionParameter functionParameter : eventParameter) {
                        if (StrUtil.equals("devAlarm", identifier)) {
                            secondMap.put(functionParameter.getIdentifier(), Collections.EMPTY_LIST);
                        } else {
                            secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                        }
                    }
                }

                List<ProductDeviceServiceEntity> serviceList = productDeviceServiceMapper.selectList(new LambdaQueryWrapper<ProductDeviceServiceEntity>().eq(ProductDeviceServiceEntity::getProductId, product.getId()));
                for (ProductDeviceServiceEntity service : serviceList) {
                    String identifier = service.getIdentifier();
                    List<FunctionParameter> responseParameter = service.getResponseParameter();
                    LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                    payload.getEvents().put(identifier, secondMap);
                    if (CollectionUtil.isEmpty(responseParameter)) {
                        continue;
                    }
                    for (FunctionParameter functionParameter : responseParameter) {
                        secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                    }
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
        return result;
    }

    @Override
    public List<ProductDownPayloadResponse> formatGatewayDownJs(String bizId) {
        GatewayEntity gatewayEntity = gatewayMapper.selectByBizId(bizId);
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }
        List<ProductDownPayloadResponse> result = new ArrayList<>();

        TenantContext.setIgnore(true);
        try {
            List<ProductEntity> products = productMapper.selectList(
                    new LambdaQueryWrapper<ProductEntity>()
                            .in(ProductEntity::getBizId, gatewayEntity.getProductBizIds())
                            .select(ProductEntity::getId, ProductEntity::getBizId, ProductEntity::getName));
            for (ProductEntity product : products) {
                ProductDownPayloadResponse response = new ProductDownPayloadResponse();
                response.setBizId(product.getBizId()).setName(product.getName());

                ProductDownPayloadBO payload = new ProductDownPayloadBO();
                payload.setPkId(product.getBizId())
                        .setTime(String.valueOf(System.currentTimeMillis()))
                        .setEvents(new LinkedHashMap<>())
                        .setServices(new LinkedHashMap<>());
                response.setPayload(payload);

                result.add(response);
                // 处理事件、服务
                List<ProductDeviceEventEntity> eventList = productDeviceEventMapper.selectList(new LambdaQueryWrapper<ProductDeviceEventEntity>().eq(ProductDeviceEventEntity::getProductId, product.getId()));
                for (ProductDeviceEventEntity event : eventList) {
                    String identifier = event.getIdentifier();
                    List<FunctionParameter> responseParameter = event.getResponseParameter();
                    LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                    payload.getEvents().put(identifier, secondMap);
                    if (CollectionUtil.isEmpty(responseParameter)) {
                        continue;
                    }
                    for (FunctionParameter functionParameter : responseParameter) {
                        secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                    }
                }
                List<ProductDeviceServiceEntity> serviceList = productDeviceServiceMapper.selectList(new LambdaQueryWrapper<ProductDeviceServiceEntity>().eq(ProductDeviceServiceEntity::getProductId, product.getId()));
                for (ProductDeviceServiceEntity service : serviceList) {
                    String identifier = service.getIdentifier();
                    List<FunctionParameter> serviceParameter = service.getServiceParameter();
                    LinkedHashMap<String, Object> secondMap = new LinkedHashMap<>();
                    payload.getServices().put(identifier, secondMap);
                    if (CollectionUtil.isEmpty(serviceParameter)) {
                        continue;
                    }
                    for (FunctionParameter functionParameter : serviceParameter) {
                        secondMap.put(functionParameter.getIdentifier(), defaultJsonValue(functionParameter.getDataType()));
                    }
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
        return result;
    }

    private Object defaultJsonValue(String dataType) {
        if (ValueConstance.STRING.equals(dataType)) {
            return StrUtil.EMPTY;
        }
        if (ValueConstance.INTEGER.equals(dataType)) {
            return 0;
        }
        if (ValueConstance.DOUBLE.equals(dataType)) {
            return 0d;
        }
        if (ValueConstance.BOOLEAN.equals(dataType)) {
            return Boolean.TRUE;
        }
        if (ValueConstance.ENUMERATE.equals(dataType)) {
            return "01";
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startGateway(String bizId) {
        GatewayEntity gatewayEntity = gatewayMapper.selectByBizId(bizId);
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }

        GwConfigBO configBO = new GwConfigBO();
        if (CollectionUtil.isNotEmpty(gatewayEntity.getUpTopic())) {
            Map<String, String> topics = new HashMap<>(gatewayEntity.getUpTopic().size());
            for (Topic topic : gatewayEntity.getUpTopic()) {
                topics.put(topic.getTopic(), topic.getName());
            }
            configBO.setUpTopics(topics);
        }
        if (CollectionUtil.isNotEmpty(gatewayEntity.getDownTopic())) {
            Map<String, String> topics = new HashMap<>(gatewayEntity.getDownTopic().size());
            for (Topic topic : gatewayEntity.getDownTopic()) {
                topics.put(topic.getTopic(), topic.getName());
            }
            configBO.setDownTopics(topics);
        }
        GatewayJsEntity gatewayJsEntity = gatewayJsMapper.selectOne(new LambdaQueryWrapper<GatewayJsEntity>().eq(GatewayJsEntity::getGatewayBizId, bizId));
        if (null != gatewayJsEntity) {
            configBO.setUpJs(gatewayJsEntity.getUpJs()).setDownJs(gatewayJsEntity.getDownJs());
        }
        String configJson = JSONUtil.toJsonPrettyStr(configBO);
        String targetFile = StrUtil.format(jsonFilePath, bizId);
        String startCmd = StrUtil.format(startCmdFormat, bizId);

        // 调用启动脚本（打镜像&启动容器）
        Session session = JschUtil.createSession(host, port, username, password);
        session.setConfig("StrictHostKeyChecking", "no");
        ChannelSftp channelSftp = JschUtil.openSftp(session);
        try {
            // 生成配置文件
            channelSftp.put(new ByteArrayInputStream(configJson.getBytes()), targetFile);
            // 启动容器
            String result = JschUtil.exec(session, startCmd, StandardCharsets.UTF_8);
            log.info("启动网关{}结果\r\n{}", bizId, result);
            if (!StrUtil.contains(result, "start success")) {
                throw new BusinessException("启动网关异常");
            }
        } catch (SftpException e) {
            log.error("写入网关配置文件异常", e);
        } finally {
            JschUtil.close(channelSftp);
            JschUtil.close(session);
        }

        gatewayEntity.setStatus(GatewayStatusEnum.RUNNING.getCode());
        gatewayMapper.updateById(gatewayEntity);
        List<String> bizProdIds = gatewayEntity.getProductBizIds();
        String[] ids = bizProdIds.toArray(new String[]{});
        productAlarmConfCache.clear(ids);
        productEventConfCache.clear(ids);
        productServiceConfCache.clear(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopGateway(String bizId) {
        GatewayEntity gatewayEntity = gatewayMapper.selectByBizId(bizId);
        if (null == gatewayEntity) {
            throw new BusinessException("网关不存在");
        }
        String stopCmd = StrUtil.format(stopCmdFormat, bizId);
        // 调用停止脚本（停止容器&删除容器）
        Session session = JschUtil.createSession(host, port, username, password);
        try {
            // 停止&删除容器
            String result = JschUtil.exec(session, stopCmd, StandardCharsets.UTF_8);
            log.info("停止网关{}结果\r\n{}", bizId, result);
            if (!StrUtil.contains(result, "stop success")) {
                throw new BusinessException("停止网关异常");
            }
        } finally {
            JschUtil.close(session);
        }
        gatewayEntity.setStatus(GatewayStatusEnum.STOP.getCode());
        gatewayMapper.updateById(gatewayEntity);

        // 清除网关缓存
        gatewayCache.clear(gatewayEntity.getBizId());

        // 将网关下的产品和设备也删除

    }

    @Override
    public GatewayProjectResponse getProjectInfoByBizId(String bizId) {
        TenantContext.setIgnore(true);
        try {
            GatewayEntity gatewayEntity = gatewayMapper.selectOne(new LambdaQueryWrapper<GatewayEntity>()
                    .eq(GatewayEntity::getBizId, bizId)
                    .select(GatewayEntity::getBizId, GatewayEntity::getProjectBizId, TenantBaseEntity::getTenantId));
            if (null == gatewayEntity) {
                return null;
            }
            GatewayProjectResponse result = new GatewayProjectResponse();

            ProjectEntity projectEntity = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>()
                    .eq(ProjectEntity::getBizProjectId, gatewayEntity.getProjectBizId())
                    .select(ProjectEntity::getBizProjectId, ProjectEntity::getBizNodeId, ProjectEntity::getParentBizNodeId, ProjectEntity::getName));

            String bizTenantId = tenantApi.getTenantInfo(gatewayEntity.getTenantId())
                    .getCheckedData()
                    .getBizTenantId();

            result.setGatewayBizId(bizId)
                    .setTenantId(gatewayEntity.getTenantId())
                    .setBizTenantId(bizTenantId)
                    .setNodeBizId(projectEntity.getBizNodeId())
                    .setProjectBizId(gatewayEntity.getProjectBizId())
                    .setProjectName(projectEntity.getName())
                    .setParentNodeBizId(projectEntity.getParentBizNodeId());
            return result;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public List<String> findBizIdByProjAndProdId(String bizProjId, String bizProdId) {
        TenantContext.setIgnore(true);
        List<GatewayEntity> bizGateIds = gatewayMapper.selectList(new LambdaQueryWrapper<GatewayEntity>()
                .eq(GatewayEntity::getProjectBizId, bizProjId)
                .apply("jsonb_exists(product_biz_ids::jsonb, '" + bizProdId + "')")
                .eq(GatewayEntity::getStatus, GatewayStatusEnum.RUNNING.getCode())
                .select(GatewayEntity::getBizId));
        if (CollectionUtil.isEmpty(bizGateIds)) {
            return Lists.newArrayList();
        }
        return bizGateIds.stream().map(GatewayEntity::getBizId).collect(Collectors.toList());
    }
}
