package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.*;
import com.landleaf.bms.api.dto.*;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.monitor.dal.mapper.*;
import com.landleaf.monitor.domain.dto.DeviceMonitorAddDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorCurrentDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorQueryDTO;
import com.landleaf.monitor.domain.entity.*;
import com.landleaf.monitor.domain.enums.AlarmConfirmTypeEnum;
import com.landleaf.monitor.domain.enums.AlarmLevelEnum;
import com.landleaf.monitor.domain.enums.AlarmStatusEnum;
import com.landleaf.monitor.domain.enums.AlarmTypeEnum;
import com.landleaf.monitor.domain.request.*;
import com.landleaf.monitor.domain.response.AVueDeviceListResponse;
import com.landleaf.monitor.domain.response.AVueDevicePageResponse;
import com.landleaf.monitor.domain.response.NodeProjectDeviceTreeResponse;
import com.landleaf.monitor.domain.response.NodeSpaceDeviceTreeResponse;
import com.landleaf.monitor.domain.vo.*;
import com.landleaf.monitor.domain.wrapper.DeviceMonitorWrapper;
import com.landleaf.monitor.enums.AlarmObjTypeEnum;
import com.landleaf.monitor.enums.EventTypeEnum;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备-监测平台的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-05
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceMonitorServiceImpl extends ServiceImpl<DeviceMonitorMapper, DeviceMonitorEntity> implements DeviceMonitorService {

    /**
     * 数据库操作句柄
     */
    private final DeviceMonitorMapper deviceMonitorMapper;
    private final DeviceMonitorTableLabelMapper deviceMonitorTableLabelMapper;
    private final DeviceCurrentApi deviceCurrentApi;
    private final UserProjectApi userProjectApi;
    private final CategoryApi categoryApi;
    private final ProductApi productApi;
    private final ProjectSpaceApi spaceApi;
    private final ProjectApi projectApi;
    private final IotApi iotApi;
    private final DeviceParameterMapper deviceParameterMapper;
    private final HistoryEventMapper historyEventMapper;
    private final BizSequenceService bizSequenceService;
    private final AlarmConfirmMapper alarmConfirmMapper;
    private static final String SERVICE_EVENT_TEMPLATE = "{}用户执行了 {} 服务，执行参数：{}";


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceMonitorAddDTO save(DeviceMonitorAddDTO addInfo) {
        DeviceMonitorEntity entity = new DeviceMonitorEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = deviceMonitorMapper.insert(entity);
        Assert.isTrue(0 != effectNum, () -> new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR));
        BeanUtil.copyProperties(entity, addInfo);
        return addInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeviceMonitorAddDTO updateInfo) {
        DeviceMonitorEntity entity = deviceMonitorMapper.selectById(updateInfo.getId());
        Assert.notNull(entity, () -> new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR));
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        deviceMonitorMapper.updateById(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        deviceMonitorMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceMonitorEntity selectById(Long id) {
        DeviceMonitorEntity entity = deviceMonitorMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DeviceMonitorEntity> list(DeviceMonitorQueryDTO queryInfo) {
        return deviceMonitorMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<DeviceMonitorEntity> page(DeviceMonitorQueryDTO queryInfo) {
        return deviceMonitorMapper.selectPage(new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize()), getCondition(queryInfo));
    }

//    private Map<String, ProjectBizCategoryVO> getCategoryMap() {
//        Map<String, ProjectBizCategoryVO> categoryMap = new HashMap<>();
//        ProjectBizCategoryVO vo1 = new ProjectBizCategoryVO();
//        vo1.setCategoryId("PC0001");
//        vo1.setCategoryName("电表");
//        vo1.setCategoryCode("PC0001");
//
//        ProjectBizCategoryVO vo2 = new ProjectBizCategoryVO();
//        vo2.setCategoryId("PC0002");
//        vo2.setCategoryName("燃气表");
//        vo2.setCategoryCode("PC0002");
//
//        ProjectBizCategoryVO vo3 = new ProjectBizCategoryVO();
//        vo3.setCategoryId("PC0003");
//        vo3.setCategoryName("水表");
//        vo3.setCategoryCode("PC0003");
//
//        ProjectBizCategoryVO vo4 = new ProjectBizCategoryVO();
//        vo4.setCategoryId("PC0004");
//        vo4.setCategoryName("红外空调遥控器");
//        vo4.setCategoryCode("PC0004");
//
//        categoryMap.put("PC0001", vo1);
//        categoryMap.put("PC0002", vo2);
//        categoryMap.put("PC0003", vo3);
//        //categoryMap.put("PC0004",vo4);
//        return categoryMap;
//    }

    @Override
    public List<ProjectBizCategoryVO> getProjectBizCategory(DeviceMonitorCurrentDTO qry) {
//        LambdaQueryWrapper<DeviceMonitorEntity> lqw = new LambdaQueryWrapper<>();
//        if (qry.getProjectIds() != null && qry.getProjectIds().size() > 0) {
//            lqw.in(DeviceMonitorEntity::getBizProjectId, qry.getProjectIds());
//        } else {
//            //根据用户id 找到用户所有的项目
//            Long userid = LoginUserUtil.getLoginUserId();
//            Response<List<String>> projectIds = userProjectApi.getUserProjectBizIds(userid);
//            if (projectIds.getResult() != null && projectIds.getResult().size() > 0) {
//                lqw.in(DeviceMonitorEntity::getBizProjectId, projectIds.getResult());
//            } else {
//                return new ArrayList<>();
//            }
//        }
//        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(lqw);
//        Map<String, ProjectBizCategoryVO> categoryMap = this.getCategoryMap();
//        List<ProjectBizCategoryVO> categories = new ArrayList<>();
//
//        List<String> categoryIds = list.stream().map(DeviceMonitorEntity::getBizCategoryId).distinct().toList();
//        for (String categoryId : categoryIds) {
//            if (categoryMap.get(categoryId) != null) {
//                categories.add(categoryMap.get(categoryId));
//            }
//        }
//        return categories;
        //根据用户id 找到用户所有的项目
        if (CollUtil.isEmpty(qry.getProjectIds())) {
            Response<List<String>> response = userProjectApi.getUserProjectBizIds(LoginUserUtil.getLoginUserId());
            if (CollUtil.isNotEmpty(response.getResult())) {
                qry.setProjectIds(response.getResult());
            } else {
                return List.of();
            }
        }

        List<String> categoryBizIds = deviceMonitorMapper.selectList(Wrappers.<DeviceMonitorEntity>lambdaQuery().in(DeviceMonitorEntity::getBizProjectId, qry.getProjectIds()))
                .stream()
                .map(DeviceMonitorEntity::getBizCategoryId)
                .distinct()
                .toList();
        if (CollUtil.isNotEmpty(categoryBizIds)) {
            return categoryApi.searchCategoryByBizId(categoryBizIds)
                    .getResult()
                    .stream()
                    .map(ProjectBizCategoryVO::from)
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<DeviceMonitorTableLabelVo> searchTableLabelList(String categoryBizId) {
        List<DeviceMonitorTableLabelEntity> tableLabelEntityList = deviceMonitorTableLabelMapper.selectListByBizId(categoryBizId);
        if (CollUtil.isEmpty(tableLabelEntityList)) {
            Response<List<CategoryDeviceParameterResponse>> parameterResponse = categoryApi.searchDeviceParameterByBizId(categoryBizId);
            Response<List<CategoryDeviceAttributeResponse>> attributeResponse = categoryApi.searchDeviceAttributeByBizId(categoryBizId);
            Assert.isTrue(parameterResponse.isSuccess(), () -> new BusinessException(parameterResponse.getErrorCode(), parameterResponse.getErrorMsg()));
            Assert.isTrue(attributeResponse.isSuccess(), () -> new BusinessException(attributeResponse.getErrorCode(), attributeResponse.getErrorMsg()));
            List<CategoryDeviceParameterResponse> parameterResult = parameterResponse.getResult();
            List<CategoryDeviceAttributeResponse> attributeResult = attributeResponse.getResult();
            if (CollUtil.isNotEmpty(parameterResult) || CollUtil.isNotEmpty(attributeResult)) {
                AtomicInteger sort = new AtomicInteger(1);
                List<DeviceMonitorTableLabelEntity> total = CollUtil.newArrayList();
                if (CollUtil.isNotEmpty(parameterResult)) {
                    List<DeviceMonitorTableLabelEntity> entities = parameterResult.stream()
                            .map(it -> {
                                DeviceMonitorTableLabelEntity entity = new DeviceMonitorTableLabelEntity();
                                entity.setCategoryBizId(categoryBizId);
                                entity.setFieldKey(it.getIdentifier());
                                entity.setFieldLabel(it.getFunctionName());
                                entity.setFieldShow(true);
                                entity.setSort(sort.getAndIncrement());
                                entity.setTenantId(TenantContext.getTenantId());
                                return entity;
                            }).toList();
                    total.addAll(entities);
                    // 设备参数
                    deviceMonitorTableLabelMapper.insertBatchSomeColumn(entities);
                }
                // 添加设备上报时间信息
                DeviceMonitorTableLabelEntity uploadTimeEntity = new DeviceMonitorTableLabelEntity();
                uploadTimeEntity.setCategoryBizId(categoryBizId);
                uploadTimeEntity.setFieldKey("uploadTime");
                uploadTimeEntity.setFieldLabel("上报时间");
                uploadTimeEntity.setFieldShow(true);
                uploadTimeEntity.setSort(sort.getAndIncrement());
                uploadTimeEntity.setTenantId(TenantContext.getTenantId());
                deviceMonitorTableLabelMapper.insert(uploadTimeEntity);
                if (CollUtil.isNotEmpty(attributeResult)) {
                    List<DeviceMonitorTableLabelEntity> entities = attributeResult.stream()
                            .map(it -> {
                                DeviceMonitorTableLabelEntity entity = new DeviceMonitorTableLabelEntity();
                                entity.setCategoryBizId(categoryBizId);
                                entity.setFieldKey(it.getIdentifier());
                                entity.setFieldLabel(it.getFunctionName());
                                entity.setFieldShow(true);
                                entity.setSort(sort.getAndIncrement());
                                entity.setTenantId(TenantContext.getTenantId());
                                return entity;
                            }).toList();
                    total.addAll(entities);
                    // 设备参数
                    deviceMonitorTableLabelMapper.insertBatchSomeColumn(entities);
                }
                // 设备属性
                return total.stream().map(DeviceMonitorTableLabelVo::from).sorted(Comparator.comparing(DeviceMonitorTableLabelVo::getSort)).toList();
            }
            return List.of();
        }
        return tableLabelEntityList.stream().map(DeviceMonitorTableLabelVo::from).sorted(Comparator.comparing(DeviceMonitorTableLabelVo::getSort)).toList();
    }

    @Override
    public void tableLabelShow(TableLabelShowRequest request) {
        DeviceMonitorTableLabelEntity entity = deviceMonitorTableLabelMapper.selectOne(Wrappers.<DeviceMonitorTableLabelEntity>lambdaQuery()
                .eq(DeviceMonitorTableLabelEntity::getCategoryBizId, request.getCategoryBizId())
                .eq(DeviceMonitorTableLabelEntity::getFieldKey, request.getProp())
        );
        Assert.notNull(entity, () -> new BusinessException("目标不存在"));
        entity.setFieldShow(request.getShow());
        deviceMonitorTableLabelMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tableLabelSort(TableLabelSortRequest request) {
        List<DeviceMonitorTableLabelEntity> entities = deviceMonitorTableLabelMapper.selectListByBizId(request.getCategoryBizId());
        Assert.notNull(entities, "目标不存在");
        String prop = request.getProp();
        String nextProp = request.getNextProp();
        DeviceMonitorTableLabelEntity entity = deviceMonitorTableLabelMapper.selectOneByBizIAadKey(request.getCategoryBizId(), prop);
        Assert.notNull(entity, "目标表头不存在");
        if (CharSequenceUtil.isNotBlank(nextProp)) {
            DeviceMonitorTableLabelEntity nextEntity = deviceMonitorTableLabelMapper.selectOneByBizIAadKey(request.getCategoryBizId(), nextProp);
            Assert.notNull(nextEntity, "下级表头不存在");
            entities.remove(entity);
            entities.add(entities.indexOf(nextEntity), entity);
        } else {
            entities.remove(entity);
            entities.add(entity);
        }

        AtomicInteger sort = new AtomicInteger(1);
        for (DeviceMonitorTableLabelEntity tableLabelEntity : entities) {
            tableLabelEntity.setSort(sort.incrementAndGet());
            deviceMonitorTableLabelMapper.updateById(tableLabelEntity);
        }
    }

    @Override
    public IPage<DeviceMonitorVO> listByProject(DeviceMonitorQueryDTO queryInfo) {
        LambdaQueryWrapper<DeviceMonitorEntity> lqw = new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getProjectId, queryInfo.getProjectId());
        if (StringUtils.isNotBlank(queryInfo.getName())) {
            lqw.like(DeviceMonitorEntity::getName, queryInfo.getName());
        }
        lqw.orderByDesc(DeviceMonitorEntity::getBizDeviceId);
        IPage<DeviceMonitorEntity> entityIPage = deviceMonitorMapper.selectPage(new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize()), lqw);

        IPage<DeviceMonitorVO> page = DeviceMonitorWrapper.builder().pageEntity2VO(entityIPage);
        List<DeviceMonitorVO> devices = page.getRecords();
        if (CollUtil.isNotEmpty(devices)) {
            //其他参数
            List<String> bizDeviceIds = devices.stream().map(DeviceMonitorVO::getBizDeviceId).distinct().collect(Collectors.toList());
            List<DeviceParameterVO> parameterVOS = deviceParameterMapper.selectList(new LambdaQueryWrapper<DeviceParameterEntity>().in(DeviceParameterEntity::getBizDeviceId, bizDeviceIds))
                    .stream()
                    .map(entity -> {
                        DeviceParameterVO parameter = new DeviceParameterVO();
                        BeanUtils.copyProperties(entity, parameter);
                        return parameter;
                    }).toList();
            Map<String, List<DeviceParameterVO>> parameterMap = parameterVOS.stream().collect(Collectors.groupingBy(DeviceParameterVO::getBizDeviceId));
            //产品
            List<String> bizProductIds = devices.stream().map(DeviceMonitorVO::getBizProductId).collect(Collectors.toList());
            List<ProductDetailResponse> products = productApi.getByBizids(bizProductIds).getResult();
            Map<String, String> productMap = products.stream().collect(Collectors.toMap(ProductDetailResponse::getBizId, ProductDetailResponse::getName));
            //空间
            List<String> spaceIds = devices.stream().map(DeviceMonitorVO::getBizAreaId).collect(Collectors.toList());
            List<ProjectSpaceTreeApiResponse> spaces = spaceApi.getByIds(spaceIds).getResult();
            Map<Long, String> spaceMap = spaces.stream().collect(Collectors.toMap(ProjectSpaceTreeApiResponse::getSpaceId, ProjectSpaceTreeApiResponse::getSpaceName));
            devices.forEach(device -> {
                device.setDeviceParameters(parameterMap.get(device.getBizDeviceId()));
                device.setProductName(productMap.get(device.getBizProductId()));
                device.setAreaName(spaceMap.get(Long.valueOf(device.getBizAreaId())));
            });
            page.setRecords(devices);
        }
        return page;
    }

    @Transactional
    @Override
    public void edit(DeviceMonitorVO editInfo) {
        TenantContext.setIgnore(true);
        DeviceMonitorEntity entity = this.getById(editInfo.getId());
//        if (null == entity) {
//            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
//        }
        Assert.notNull(entity, () -> new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR));
        BeanUtil.copyProperties(editInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        //产品
        if (editInfo.getProductId() != null) {
            ProductDetailResponse product = productApi.getProductDetail(editInfo.getProductId()).getResult();
            entity.setBizProductId(product.getBizId());
            entity.setBizCategoryId(product.getCategoryId());
        }
        //项目
        ProjectDetailsResponse project = projectApi.getDetails(editInfo.getProjectId()).getResult();
        entity.setBizProjectId(project.getBizProjectId());
        entity.setProjectName(project.getName());
        this.updateById(entity);

        //设备参数 - 直接更新
        editInfo.getDeviceParameters().forEach(parameter -> {
            DeviceParameterEntity detail = deviceParameterMapper.getParameter(editInfo.getBizDeviceId(), parameter.getIdentifier());
            if (detail != null) {
                detail.setValue(parameter.getValue());
                deviceParameterMapper.updateById(detail);
            } else {
                if (StringUtils.isNotBlank(parameter.getValue())) {
                    //添加设备参数
                    DeviceMonitorEntity device = deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getBizDeviceId, editInfo.getBizDeviceId())).get(0);
                    ProductDeviceParameterResponse productApiParameter = productApi.getParameter(device.getProductId(), parameter.getIdentifier()).getResult();
                    DeviceParameterEntity newEntity = new DeviceParameterEntity();
                    BeanUtil.copyProperties(productApiParameter, newEntity);
                    newEntity.setId(null);
                    newEntity.setTenantId(TenantContext.getTenantId());
                    newEntity.setBizDeviceId(editInfo.getBizDeviceId());
                    newEntity.setValue(parameter.getValue());
                    deviceParameterMapper.insert(newEntity);
                }
            }
        });

        //同步 物联平台
        com.landleaf.monitor.dto.DeviceMonitorVO deviceMonitorDTO = new com.landleaf.monitor.dto.DeviceMonitorVO();
        BeanUtils.copyProperties(entity, deviceMonitorDTO);
        iotApi.edit(deviceMonitorDTO);
    }

    @Override
    public void delete(String bizDeviceId) {
        deviceMonitorMapper.delete(new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getBizDeviceId, bizDeviceId));
    }

    @Override
    public DeviceMonitorEntity getbyDeviceId(String bizDeviceId) {
        return deviceMonitorMapper.selectOne(new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getBizDeviceId, bizDeviceId));
    }

    @Override
    public DeviceMonitorEntity getbyOutId(String outId) {
        return deviceMonitorMapper.selectOne(new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getSourceDeviceId, outId));
    }

    @Override
    public List<ProjectStaDTO> listStaProject(Long tenantId) {
        return deviceMonitorMapper.listStaProject();
    }

    @Override
    public IPage<AVueDevicePageResponse> aVueGetDevices(String bizProjectId, AVueDevicePageRequest request) {
        TenantContext.setIgnore(true);
        try {
            return deviceMonitorMapper.aVueGetDevices(Page.of(request.getPageNo(), request.getPageSize()),
                    bizProjectId,
                    request.getCategoryName(),
                    request.getName(),
                    request.getCode());
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public List<AVueDeviceListResponse> aVueGetDeviceAll(String bizProjectId) {
        TenantContext.setIgnore(true);
        try {
            return deviceMonitorMapper.aVueGetDeviceAll(bizProjectId);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public IPage<JSONObject> getcurrent(DeviceMonitorCurrentDTO query) {
//        //查询 所有项目
//        //Map<String,String> projectMap = bmsRemoteApi.getproject
//        Map<String, String> projectMap = new HashMap<>();
//        projectMap.put("PJ00000001", "锦江体验中心酒店");
//        boolean hasNoProjects = false;
//        IPage<DeviceMonitorEntity> page = new Page<>(query.getPageNo(), query.getPageSize());
//        LambdaQueryWrapper<DeviceMonitorEntity> lqw = new LambdaQueryWrapper<>();
//        if (query.getProjectIds() != null && query.getProjectIds().size() > 0) {
//            lqw.in(DeviceMonitorEntity::getBizProjectId, query.getProjectIds());
//        } else {
//            //根据用户id 找到用户所有的项目
//            Long userid = LoginUserUtil.getLoginUserId();
//            Response<List<String>> projectIds = userProjectApi.getUserProjectBizIds(userid);
//            if (projectIds.getResult() != null && projectIds.getResult().size() > 0) {
//                lqw.in(DeviceMonitorEntity::getBizProjectId, projectIds.getResult());
//            } else {
//                return buildBlankPage();
//            }
//        }
//        if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getName())) {
//            lqw.like(DeviceMonitorEntity::getName, query.getName());
//        }
//        if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getBizCategoryId())) {
//            lqw.like(DeviceMonitorEntity::getBizCategoryId, query.getBizCategoryId());
//        }
        List<String> meterBizDeviceIds = deviceMonitorMapper.selectProjectsMeterBizDeviceIds(query.getProjectIds(), query.getBizCategoryId());
        Page<DeviceMonitorVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<JSONObject> page2 = new Page<>(query.getPageNo(), query.getPageSize());
        //根据用户id 找到用户所有的项目
        if (CollUtil.isEmpty(query.getProjectIds())) {
            Response<List<String>> response = userProjectApi.getUserProjectBizIds(LoginUserUtil.getLoginUserId());
            if (CollUtil.isNotEmpty(response.getResult())) {
                query.setProjectIds(response.getResult());
            } else {
                return page2;
            }
        }
        Page<DeviceMonitorVO> monitorVOPage = deviceMonitorMapper.pageDeviceMonitor(page, query, meterBizDeviceIds);

        List<String> deviceIds = monitorVOPage.getRecords()
                .stream()
                .map(DeviceMonitorVO::getBizDeviceId)
                .distinct()
                .toList();
        if (deviceIds.isEmpty()) {
            return new Page<>(query.getPageNo(), query.getPageSize());
        }
        Map<String, DeviceCurrentDTO> currentMap = deviceCurrentApi.getDeviceCurrent(deviceIds).getResult().stream()
                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, Function.identity()));

        //查询所有的品类-产品
        Response<List<ProductDeviceAttrMapResponse>> response = productApi.getProductAttrsMap(query.getBizCategoryId());
        Map<String, Map<String, String>> descs = response.getResult().stream().collect(Collectors.toMap(
                attr -> String.format("%s_%s", attr.getProductId(), attr.getIdentifier()),
                productDeviceAttributeEntity -> productDeviceAttributeEntity.getValueDescription().stream().collect(Collectors.toMap(ValueDescriptionResponse::getKey, ValueDescriptionResponse::getValue))));

        //判断属性为枚举 data_type: 05 ENUMERATE
        List<String> enumAttrs = productApi.getCategoryEnumAttrs(query.getBizCategoryId()).getResult();
        //根据设备和设备属性 查询所有枚举类型
        if (currentMap != null && currentMap.size() > 0) {
            //设备参数 枚举转换
            currentMap.forEach((bizDeviceId, deviceCurrentDTO) -> {
                Long productId = deviceMonitorMapper.getByBizid(bizDeviceId).getProductId();
                if (enumAttrs != null && enumAttrs.size() > 0) {
                    enumAttrs.stream().forEach(attr -> {
                        if (deviceCurrentDTO.getCurrent().containsKey(attr)) {
                            String origin = String.valueOf(deviceCurrentDTO.getCurrent().getOrDefault(attr, ""));
                            Map<String, Object> current = deviceCurrentDTO.getCurrent();
                            Optional.ofNullable(descs.get(String.format("%s_%s", productId, attr)))
                                    .ifPresentOrElse(o -> current.put(attr, o.getOrDefault(origin, origin)), () -> current.put(attr, origin));
                        }
                    });
                }
                // 将uploadTime转为时间格式 yyyy-MM-dd HH:mm:ss
                if (deviceCurrentDTO.getCurrent().containsKey("uploadTime") && null != deviceCurrentDTO.getCurrent().get("uploadTime")) {
                    // 做转换
                    deviceCurrentDTO.getCurrent().put("uploadTime", DateFormatUtils.format((long) deviceCurrentDTO.getCurrent().get("uploadTime"), "yyyy-MM-dd HH:mm:ss"));
                }
            });
        }

        //空间
        List<String> spaceIds = monitorVOPage.getRecords().stream().map(DeviceMonitorVO::getBizAreaId).distinct().collect(Collectors.toList());
        List<ProjectSpaceTreeApiResponse> spaces = spaceApi.getByIds(spaceIds).getResult();

        Map<Long, ProjectSpaceTreeApiResponse> spaceMap = CollUtil.isNotEmpty(spaces) ?
                spaces.stream().collect(Collectors.toMap(ProjectSpaceTreeApiResponse::getSpaceId, v -> v)) : new HashMap<>();
        //自定义设备属性
        List<DeviceParameterEntity> parameters = deviceParameterMapper.selectList(new LambdaQueryWrapper<DeviceParameterEntity>().in(DeviceParameterEntity::getBizDeviceId, deviceIds));
        Map<String, List<DeviceParameterEntity>> parameterMap = parameters.stream().collect(Collectors.groupingBy(DeviceParameterEntity::getBizDeviceId));
        monitorVOPage.convert(it -> {
            it.setCurrent(currentMap.get(it.getBizDeviceId()));
            it.setBizAreaId(StringUtils.isNotBlank(it.getBizAreaId()) ? spaceMap.getOrDefault(Long.valueOf(it.getBizAreaId()), new ProjectSpaceTreeApiResponse()).getSpaceName() : null);
            return it;
        });
        page2.setRecords(monitorVOPage.getRecords().stream().map(deviceMonitorVO -> {
            byte[] bytes = JSON.toJSONBytes(deviceMonitorVO);
            JSONObject jo = JSON.parseObject(bytes);
            if (!parameterMap.isEmpty()) {
                parameterMap.getOrDefault(deviceMonitorVO.getBizDeviceId(), new ArrayList<DeviceParameterEntity>()).forEach(parameter -> jo.put(parameter.getIdentifier(), parameter.getValue()));
            }
            return jo;
        }).collect(Collectors.toList()));
        page2.setTotal(monitorVOPage.getTotal());
        return page2;

    }

    @Override
    public DeviceMonitorEntity selectByBizDeviceId(String bizDeviceId) {
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().eq(DeviceMonitorEntity::getBizDeviceId, bizDeviceId));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public NodeProjectDeviceTreeResponse getDeviceTree() {
        // 从userProject获取当前的管理节点的树
        Response<NodeProjectTreeDTO> resp = userProjectApi.getCurrentUserProjectTree();
        Assert.isTrue(resp.isSuccess(), () -> new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR));
        NodeProjectTreeDTO dto = resp.getResult();
        NodeProjectDeviceTreeResponse result = BeanUtil.copyProperties(dto, NodeProjectDeviceTreeResponse.class);

        // 从dto获取所有的设备信息
        List<String> bizProjectIds = new ArrayList<>();
        getBizProjectIds(dto, bizProjectIds);

        // 通过bizProjectId,查询设备
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizProjectId, bizProjectIds));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizProjectId));

        // 查询相关产品的设备属性
        List<String> bizProductIds = list.stream().map(DeviceMonitorEntity::getBizProductId).toList();
        Response<Map<String, List<ProductDeviceAttrResponse>>> projectDetails = productApi.getProjectAttrs(bizProductIds);
        assembleDevice2Proj(result, map, projectDetails.getResult());
        return result;
    }

    @Override
    public NodeProjectDeviceTreeResponse getDeviceTreeProduct() {
        // 从userProject获取当前的管理节点的树
        Response<NodeProjectTreeDTO> resp = userProjectApi.getCurrentUserProjectTree();
        Assert.isTrue(resp.isSuccess(), () -> new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR));
        NodeProjectTreeDTO dto = resp.getResult();
        NodeProjectDeviceTreeResponse result = BeanUtil.copyProperties(dto, NodeProjectDeviceTreeResponse.class);

        // 从dto获取所有的设备信息
        List<String> bizProjectIds = new ArrayList<>();
        getBizProjectIds(dto, bizProjectIds);

        // 通过bizProjectId,查询设备
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizProjectId, bizProjectIds));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        //Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizProjectId));
        Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizProductId));

        // 查询相关产品的设备属性
        List<String> bizProductIds = list.stream().map(DeviceMonitorEntity::getBizProductId).distinct().toList();
        Response<Map<String, List<ProductDeviceAttrResponse>>> projectDetails = productApi.getProjectAttrs(bizProductIds);
        assembleDevice2ProjByProduct(result, map, projectDetails.getResult());
        return result;
    }

    @Override
    public NodeProjectDeviceTreeResponse getDeviceTreeV1Product(String bizProjId) {
        // 从userProject获取当前的管理节点的树
        Response<NodeProjectTreeDTO> resp = userProjectApi.getCurrentUserProjectTree();
        Assert.isTrue(resp.isSuccess(), () -> new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR));
        NodeProjectTreeDTO dto = resp.getResult();
        NodeProjectDeviceTreeResponse result = BeanUtil.copyProperties(dto, NodeProjectDeviceTreeResponse.class);

        if (StringUtils.isEmpty(bizProjId)) {
            return result;
        }
        // 从dto获取所有的设备信息
        result = getChildrenByBizProjId(result, bizProjId);
        if (null == result) {
            return result;
        }


        List<String> bizProjectIds = new ArrayList<>();
        bizProjectIds.add(bizProjId);
        // 通过bizProjectId,查询设备
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizProjectId, bizProjectIds));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        // 排除手动抄表的水电气
        List<String> bizCategoryIdList = categoryApi.getBizCategoryIdList(
                CollUtil.newArrayList(DeviceStaCategoryEnum.RQB.getCode(), DeviceStaCategoryEnum.ZNSB.getCode(), DeviceStaCategoryEnum.DB3PH.getCode())
        ).getCheckedData();
        List<String> meterBizDeviceIds = deviceMonitorMapper.selectProjectMeterBizDeviceIds(bizProjId, bizCategoryIdList);
        list = list.stream().filter(o -> !meterBizDeviceIds.contains(o.getBizDeviceId())).toList();


        Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizProductId));

        // 查询相关产品的设备属性
        List<String> bizProductIds = list.stream().map(DeviceMonitorEntity::getBizProductId).distinct().toList();
        Response<Map<String, List<ProductDeviceAttrResponse>>> projectDetails = productApi.getProjectAttrs(bizProductIds);
        assembleDevice2ProjByProduct(result, map, projectDetails.getResult());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tableLabelWidth(TableLabelWidthRequest request) {
        DeviceMonitorTableLabelEntity entity = deviceMonitorTableLabelMapper.selectOneByBizIAadKey(request.getCategoryBizId(), request.getProp());
        Assert.notNull(entity, "目标表头不存在");
        entity.setWidth(request.getWidth());
        deviceMonitorTableLabelMapper.updateById(entity);
    }

    @Override
    @Async("businessExecutor")
    public void addServiceEvent(long time, Long tenantId, String username, DeviceMonitorEntity device, AVueServiceControlRequest request, Boolean isSuccess) {
        try {
            Long productId = device.getProductId();
            if (null == productId) {
                throw new BusinessException("设备产品id为空");
            }
            TenantContext.setIgnore(true);
            // 获取产品控制服务 用来生成 服务参数日志内容 ε=(´ο｀*)))唉
            List<ProductDeviceServiceListResponse> services = productApi.getServiceByProdId(productId).getCheckedData();
            String serviceLog = "";
            cn.hutool.json.JSONObject serviceParamLog = JSONUtil.createObj();
            // 处理设备服务
            for (ProductDeviceServiceListResponse service : services) {
                if (!Objects.equals(request.getIdentifier(), service.getIdentifier())) {
                    continue;
                }
                serviceLog = service.getFunctionName();
                if (CollUtil.isNotEmpty(request.getFunctionParameters())) {
                    for (AVueFunctionParameterRequest functionParameter : request.getFunctionParameters()) {
                        serviceParamLog.putOpt(functionParameter.getIdentifier(), functionParameter.getValue());
                    }
                }
            }
            HistoryEventEntity historyEventEntity = new HistoryEventEntity();
            historyEventEntity.setTenantId(tenantId);
            historyEventEntity.setProjectBizId(device.getBizProjectId());
            historyEventEntity.setAlarmType(AlarmTypeEnum.SERVICE_EVENT.getCode());
            historyEventEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
            historyEventEntity.setAlarmLevel(AlarmLevelEnum.INFO.getCode());
            historyEventEntity.setObjId(request.getBizDeviceId());
            historyEventEntity.setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
            historyEventEntity.setEventType(EventTypeEnum.COMMON_EVENT.getCode());
            historyEventEntity.setAlarmStatus(AlarmStatusEnum.RESET.getCode());
            historyEventEntity.setAlarmCode(request.getIdentifier());
            historyEventEntity.setAlarmDesc(StrUtil.format(SERVICE_EVENT_TEMPLATE, username, serviceLog, serviceParamLog));

            historyEventEntity.setAlarmBizId(bizSequenceService.next(BizSequenceEnum.ALARM));
            historyEventEntity.setEventId(bizSequenceService.next(BizSequenceEnum.EVENT));

            historyEventMapper.insert(historyEventEntity);

            AlarmConfirmEntity alarmConfirmEntity = new AlarmConfirmEntity();
            alarmConfirmEntity.setTenantId(tenantId);
            alarmConfirmEntity.setEventId(historyEventEntity.getEventId());
            alarmConfirmEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.AUTO.getCode());
            alarmConfirmEntity.setIsConfirm(true);
            alarmConfirmEntity.setConfirmUser(0L);
            alarmConfirmEntity.setConfirmTime(LocalDateTime.now());
            alarmConfirmMapper.insert(alarmConfirmEntity);
        } catch (Exception e) {
            log.error("AVue设备服务下发日志保存失败：{}", request, e);
        }
    }

    @Override
    public NodeProjectDeviceTreeResponse getDeviceTreeSpace() {
        // 从userProject获取当前的管理节点的树
        Response<NodeProjectTreeDTO> resp = userProjectApi.getCurrentUserProjectTree();
        Assert.isTrue(resp.isSuccess(), () -> new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR));
        NodeProjectTreeDTO dto = resp.getResult();
        NodeProjectDeviceTreeResponse result = BeanUtil.copyProperties(dto, NodeProjectDeviceTreeResponse.class);

        // 从dto获取所有的设备信息
        List<String> bizProjectIds = new ArrayList<>();
        getBizProjectIds(dto, bizProjectIds);

        // 通过bizProjectId,查询设备
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizProjectId, bizProjectIds));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        //根据空间分组
        Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizAreaId));

        // 查询相关产品的设备属性
        List<String> bizProductIds = list.stream().map(DeviceMonitorEntity::getBizProductId).distinct().toList();
        Response<Map<String, List<ProductDeviceAttrResponse>>> projectDetails = productApi.getProjectAttrs(bizProductIds);
        assembleDevice2ProjBySpace(result, map, projectDetails.getResult());
        return result;
    }

    @Override
    public NodeProjectDeviceTreeResponse getDeviceTreeV1Space(String bizId) {
        // 从userProject获取当前的管理节点的树
        Response<NodeProjectTreeDTO> resp = userProjectApi.getCurrentUserProjectTree();
        Assert.isTrue(resp.isSuccess(), () -> new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR));
        NodeProjectTreeDTO dto = resp.getResult();
        NodeProjectDeviceTreeResponse result = BeanUtil.copyProperties(dto, NodeProjectDeviceTreeResponse.class);

        if (StringUtils.isEmpty(bizId)) {
            return result;
        }

        // 从dto获取所有的设备信息
        result = getChildrenByBizProjId(result, bizId);

        if (null == result) {
            return result;
        }
        // 通过bizProjectId,查询设备
        Map<String, List<ProductDeviceAttrResponse>> productAttrMap = Maps.newHashMap();
        List<String> bizProjectIds = new ArrayList<>();
        bizProjectIds.add(bizId);
        List<DeviceMonitorEntity> list = deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizProjectId, bizProjectIds));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        // 排除手动抄表的水电气
        List<String> bizCategoryIdList = categoryApi.getBizCategoryIdList(
                CollUtil.newArrayList(DeviceStaCategoryEnum.RQB.getCode(), DeviceStaCategoryEnum.ZNSB.getCode(), DeviceStaCategoryEnum.DB3PH.getCode())
        ).getCheckedData();
        List<String> meterBizDeviceIds = deviceMonitorMapper.selectProjectMeterBizDeviceIds(bizId, bizCategoryIdList);
        list = list.stream().filter(o -> !meterBizDeviceIds.contains(o.getBizDeviceId())).toList();

        Map<String, List<DeviceMonitorEntity>> map = list.stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizAreaId));
        List<String> bizProductIds = list.stream().map(DeviceMonitorEntity::getBizProductId).distinct().toList();
        Response<Map<String, List<ProductDeviceAttrResponse>>> projectDetails = productApi.getProjectAttrs(bizProductIds);
        productAttrMap = projectDetails.getResult();
        assembleDevice2ProjBySpace(result, map, productAttrMap);
        return result;
    }

    private NodeProjectDeviceTreeResponse getChildrenByBizProjId(NodeProjectDeviceTreeResponse result, String bizId) {
        if ("00".equals(result.getType()) && result.getProjectBizId().equals(bizId)) {
            return result;
        } else if (!CollectionUtils.isEmpty(result.getChildren())) {
            for (NodeProjectDeviceTreeResponse i : result.getChildren()) {
                NodeProjectDeviceTreeResponse val = getChildrenByBizProjId(i, bizId);
                if (null != val) {
                    return val;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public NodeSpaceDeviceTreeResponse nodeSpaceDeviceTree(String bizCategoryId) {
        // 获取用户当前的管理节点的树
        NodeProjectTreeDTO nodeProjectTreeDTO = userProjectApi.getCurrentUserProjectTree().getCheckedData();
        if (null == nodeProjectTreeDTO) {
            return null;
        }
        NodeSpaceDeviceTreeResponse result = new NodeSpaceDeviceTreeResponse();
        assembleNodeToNodeSpaceDeviceTreeResponse(result, nodeProjectTreeDTO, bizCategoryId);
        return result;
    }

    private void assembleNodeToNodeSpaceDeviceTreeResponse(NodeSpaceDeviceTreeResponse node, NodeProjectTreeDTO nodeProjectTreeDTO, String bizCategoryId) {
        node.setName(nodeProjectTreeDTO.getName());
        if ("00".equals(nodeProjectTreeDTO.getType())) {
            node.setId(nodeProjectTreeDTO.getProjectBizId()).setBizId(nodeProjectTreeDTO.getProjectBizId());

            Map<String, List<DeviceMonitorEntity>> areaDeviceMap = deviceMonitorMapper.selectList(
                    new QueryWrapper<DeviceMonitorEntity>().lambda()
                            .eq(DeviceMonitorEntity::getBizProjectId, nodeProjectTreeDTO.getProjectBizId())
                            .eq(DeviceMonitorEntity::getBizCategoryId, bizCategoryId)
            ).stream().collect(Collectors.groupingBy(DeviceMonitorEntity::getBizAreaId));

            List<ProjectSpaceTreeApiResponse> projectSpaceTreeList = spaceApi.searchSpaces(nodeProjectTreeDTO.getProjectId()).getCheckedData();
            // 项目节点对应的空间
            ProjectSpaceTreeApiResponse projectSpaceTreeDTO = projectSpaceTreeList.get(0);
            List<DeviceMonitorEntity> projectDeviceList = areaDeviceMap.get(String.valueOf(projectSpaceTreeDTO.getSpaceId()));
            if (!CollectionUtils.isEmpty(projectDeviceList)) {
                projectDeviceList.forEach(o -> {
                    NodeSpaceDeviceTreeResponse deviceNode = new NodeSpaceDeviceTreeResponse()
                            .setId(o.getBizDeviceId())
                            .setBizId(o.getBizDeviceId())
                            .setName(o.getName())
                            .setCheckable(Boolean.TRUE);
                    node.getChildren().add(deviceNode);
                });
            }
            // 转换空间树
            List<ProjectSpaceTreeApiResponse> projectSpaceChildren = projectSpaceTreeDTO.getChildren();
            if (!CollectionUtils.isEmpty(projectSpaceChildren)) {
                projectSpaceChildren.forEach(o -> assembleSpaceToNodeSpaceDeviceTreeResponse(node, o, areaDeviceMap));
            }
        } else {
            node.setId(nodeProjectTreeDTO.getBizNodeId()).setBizId(nodeProjectTreeDTO.getBizNodeId());

            if (!CollectionUtils.isEmpty(nodeProjectTreeDTO.getChildren())) {
                nodeProjectTreeDTO.getChildren().forEach(o -> {
                    NodeSpaceDeviceTreeResponse result = new NodeSpaceDeviceTreeResponse()
                            .setId(nodeProjectTreeDTO.getBizNodeId())
                            .setBizId(nodeProjectTreeDTO.getBizNodeId())
                            .setName(nodeProjectTreeDTO.getName());
                    node.getChildren().add(result);
                    assembleNodeToNodeSpaceDeviceTreeResponse(result, o, bizCategoryId);
                });
            }
        }
    }

    private void assembleSpaceToNodeSpaceDeviceTreeResponse(NodeSpaceDeviceTreeResponse node, ProjectSpaceTreeApiResponse projectSpaceTreeDTO, Map<String, List<DeviceMonitorEntity>> areaDeviceMap) {
        NodeSpaceDeviceTreeResponse spaceNode = new NodeSpaceDeviceTreeResponse()
                .setId(projectSpaceTreeDTO.getBizId())
                .setBizId(projectSpaceTreeDTO.getBizId())
                .setName(projectSpaceTreeDTO.getSpaceName());
        node.getChildren().add(spaceNode);
        List<DeviceMonitorEntity> projectDeviceList = areaDeviceMap.get(String.valueOf(projectSpaceTreeDTO.getSpaceId()));
        if (!CollectionUtils.isEmpty(projectDeviceList)) {
            projectDeviceList.forEach(o -> {
                NodeSpaceDeviceTreeResponse deviceNode = new NodeSpaceDeviceTreeResponse()
                        .setId(o.getBizDeviceId())
                        .setBizId(o.getBizDeviceId())
                        .setName(o.getName())
                        .setCheckable(Boolean.TRUE);
                spaceNode.getChildren().add(deviceNode);
            });
        }
        if (!CollectionUtils.isEmpty(projectSpaceTreeDTO.getChildren())) {
            projectSpaceTreeDTO.getChildren().forEach(o -> assembleSpaceToNodeSpaceDeviceTreeResponse(spaceNode, o, areaDeviceMap));
        }
    }

    @Override
    public List<DeviceMonitorEntity> selectByBizDeviceIds(List<String> bizDeviceIds) {
        return deviceMonitorMapper.selectList(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizDeviceId, bizDeviceIds));
    }

    @Override
    public List<DeviceStaDTO> listStaDeviceByCategory(DeviceStaCategoryEnum categoryType) {
        Response<String> projectDetails = categoryApi.getBizCategoryId(categoryType.getCode());
        String categoryBizId = projectDetails.getResult();
        List<DeviceStaDTO> deviceStaDTOS = deviceMonitorMapper.listStaDeviceByCategory(Collections.singletonList(categoryBizId));
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOS) {
            String bizDeviceId = deviceStaDTO.getBizDeviceId();
            List<DeviceParameterEntity> deviceParameterEntities = deviceParameterMapper.selectList(Wrappers.<DeviceParameterEntity>lambdaQuery()
                    .eq(DeviceParameterEntity::getBizDeviceId, bizDeviceId));
            Map<String, String> otherParams = new HashMap<>();
            for (DeviceParameterEntity deviceParameterEntity : deviceParameterEntities) {
                otherParams.put(deviceParameterEntity.getIdentifier(), deviceParameterEntity.getValue());
            }
            deviceStaDTO.setOtherParams(otherParams);
        }
        return deviceStaDTOS;
    }

    private void assembleDevice2Proj(NodeProjectDeviceTreeResponse result, Map<String, List<DeviceMonitorEntity>> map, Map<String, List<ProductDeviceAttrResponse>> productAttrMap) {
        if ("00".equals(result.getType())) {
            List<DeviceMonitorEntity> list = map.get(result.getProjectBizId());
            if (!CollectionUtils.isEmpty(list)) {
                result.setChildren(list.stream().map(i -> {
                    DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
                    vo.setAttrCodes(new ArrayList<>());
                    String bizProductId = vo.getBizProductId();
                    if (productAttrMap != null) {
                        List<ProductDeviceAttrResponse> productDeviceAttrResponses = productAttrMap.get(bizProductId);
                        if (CollUtil.isNotEmpty(productDeviceAttrResponses)) {
                            vo.setAttrCodes(BeanUtil.copyToList(productDeviceAttrResponses, DeviceAttrDisplayVO.class));
                        }
                    }
                    return vo;
                }).collect(Collectors.toList()));
            }
        } else if (!CollectionUtils.isEmpty(result.getChildren())) {
            result.getChildren().forEach(i -> assembleDevice2Proj(i, map, productAttrMap));
        }
    }

    private void assembleDevice2ProjByProduct(NodeProjectDeviceTreeResponse result, Map<String, List<DeviceMonitorEntity>> map, Map<String, List<ProductDeviceAttrResponse>> productAttrMap) {
        List<String> productIds = productAttrMap.keySet().stream().toList();
        Response<List<ProductDetailResponse>> productResponse = productApi.getByBizids(productIds);
        Map<String, String> allProducts = productResponse.getResult().stream().collect(Collectors.toMap(ProductDetailResponse::getBizId, ProductDetailResponse::getName));
        if ("00".equals(result.getType())) {
            List<DeviceTreeDisplayVO> productChildren = new ArrayList<>();
            allProducts.forEach((bizId, name) -> {
                DeviceTreeDisplayVO product = new DeviceTreeDisplayVO();
                product.setBizDeviceId(bizId);
                product.setBizProductId(bizId);
                product.setName(name);
                List<DeviceMonitorEntity> list = map.get(bizId).stream().filter(i -> i.getBizProjectId().equals(result.getProjectBizId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(list)) {
                    product.setChildren(list.stream().map(i -> {
                        DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
                        vo.setAttrCodes(new ArrayList<>());
                        String bizProductId = vo.getBizProductId();
                        if (productAttrMap != null) {
                            List<ProductDeviceAttrResponse> productDeviceAttrResponses = productAttrMap.get(bizProductId);
                            if (CollUtil.isNotEmpty(productDeviceAttrResponses)) {
                                vo.setAttrCodes(BeanUtil.copyToList(productDeviceAttrResponses, DeviceAttrDisplayVO.class));
                            }
                        }
                        return vo;
                    }).collect(Collectors.toList()));
                    productChildren.add(product);
                }
            });
            result.setChildren(productChildren.stream().map(i -> {
                DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
                return vo;
            }).collect(Collectors.toList()));

        } else if (!CollectionUtils.isEmpty(result.getChildren())) {
            result.getChildren().forEach(i -> assembleDevice2ProjByProduct(i, map, productAttrMap));
        }
    }

    private void assembleDevice2ProjBySpace(NodeProjectDeviceTreeResponse result, Map<String, List<DeviceMonitorEntity>> map, Map<String, List<ProductDeviceAttrResponse>> productAttrMap) {
        List<String> productIds = productAttrMap.keySet().stream().toList();
        if ("00".equals(result.getType())) {
            Response<List<ProjectSpaceTreeApiResponse>> spaces = spaceApi.getPlaneSpaces(result.getProjectId(), false);
            Map<Long, String> spacesMap = spaces.getResult().stream().collect(Collectors.toMap(ProjectSpaceTreeApiResponse::getSpaceId, ProjectSpaceTreeApiResponse::getSpaceName));
            List<DeviceTreeDisplayVO> productChildren = new ArrayList<>();
            spacesMap.forEach((bizId, name) -> {
                DeviceTreeDisplayVO product = new DeviceTreeDisplayVO();
                product.setName(name);
                product.setBizDeviceId(bizId.toString());
                List<DeviceMonitorEntity> list = map.get(String.valueOf(bizId));
                if (!CollectionUtils.isEmpty(list)) {
                    product.setChildren(list.stream().map(i -> {
                        DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
                        vo.setAttrCodes(new ArrayList<>());
                        String bizProductId = vo.getBizProductId();
                        if (productAttrMap != null) {
                            List<ProductDeviceAttrResponse> productDeviceAttrResponses = productAttrMap.get(bizProductId);
                            if (CollUtil.isNotEmpty(productDeviceAttrResponses)) {
                                vo.setAttrCodes(BeanUtil.copyToList(productDeviceAttrResponses, DeviceAttrDisplayVO.class));
                            }
                        }
                        return vo;
                    }).collect(Collectors.toList()));
                }
                productChildren.add(product);
            });
            result.setChildren(productChildren.stream().map(i -> {
                DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
                return vo;
            }).collect(Collectors.toList()));
            //根节点空间的设备处理
            Response<List<ProjectSpaceTreeApiResponse>> rootspace = spaceApi.getPlaneSpaces(result.getProjectId(), true);
            List<DeviceMonitorEntity> rootDevices = map.get(String.valueOf(rootspace.getResult().get(0).getSpaceId()));
            if (rootDevices != null && rootDevices.size() > 0) {
                List<DeviceTreeDisplayVO> rootVOs = rootDevices.stream().map(new Function<DeviceMonitorEntity, DeviceTreeDisplayVO>() {
                    @Override
                    public DeviceTreeDisplayVO apply(DeviceMonitorEntity deviceMonitorEntity) {
                        DeviceTreeDisplayVO vo = BeanUtil.copyProperties(deviceMonitorEntity, DeviceTreeDisplayVO.class);
                        vo.setAttrCodes(new ArrayList<>());
                        String bizProductId = vo.getBizProductId();
                        if (productAttrMap != null) {
                            List<ProductDeviceAttrResponse> productDeviceAttrResponses = productAttrMap.get(bizProductId);
                            if (CollUtil.isNotEmpty(productDeviceAttrResponses)) {
                                vo.setAttrCodes(BeanUtil.copyToList(productDeviceAttrResponses, DeviceAttrDisplayVO.class));
                            }
                        }
                        return vo;
                    }
                }).collect(Collectors.toList());
                result.getChildren().addAll(0, rootVOs);
            }
        } else if (!CollectionUtils.isEmpty(result.getChildren())) {
            //添加root 节点空间的设备
//            Response<List<ProjectSpaceTreeApiResponse>> spaces = spaceApi.getPlaneSpaces(result.getProjectId(),false);
//
//            List<DeviceMonitorEntity> list = map.get(String.valueOf(spaces.getResult().get(0).getSpaceId()));
//            result.setChildren(list.stream().map(i -> {
//                DeviceTreeDisplayVO vo = BeanUtil.copyProperties(i, DeviceTreeDisplayVO.class);
//                vo.setAttrCodes(new ArrayList<>());
//                String bizProductId = vo.getBizProductId();
//                if (productAttrMap != null) {
//                    List<ProductDeviceAttrResponse> productDeviceAttrResponses = productAttrMap.get(bizProductId);
//                    if (CollUtil.isNotEmpty(productDeviceAttrResponses)) {
//                        vo.setAttrCodes(BeanUtil.copyToList(productDeviceAttrResponses, DeviceAttrDisplayVO.class));
//                    }
//                }
//                return vo;
//            }).collect(Collectors.toList()));
            result.getChildren().forEach(i -> assembleDevice2ProjBySpace(i, map, productAttrMap));
        }
    }

    private void getBizProjectIds(NodeProjectTreeDTO dto, List<String> bizProjectIds) {
        if ("00".equals(dto.getType())) {
            bizProjectIds.add(dto.getProjectBizId());
        } else if (!CollectionUtils.isEmpty(dto.getChildren())) {
            dto.getChildren().forEach(i -> {
                getBizProjectIds(i, bizProjectIds);
            });
        }
    }

//    private PageDTO<DeviceMonitorVO> buildBlankPage() {
//        PageDTO<DeviceMonitorVO> pageVO = new PageDTO<>();
//        pageVO.setCurrent(0);
//        pageVO.setTotal(0);
//        pageVO.setPages(1);
//        pageVO.setRecords(new ArrayList<>());
//        return pageVO;
//    }

//    private PageDTO<DeviceMonitorVO> page2VO(IPage<DeviceMonitorEntity> originPage, Map<String, String> projectMap) {
//        List<DeviceMonitorEntity> devices = originPage.getRecords();
//        List<String> deviceIds = devices.stream().map(DeviceMonitorEntity::getBizDeviceId).distinct().collect(Collectors.toList());
//
//        Map<String, DeviceCurrentDTO> currentDTOMap = deviceCurrentApi.getDeviceCurrent(deviceIds).getResult().stream()
//                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, Function.identity()));
//        List<DeviceMonitorVO> result = devices.stream().map(deviceMonitorEntity -> {
//            DeviceMonitorVO vo = new DeviceMonitorVO();
//            BeanUtils.copyProperties(deviceMonitorEntity, vo);
//            vo.setBizProjectName(projectMap.get(vo.getBizProjectId()));
//            vo.setCurrent(currentDTOMap.get(deviceMonitorEntity.getBizDeviceId()));
//            return vo;
//        }).collect(Collectors.toList());
//
//        PageDTO<DeviceMonitorVO> pageVO = new PageDTO<>();
//        pageVO.setCurrent(originPage.getCurrent());
//        pageVO.setTotal(originPage.getTotal());
//        pageVO.setPages(originPage.getPages());
//        pageVO.setRecords(result);
//        return pageVO;
//    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<DeviceMonitorEntity> getCondition(DeviceMonitorQueryDTO queryInfo) {
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(queryInfo.getStartTime());
        } catch (Exception e) {
            log.error("查询参数错误，startTime不符合格式{}", queryInfo.getStartTime());
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(queryInfo.getEndTime());
        } catch (Exception e) {
            log.error("查询参数错误，endTime不符合格式{}", queryInfo.getStartTime());
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }

        LocalDateTime startTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.MAX);

        return Wrappers.<DeviceMonitorEntity>lambdaQuery()
                .eq(DeviceMonitorEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE)
                .between(DeviceMonitorEntity::getCreateTime, startTime, endTime)
                .eq(Objects.nonNull(queryInfo.getId()), DeviceMonitorEntity::getId, queryInfo.getId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getBizProjectId()), DeviceMonitorEntity::getBizProjectId, queryInfo.getBizProjectId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getBizAreaId()), DeviceMonitorEntity::getBizAreaId, queryInfo.getBizAreaId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getAreaPath()), DeviceMonitorEntity::getAreaPath, queryInfo.getAreaPath())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getBizDeviceId()), DeviceMonitorEntity::getBizDeviceId, queryInfo.getBizDeviceId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getName()), DeviceMonitorEntity::getName, queryInfo.getName())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getBizProductId()), DeviceMonitorEntity::getBizProjectId, queryInfo.getBizProjectId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getBizCategoryId()), DeviceMonitorEntity::getBizCategoryId, queryInfo.getBizCategoryId())
                .like(CharSequenceUtil.isNotBlank(queryInfo.getCode()), DeviceMonitorEntity::getCode, queryInfo.getCode())
                .eq(Objects.nonNull(queryInfo.getTenantId()), DeviceMonitorEntity::getTenantId, queryInfo.getTenantId())
                .orderByDesc(DeviceMonitorEntity::getBizDeviceId);
