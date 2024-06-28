package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.dto.ManagementNodeTreeDTO;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.enums.BmsConstants;
import com.landleaf.bms.domain.enums.ErrorCodeConstants;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.bms.domain.request.DeviceManagerAttributeHistoryRequest;
import com.landleaf.bms.domain.request.DeviceManagerPageRequest;
import com.landleaf.bms.domain.request.DeviceManagerServiceControlRequest;
import com.landleaf.bms.domain.request.FunctionParameterRequest;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.base.bo.FunctionParameter;
import com.landleaf.comm.base.bo.ValueDescription;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.dto.DeviceHistoryDTO;
import com.landleaf.data.api.device.dto.HistoryQueryInnerDTO;
import com.landleaf.monitor.api.HistoryEventApi;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryPageDTO;
import com.landleaf.monitor.api.dto.DeviceServiceEventAddDTO;
import com.landleaf.monitor.api.request.DeviceManagerEventExportRequest;
import com.landleaf.monitor.api.request.DeviceManagerEventPageRequest;
import com.landleaf.monitor.api.request.DeviceServiceEventAddRequest;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.landleaf.bms.constance.ValueConstance.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceManagerService {
    private final UserNodeMapper userNodeMapper;
    private final ManagementNodeMapper managementNodeMapper;
    private final ProjectSpaceMapper projectSpaceMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryCatalogueMapper categoryCatalogueMapper;
    private final ProjectService projectService;
    private final DeviceIotMapper deviceIotMapper;
    private final DeviceCurrentApi deviceCurrentApi;
    private final DeviceHistoryApi deviceHistoryApi;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final DeviceParameterDetailMapper deviceParameterDetailMapper;
    private final HistoryEventApi historyEventApi;
    private static final String SERVICE_EVENT_TEMPLATE = "{}用户执行了 {} 服务，执行参数：{}";

    /**
     * 获取当前用户设备管理分组节点树
     *
     * @return DeviceManagerNodeTreeResponse
     */
    public DeviceManagerNodeTreeResponse getDeviceManagerNodeTree() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
        // 当前用户的节点权限
        List<UserNodeEntity> userNodeEntityList = userNodeMapper.selectList(
                new LambdaQueryWrapper<UserNodeEntity>().eq(UserNodeEntity::getUserId, userId)
        );
        if (CollectionUtils.isEmpty(userNodeEntityList)) {
            return null;
        }
        // 获取管理节点
        List<Long> nodeIds = userNodeEntityList.stream().map(UserNodeEntity::getNodeId).toList();
        Short type = userNodeEntityList.get(0).getType();
        List<Long> projectIds = new ArrayList<>();
        // 满足权限的节点列表
        Map<String, List<ManagementNodeTreeDTO>> managementNodeTreeMap = new HashMap<>();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            managementNodeTreeMap = managementNodeMapper.recursiveUpManagementNodeTreeDTOByIds(nodeIds)
                    .stream()
                    .peek(node -> Optional.ofNullable(node.getProjectId()).ifPresent(projectIds::add))
                    .collect(Collectors.groupingBy(ManagementNodeTreeDTO::getParentBizNodeId));
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            List<Long> downNodeIds = managementNodeMapper.recursiveDownListByIds(nodeIds)
                    .stream()
                    .map(ManagementNodeEntity::getId)
                    .toList();
            managementNodeTreeMap = managementNodeMapper.recursiveUpManagementNodeTreeDTOByIds(downNodeIds)
                    .stream()
                    .peek(node -> Optional.ofNullable(node.getProjectId()).ifPresent(projectIds::add))
                    .collect(Collectors.groupingBy(ManagementNodeTreeDTO::getParentBizNodeId));
        }
        // 获取项目分区节点
        Map<Long, Map<Long, List<ProjectSpaceEntity>>> projectSpaceTreeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(projectIds)) {
            projectSpaceTreeMap = projectSpaceMapper.selectList(
                            new LambdaQueryWrapper<ProjectSpaceEntity>()
                                    .in(ProjectSpaceEntity::getProjectId, projectIds)
                                    .orderByAsc(ProjectSpaceEntity::getId)
                    ).stream()
                    .collect(Collectors.groupingBy(ProjectSpaceEntity::getProjectId, Collectors.groupingBy(ProjectSpaceEntity::getParentId)));
        }
        // 树转换
        if (CollectionUtil.isEmpty(managementNodeTreeMap.get(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID))) {
            throw new ServiceException(ErrorCodeConstants.ROOT_NODE_NOT_EXIST);
        }
        DeviceManagerNodeTreeResponse rootNode = DeviceManagerNodeTreeResponse.convertToNodeFromNode(managementNodeTreeMap.get(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID).get(0));
        convertToNodeTree(rootNode, managementNodeTreeMap, projectSpaceTreeMap, null, null);
        return rootNode;
    }

    private void convertToNodeTree(DeviceManagerNodeTreeResponse node,
                                   Map<String, List<ManagementNodeTreeDTO>> managementNodeTreeMap,
                                   Map<Long, Map<Long, List<ProjectSpaceEntity>>> projectSpaceTreeMap,
                                   Long projectId,
                                   Long nodeId) {
        if (node.getType() == 0) {
            if (CollectionUtil.isNotEmpty(managementNodeTreeMap.get(node.getNodeId()))) {
                List<DeviceManagerNodeTreeResponse> children = new ArrayList<>();
                managementNodeTreeMap.get(node.getNodeId())
                        .forEach(dto -> {
                            DeviceManagerNodeTreeResponse child;
                            if (Objects.equals(dto.getType(), "00")) {
                                // 项目管理节点
                                child = DeviceManagerNodeTreeResponse.convertToProjectFromNode(dto);
                                convertToNodeTree(child, managementNodeTreeMap, projectSpaceTreeMap, dto.getProjectId(), null);
                            } else {
                                child = DeviceManagerNodeTreeResponse.convertToNodeFromNode(dto);
                                convertToNodeTree(child, managementNodeTreeMap, projectSpaceTreeMap, null, null);
                            }
                            children.add(child);
                        });
                node.setChildren(children);
            }
        } else if (node.getType() == 1) {
            Map<Long, List<ProjectSpaceEntity>> spaceTreeMap = projectSpaceTreeMap.get(projectId);
            if (MapUtil.isNotEmpty(spaceTreeMap) && CollectionUtil.isNotEmpty(spaceTreeMap.get(0L))) {
                Long rootSpaceId = spaceTreeMap.get(0L).get(0).getId();
                if (CollectionUtil.isNotEmpty(spaceTreeMap.get(rootSpaceId))) {
                    List<DeviceManagerNodeTreeResponse> children = new ArrayList<>();
                    spaceTreeMap.get(rootSpaceId).forEach(dto -> {
                        DeviceManagerNodeTreeResponse child = DeviceManagerNodeTreeResponse.convertToProjectFromSpace(dto, node.getNodeId());
                        convertToNodeTree(child, managementNodeTreeMap, projectSpaceTreeMap, projectId, dto.getId());
                        children.add(child);
                    });
                    node.setChildren(children);
                }

            }
        } else if (node.getType() == 2) {
            if (CollectionUtil.isNotEmpty(projectSpaceTreeMap.get(projectId).get(nodeId))) {
                List<DeviceManagerNodeTreeResponse> children = new ArrayList<>();
                projectSpaceTreeMap.get(projectId).get(nodeId).forEach(dto -> {
                    DeviceManagerNodeTreeResponse child = DeviceManagerNodeTreeResponse.convertToProjectFromSpace(dto, node.getNodeId());
                    convertToNodeTree(child, managementNodeTreeMap, projectSpaceTreeMap, projectId, dto.getId());
                    children.add(child);
                });
                node.setChildren(children);
            }
        }
    }

    /**
     * 获取当前用户设备管理产品节点树
     *
     * @return DeviceManagerProductTreeResponse
     */
    public DeviceManagerProductTreeResponse getDeviceManagerProductTree() {
        TenantContext.setIgnore(true);
        Map<String, List<ProductEntity>> productMap = productMapper.selectTenantProductList(TenantContext.getTenantId())
                .stream()
                .collect(Collectors.groupingBy(ProductEntity::getCategoryId));
        if (productMap.isEmpty()) {
            return null;
        }
        List<Long> catalogueIds = new ArrayList<>();
        Map<String, List<CategoryEntity>> categoryMap = categoryMapper.selectList(
                        new LambdaQueryWrapper<CategoryEntity>()
                                .in(CategoryEntity::getBizId, productMap.keySet())
                                .orderByAsc(CategoryEntity::getId)
                ).stream()
                .peek(o -> catalogueIds.add(o.getParentId()))
                .collect(Collectors.groupingBy(category -> category.getParentId().toString()));

        Map<String, List<CategoryCatalogueEntity>> categoryCatalogueMap = categoryCatalogueMapper.recursiveUpCatalogueByIds(catalogueIds)
                .stream()
                .collect(Collectors.groupingBy(catalogue -> catalogue.getParentId().toString()));

        DeviceManagerProductTreeResponse rootNode = DeviceManagerProductTreeResponse.newRootNode();
        convertToProductTree(rootNode, categoryCatalogueMap, categoryMap, productMap);
        return rootNode;
    }

    private void convertToProductTree(DeviceManagerProductTreeResponse node, Map<String, List<CategoryCatalogueEntity>> categoryCatalogueMap, Map<String, List<CategoryEntity>> categoryMap, Map<String, List<ProductEntity>> productMap) {
        List<DeviceManagerProductTreeResponse> children = new ArrayList<>();
        node.setChildren(children);
        if (node.getType() == 0) {
            if (CollectionUtil.isNotEmpty(categoryCatalogueMap.get(node.getNodeId()))) {
                categoryCatalogueMap.get(node.getNodeId())
                        .forEach(dto -> {
                            DeviceManagerProductTreeResponse child = DeviceManagerProductTreeResponse.convertToCatalogue(dto);
                            convertToProductTree(child, categoryCatalogueMap, categoryMap, productMap);
                            children.add(child);
                        });
            }
            if (CollectionUtil.isNotEmpty(categoryMap.get(node.getNodeId()))) {
                categoryMap.get(node.getNodeId())
                        .forEach(dto -> {
                            DeviceManagerProductTreeResponse child = DeviceManagerProductTreeResponse.convertToCategory(dto);
                            convertToProductTree(child, categoryCatalogueMap, categoryMap, productMap);
                            children.add(child);
                        });
            }
        } else if (node.getType() == 1) {
            if (CollectionUtil.isNotEmpty(productMap.get(node.getNodeId()))) {
                productMap.get(node.getNodeId())
                        .forEach(dto -> children.add(DeviceManagerProductTreeResponse.convertToProduct(dto)));

            }
        }
    }

    /**
     * 分页列表查询
     *
     * @param request 查询参数
     * @return Page<DeviceManagerPageResponse>
     */
    public Page<DeviceManagerPageResponse> page(DeviceManagerPageRequest request) {
        // 获取用户有权限的项目业务id
        Collection<String> projectBizIds = projectService.getUserProjectBizIds(LoginUserUtil.getLoginUserId());
        if (CollUtil.isEmpty(projectBizIds)) {
            return Page.of(request.getPageNo(), request.getPageSize());
        }
        TenantContext.setIgnore(true);
        // 分区id
        Collection<Long> areaIds = null;
        // 品类id
        Collection<String> categoryIds = null;
        // 产品id
        String bizProductId = null;
        // 处理查询参数
        if (request.selectByProject()) {
            if (request.getType1() == 0) {
                List<String> selectProjectBizIds = managementNodeMapper.recursiveDownBizProjectIdsByBizNodeId(request.getNodeId1());
                if (projectBizIds.isEmpty()) {
                    return Page.of(request.getPageNo(), request.getPageSize());
                }
                // 取交集
                projectBizIds = CollUtil.intersection(projectBizIds, selectProjectBizIds);
                if (projectBizIds.isEmpty()) {
                    return Page.of(request.getPageNo(), request.getPageSize());
                }
            } else if (request.getType1() == 1) {
                projectBizIds = CollUtil.newArrayList(request.getNodeId1());
            } else if (request.getType1() == 2) {
                areaIds = projectSpaceMapper.recursiveDownById(Long.valueOf(request.getNodeId1()))
                        .stream()
                        .map(ProjectSpaceEntity::getId)
                        .toList();
                if (areaIds.isEmpty()) {
                    return Page.of(request.getPageNo(), request.getPageSize());
                }
            }
        } else if (request.SelectByProduct()) {
            if (request.getType2() == 0 && !BmsConstants.ROOT_CATEGORY_CATALOGUE_ID.equals(request.getNodeId2())) {
                List<Long> catalogueIds = categoryCatalogueMapper.recursiveDownIdsById(Long.valueOf(request.getNodeId2()));
                categoryIds = categoryMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().in(CategoryEntity::getParentId, catalogueIds))
                        .stream()
                        .map(CategoryEntity::getBizId)
                        .toList();
                if (categoryIds.isEmpty()) {
                    return Page.of(request.getPageNo(), request.getPageSize());
                }
            } else if (request.getType2() == 1) {
                categoryIds = CollUtil.newArrayList(request.getNodeId2());
            } else if (request.getType2() == 2) {
                bizProductId = request.getNodeId2();
            }
        }
        List<String> bizDeviceIds = null;
        Map<String, Integer> deviceCstMap = null;
        if (null != request.getCst()) {
            // 获取在线状态的逻辑非常坑。。。
            bizDeviceIds = deviceIotMapper.selectAllBizDeviceIdWhenPage(
                    projectBizIds,
                    areaIds,
                    categoryIds,
                    bizProductId,
                    request.getName()
            );
            deviceCstMap = deviceCurrentApi.getDeviceCstMap(bizDeviceIds).getCheckedData();
            for (Map.Entry<String, Integer> entry : deviceCstMap.entrySet()) {
                if (!Objects.equals(entry.getValue(), request.getCst())) {
                    bizDeviceIds.remove(entry.getKey());
                }
            }
            if (bizDeviceIds.isEmpty()) {
                return Page.of(request.getPageNo(), request.getPageSize());
            }
        }
        // 分页查询
        Page<DeviceManagerPageResponse> result = deviceIotMapper.deviceManagePageQuery(
                Page.of(request.getPageNo(), request.getPageSize()),
                projectBizIds,
                areaIds,
                categoryIds,
                bizProductId,
                request.getName(),
                bizDeviceIds
        );
        // 后置填充在线状态
        if (null == deviceCstMap) {
            deviceCstMap = deviceCurrentApi.getDeviceCstMap(
                    result.getRecords()
                            .stream()
                            .map(DeviceManagerPageResponse::getBizDeviceId)
                            .toList()
            ).getCheckedData();
        }
        for (DeviceManagerPageResponse device : result.getRecords()) {
            device.setCst(deviceCstMap.get(device.getBizDeviceId()));
        }
        return result;
    }

    /**
     * 设备管理-设备详情信息
     *
     * @param bizDeviceId 设备业务id
     * @return DeviceManagerDetailResponse
     */
    public DeviceManagerDetailResponse detail(String bizDeviceId) {
        TenantContext.setIgnore(true);
        DeviceManagerDetailResponse result = deviceIotMapper.detail(bizDeviceId);
        if (null == result) {
            throw new BusinessException("设备不存在");
        }
        // 获取设备通讯状态
        Integer cst = deviceCurrentApi.getDeviceCstStatus(bizDeviceId).getCheckedData();
        result.setCst(cst);
        return result;
    }

    /**
     * 设备管理-运行监控
     *
     * @param bizDeviceId 设备业务id
     * @return DeviceManagerMonitorResponse
     */
    public DeviceManagerMonitorResponse statusMonitor(String bizDeviceId) {
        DeviceIotEntity device = deviceIotMapper.selectOne(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId, bizDeviceId));
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        Long productId = device.getProductId();
        if (null == productId) {
            throw new BusinessException("设备产品id为空");
        }
        TenantContext.setIgnore(true);
        // 获取产品所有属性、参数、控制服务
        List<DeviceManagerMonitorService> services = productDeviceServiceMapper.selectDeviceMonitorServiceListById(productId);
        List<DeviceManagerMonitorAttribute> attributes = productDeviceAttributeMapper.selectDeviceManagerMonitorAttributeListById(productId);
        List<DeviceManagerMonitorProperty> properties = productDeviceParameterMapper.selectDeviceManagerMonitorPropertyListById(productId);
        // 获取设备自定义参数
        Map<String, DeviceParameterDetailEntity> deviceParameters = deviceParameterDetailMapper.selectList(
                        new LambdaQueryWrapper<DeviceParameterDetailEntity>()
                                .eq(DeviceParameterDetailEntity::getBizDeviceId, bizDeviceId)
                ).stream()
                .collect(Collectors.toMap(DeviceParameterDetailEntity::getIdentifier, o -> o, (o1, o2) -> o1));
        // 组装返回参数
        DeviceManagerMonitorResponse result = buildDeviceManagerMonitorResponse(
                services,
                attributes,
                properties,
                deviceParameters
        );
        // 获取属性当前数据
        Map<String, Object> currentMap = deviceCurrentApi.getDeviceCurrentById(bizDeviceId).getCheckedData();
        result.setAttrUploadTime(currentMap.get("uploadTime") == null ? StrUtil.EMPTY : currentMap.get("uploadTime").toString());
        Map<String, DeviceManagerMonitorAttribute> numberAttrCodeMap = new HashMap<>();
        result.getAttributes().forEach(attr -> {
            attr.setValue(currentMap.get(attr.getIdentifier()) == null ? StrUtil.EMPTY : switch (attr.getDataType()) {
                case ENUMERATE -> attr.getValueDescription()
                        .stream()
                        .filter(o -> enumEquals(o.getKey(), currentMap.get(attr.getIdentifier()).toString()))
                        .findAny()
                        .map(ValueDescription::getValue)
                        .orElse(StrUtil.EMPTY);
                case BOOLEAN -> attr.getValueDescription()
                        .stream()
                        .filter(o -> enumEquals(o.getKey(), convertBooleanType(currentMap.get(attr.getIdentifier()).toString())))
                        .findAny()
                        .map(ValueDescription::getValue)
                        .orElse(StrUtil.EMPTY);
                default -> currentMap.get(attr.getIdentifier()).toString();
            });
            if (Objects.equals(INTEGER, attr.getDataType()) || Objects.equals(DOUBLE, attr.getDataType())) {
                numberAttrCodeMap.put(attr.getIdentifier(), attr);
            }
        });
        // 获取数值属性最近1天的数据
        if (CollUtil.isNotEmpty(numberAttrCodeMap)) {
            HistoryQueryInnerDTO query = new HistoryQueryInnerDTO();
            LocalDateTime now = LocalDateTime.now();
            query.setBizProductId(device.getBizProductId())
                    .setBizDeviceIds(bizDeviceId)
                    .setPeriodType(0)
                    .setAttrCode(CollUtil.join(numberAttrCodeMap.keySet(), ","))
                    .setEndTime(LocalDateTimeUtil.format(now, "yyyy-MM-dd HH:mm:ss"))
                    .setStartTime(LocalDateTimeUtil.format(now.minusHours(2L), "yyyy-MM-dd HH:mm:ss"));
            List<DeviceHistoryDTO> histories = deviceHistoryApi.getDeviceHistory(query).getCheckedData();
            for (DeviceHistoryDTO history : histories) {
                DeviceManagerMonitorAttribute attr = numberAttrCodeMap.get(history.getAttrCode());
                if (null != attr) {
                    attr.setTimes(history.getTimes()).setValues(history.getValues());
                }
            }
        }
        return result;
    }

    private DeviceManagerMonitorResponse buildDeviceManagerMonitorResponse(List<DeviceManagerMonitorService> services,
                                                                           List<DeviceManagerMonitorAttribute> attributes,
                                                                           List<DeviceManagerMonitorProperty> properties,
                                                                           Map<String, DeviceParameterDetailEntity> deviceParameters) {
        DeviceManagerMonitorResponse result = new DeviceManagerMonitorResponse();
        // 处理设备服务
        services.forEach(service -> {
            // 如果是GeneralWritePoints，则修改对应的信息
            if (!Objects.equals("GeneralWritePoints", service.getIdentifier())) {
                return;
            }
            // 通用控制服务属性描述（附加参数）
            List<FunctionParameter> controlPointDesc = new ArrayList<>();
            service.getFunctionParameter()
                    .forEach(parameter -> {
                        if (Objects.equals("SysWritablePoint", parameter.getIdentifier())) {
                            properties.forEach(property -> {
                                // 读写参数
                                if (Objects.equals("02", property.getRw())) {
                                    FunctionParameter obj = new FunctionParameter();
                                    obj.setIdentifier(property.getIdentifier());
                                    obj.setName(property.getFunctionName());
                                    obj.setDataType(property.getDataType());
                                    obj.setValueDescription(property.getValueDescription());
                                    controlPointDesc.add(obj);
                                }
                            });
                            attributes.forEach(attr -> {
                                // 读写属性
                                if (Objects.equals("02", attr.getRw())) {
                                    FunctionParameter obj = new FunctionParameter();
                                    obj.setIdentifier(attr.getIdentifier());
                                    obj.setName(attr.getFunctionName());
                                    obj.setDataType(attr.getDataType());
                                    obj.setValueDescription(attr.getValueDescription());
                                    controlPointDesc.add(obj);
                                }
                            });
                            // 参数类型改为枚举
                            parameter.setDataType(ENUMERATE);
                            // 修改选项描述
                            List<ValueDescription> valueDescription = controlPointDesc.stream()
                                    .map(o -> {
                                        ValueDescription obj = new ValueDescription();
                                        obj.setKey(o.getIdentifier());
                                        obj.setValue(o.getName());
                                        return obj;
                                    }).toList();
                            parameter.setValueDescription(valueDescription);
                        }
                    });
            service.setControlPointDesc(controlPointDesc);
        });
        result.setServices(services);
        // 处理设备属性
        List<DeviceManagerMonitorAttribute> monitorAttrs = attributes.stream()
                // 过滤掉通讯状态
                .filter(attr -> !Objects.equals("CST", attr.getIdentifier()))
                .peek(attr -> attr.setUnit(
                        attr.getValueDescription()
                                .stream()
                                .filter(it -> StrUtil.equals("UNIT", it.getKey()))
                                .findAny()
                                .map(ValueDescription::getValue)
                                .orElse(StrUtil.EMPTY)
                ))
                .sorted(
                        // 先根据类型排，数值类型在前，其他类型在后
                        Comparator.comparing(DeviceManagerMonitorAttribute::getFirstSort)
                                // 再根据id排序
                                .thenComparing(DeviceManagerMonitorAttribute::getSecondSort)
                ).toList();
        result.setAttributes(monitorAttrs);
        // 处理设备参数
        List<DeviceManagerMonitorProperty> monitorProperties = properties.stream()
                // 过滤系统必选参数
                .filter(property -> !Objects.equals("01", property.getFunctionType()))
                .peek(property -> {
                    property.setUnit(property.getValueDescription().stream()
                            .filter(it -> StrUtil.equals("UNIT", it.getKey()))
                            .findAny()
                            .map(ValueDescription::getValue)
                            .orElse(StrUtil.EMPTY));
                    DeviceParameterDetailEntity parameterDetail = deviceParameters.get(property.getIdentifier());
                    if (null == parameterDetail) {
                        property.setValue(StrUtil.EMPTY).setUpdateTime(null);
                    } else {
                        String value = switch (property.getDataType()) {
                            case STRING, INTEGER, DOUBLE -> parameterDetail.getValue();
                            case ENUMERATE -> property.getValueDescription()
                                    .stream()
                                    .filter(o -> StrUtil.equals(o.getKey(), parameterDetail.getValue()))
                                    .findAny()
                                    .map(ValueDescription::getValue)
                                    .orElse(StrUtil.EMPTY);
                            case BOOLEAN -> property.getValueDescription()
                                    .stream()
                                    .filter(o -> StrUtil.equals(o.getKey(), convertBooleanType(parameterDetail.getValue())))
                                    .findAny()
                                    .map(ValueDescription::getValue)
                                    .orElse(StrUtil.EMPTY);
                            default -> StrUtil.EMPTY;
                        };
                        property.setValue(value).setUpdateTime(parameterDetail.getUpdateTime());
                    }
                }).toList();
        result.setProperties(monitorProperties);
        return result;
    }

    private String convertBooleanType(String value) {
        if (NumberUtil.isNumber(value)) {
            int temp = new BigDecimal(value).intValue();
            if (1 == temp) {
                value = BOOLEAN_TRUE_KEY;
            }
            if (0 == temp) {
                value = BOOLEAN_FALSE_KEY;
            }
        }
        return value;
    }

    /**
     * 获取设备测点
     *
     * @param bizDeviceId 设备业务id
     * @return List<DeviceManagerAttributesResponse>
     */
    public List<DeviceManagerAttributesResponse> deviceAttributes(String bizDeviceId) {
        DeviceIotEntity device = deviceIotMapper.selectOne(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId, bizDeviceId));
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        Long productId = device.getProductId();
        if (null == productId) {
            throw new BusinessException("设备产品id为空");
        }
        TenantContext.setIgnore(true);
        return productDeviceAttributeMapper.selectList(
                        new LambdaQueryWrapper<ProductDeviceAttributeEntity>()
                                .eq(ProductDeviceAttributeEntity::getProductId, productId)
                                .orderByAsc(Arrays.asList(BaseEntity::getCreateTime, ProductDeviceAttributeEntity::getIdentifier))
                ).stream()
                .map(o -> new DeviceManagerAttributesResponse()
                        .setAttrCode(o.getIdentifier()).setName(o.getFunctionName())
                ).toList();
    }

    /**
     * 设备管理-历史数据
     *
     * @param request 查询参数
     * @return DeviceManagerAttributeHistoryResponse
     */
    public List<DeviceManagerAttributeHistoryResponse> deviceAttributesHistory(DeviceManagerAttributeHistoryRequest request) {
        DeviceIotEntity device = deviceIotMapper.selectOne(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId, request.getBizDeviceId()));
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        Long productId = device.getProductId();
        if (null == productId) {
            throw new BusinessException("设备产品id为空");
        }
        TenantContext.setIgnore(true);
        List<String> attrCodes = Arrays.asList(request.getAttrCode().split(","));
        Map<String, ProductDeviceAttributeEntity> attrMap = productDeviceAttributeMapper.selectList(
                        new LambdaQueryWrapper<ProductDeviceAttributeEntity>()
                                .eq(ProductDeviceAttributeEntity::getProductId, productId)
                                .in(ProductDeviceAttributeEntity::getIdentifier, attrCodes)
                ).stream()
                .collect(Collectors.toMap(ProductDeviceAttributeEntity::getIdentifier, o -> o));
        if (attrCodes.size() != attrMap.size()) {
            throw new BusinessException("属性查询参数异常");
        }

        List<DeviceManagerAttributeHistoryResponse> result = new ArrayList<>();
        HistoryQueryInnerDTO query = new HistoryQueryInnerDTO()
                .setStartTime(request.getStart())
                .setEndTime(request.getEnd())
                .setBizProductId(device.getBizProductId())
                .setBizDeviceIds(device.getBizDeviceId())
                .setAttrCode(request.getAttrCode())
                .setPeriodType(request.getPeriodType());
        Map<String, DeviceHistoryDTO> historyMap = deviceHistoryApi.getDeviceHistory(query)
                .getCheckedData()
                .stream()
                .collect(Collectors.toMap(DeviceHistoryDTO::getAttrCode, o -> o));
        // 时间轴
        DeviceManagerAttributeHistoryResponse timeColumn = new DeviceManagerAttributeHistoryResponse()
                .setName("时间")
                .setCode("time")
                .setNumberFlag(Boolean.FALSE);
        result.add(timeColumn);
        for (String attrCode : attrCodes) {
            DeviceManagerAttributeHistoryResponse attrColumn = new DeviceManagerAttributeHistoryResponse();
            result.add(attrColumn);

            ProductDeviceAttributeEntity attr = attrMap.get(attrCode);
            attrColumn.setName(attr.getFunctionName())
                    .setCode(attrCode)
                    .setUnit(attr.getValueDescription()
                            .stream()
                            .filter(it -> StrUtil.equals("UNIT", it.getKey()))
                            .findAny()
                            .map(com.landleaf.bms.api.json.ValueDescription::getValue)
                            .orElse(StrUtil.EMPTY))
                    .setNumberFlag(Objects.equals(INTEGER, attr.getDataType()) || Objects.equals(DOUBLE, attr.getDataType()));
            DeviceHistoryDTO history = historyMap.get(attrCode);
            if (null != history) {
                if (null == timeColumn.getData()) {
                    timeColumn.setData(history.getTimes());
                }
                List<String> data = switch (attr.getDataType()) {
                    case ENUMERATE -> history.getValues()
                            .stream()
                            .map(item -> attr.getValueDescription()
                                    .stream()
                                    .filter(o -> enumEquals(o.getKey(), item))
                                    .findAny()
                                    .map(com.landleaf.bms.api.json.ValueDescription::getValue)
                                    .orElse(item)
                            ).toList();
                    case BOOLEAN -> history.getValues()
                            .stream()
                            .map(item -> attr.getValueDescription()
                                    .stream()
                                    .filter(o -> enumEquals(o.getKey(), convertBooleanType(item)))
                                    .findAny()
                                    .map(com.landleaf.bms.api.json.ValueDescription::getValue)
                                    .orElse(item)
                            ).toList();
                    default -> history.getValues();
                };
                attrColumn.setData(data);
            }
        }
        return result;
    }

    private boolean enumEquals(String config, String value) {
        return StrUtil.equals(config, value);
    }

    /**
     * 历史事件分页查询
     *
     * @param request 查询参数
     * @return Page<DeviceManagerEventHistoryResponse>
     */
    public Page<DeviceManagerEventHistoryDTO> deviceEventsHistory(DeviceManagerEventPageRequest request) {
        Response<DeviceManagerEventHistoryPageDTO> response = historyEventApi.deviceEventsHistory(request);
        if (response.isSuccess()) {
            DeviceManagerEventHistoryPageDTO data = response.getResult();
            Page<DeviceManagerEventHistoryDTO> page = Page.of(data.getCurrent(), data.getSize(), data.getTotal());
            page.setRecords(data.getRecords());
            return page;
        }
        return Page.of(request.getPageNo(), request.getPageSize());
    }

    /**
     * 历史事件导出
     *
     * @param request 查询参数
     * @return List<DeviceManagerEventHistoryDTO>
     */
    public List<DeviceManagerEventHistoryDTO> deviceEventsHistoryExport(DeviceManagerEventExportRequest request) {
        Response<List<DeviceManagerEventHistoryDTO>> response = historyEventApi.deviceEventsHistoryExport(request);
        if (response.isSuccess()) {
            return response.getResult();
        }
        return Collections.emptyList();
    }

    /**
     * 获取设备实体
     *
     * @param bizDeviceId 设备业务id
     * @return DeviceIotEntity
     */
    public DeviceIotEntity getDeviceEntityByBizDeviceId(String bizDeviceId) {
        return deviceIotMapper.selectOne(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId, bizDeviceId));
    }

    /**
     * 生产设备服务历史事件参数
     *
     * @param device  设备信息
     * @param request 服务控制参数
     */
    @Async("businessExecutor")
    public void addServiceEvent(Long time,
                                Long tenantId,
                                String username,
                                DeviceIotEntity device,
                                DeviceManagerServiceControlRequest request,
                                Boolean flag) {
        Long productId = device.getProductId();
        if (null == productId) {
            throw new BusinessException("设备产品id为空");
        }
        TenantContext.setIgnore(true);
        DeviceServiceEventAddRequest eventAddRequest = new DeviceServiceEventAddRequest()
                .setTenantId(tenantId)
                .setTime(time)
                .setBizProjectId(device.getBizProjectId())
                .setBizDeviceId(device.getBizDeviceId())
                .setServiceId(request.getIdentifier());
        // 获取产品控制服务 用来生成 服务参数日志内容 ε=(´ο｀*)))唉
        List<DeviceManagerMonitorService> services = productDeviceServiceMapper.selectDeviceMonitorServiceListById(productId);
        String serviceLog = "";
        JSONObject serviceParamLog = JSONUtil.createObj();
        // 处理设备服务
        for (DeviceManagerMonitorService service : services) {
            if (!Objects.equals(request.getIdentifier(), service.getIdentifier())) {
                continue;
            }
            serviceLog = service.getFunctionName();
            if (CollUtil.isNotEmpty(request.getFunctionParameters())) {
                for (FunctionParameterRequest functionParameter : request.getFunctionParameters()) {
                    serviceParamLog.putOpt(functionParameter.getIdentifier(), functionParameter.getValue());
                }
            }
        }
        eventAddRequest.setServiceDesc(StrUtil.format(SERVICE_EVENT_TEMPLATE, username, serviceLog, serviceParamLog));
        Response<DeviceServiceEventAddDTO> response = historyEventApi.addDeviceServiceEvent(eventAddRequest);
        if (!response.isSuccess()) {
            log.error("设备服务下发日志保存失败：{}：{}", eventAddRequest, response.getErrorMsg());
        }
    }
}