//        LambdaQueryWrapper<DeviceMonitorEntity> wrapper = new QueryWrapper<DeviceMonitorEntity>().lambda().eq(DeviceMonitorEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);
//
//        // 开始时间
//        if (CharSequenceUtil.isNotBlank(queryInfo.getStartTime())) {
//            long startTimeMillion;
//            try {
//                startTimeMillion = DateUtils
//                        .parseDate(queryInfo.getStartTime() + " 00:00:00")
//                        .getTime();
//            } catch (Exception e) {
//                log.error("查询参数错误，startTime不符合格式{}", queryInfo.getStartTime());
//                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
//            }
//            wrapper.le(DeviceMonitorEntity::getCreateTime, new Timestamp(startTimeMillion));
//        }
//
//        // 结束时间
//        if (CharSequenceUtil.isNotBlank(queryInfo.getEndTime())) {
//            long endTimeMillion;
//            try {
//                endTimeMillion = DateUtils
//                        .parseDate(queryInfo.getEndTime() + " 23:59:59")
//                        .getTime();
//            } catch (Exception e) {
//                log.error("查询参数错误，endTime不符合格式{}", queryInfo.getEndTime());
//                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
//            }
//            wrapper.ge(DeviceMonitorEntity::getCreateTime, new Timestamp(endTimeMillion));
//        }
//        // 设备id
//        if (null != queryInfo.getId()) {
//            wrapper.eq(DeviceMonitorEntity::getId, queryInfo.getId());
//        }
//        // 项目id（全局唯一id）
//        if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
//            wrapper.like(DeviceMonitorEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
//        }
//        // 分区id（全局唯一id）
//        if (!StringUtils.hasText(queryInfo.getBizAreaId())) {
//            wrapper.like(DeviceMonitorEntity::getBizAreaId, "%" + queryInfo.getBizAreaId() + "%");
//        }
//        // 分区路径path
//        if (!StringUtils.hasText(queryInfo.getAreaPath())) {
//            wrapper.like(DeviceMonitorEntity::getAreaPath, "%" + queryInfo.getAreaPath() + "%");
//        }
//        // 设备id（全局唯一id）
//        if (!StringUtils.hasText(queryInfo.getBizDeviceId())) {
//            wrapper.like(DeviceMonitorEntity::getBizDeviceId, "%" + queryInfo.getBizDeviceId() + "%");
//        }
//        // 设备名称
//        if (!StringUtils.hasText(queryInfo.getName())) {
//            wrapper.like(DeviceMonitorEntity::getName, "%" + queryInfo.getName() + "%");
//        }
//        // 产品id（全局唯一id）
//        if (!StringUtils.hasText(queryInfo.getBizProductId())) {
//            wrapper.like(DeviceMonitorEntity::getBizProductId, "%" + queryInfo.getBizProductId() + "%");
//        }
//        // 品类id（全局唯一id）
//        if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
//            wrapper.like(DeviceMonitorEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
//        }
//        // 设备编码（校验唯一）
//        if (!StringUtils.hasText(queryInfo.getCode())) {
//            wrapper.like(DeviceMonitorEntity::getCode, "%" + queryInfo.getCode() + "%");
//        }
//        // 租户id
//        if (null != queryInfo.getTenantId()) {
//            wrapper.eq(DeviceMonitorEntity::getTenantId, queryInfo.getTenantId());
//        }
//        wrapper.orderByDesc(DeviceMonitorEntity::getUpdateTime);
//        return wrapper;
    }
}
