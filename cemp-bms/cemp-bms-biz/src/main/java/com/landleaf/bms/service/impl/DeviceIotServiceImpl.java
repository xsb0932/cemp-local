package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.dto.GatewayDeviceIdRelationDTO;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.request.DeviceIotRequest;
import com.landleaf.bms.domain.request.DeviceParameterDetailRequest;
import com.landleaf.bms.domain.response.DeviceIotInfoResponse;
import com.landleaf.bms.domain.response.DeviceIotOfExcel;
import com.landleaf.bms.domain.response.DeviceIotResponse;
import com.landleaf.bms.domain.response.DeviceParameterDetailResponse;
import com.landleaf.bms.service.DeviceIotService;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import com.landleaf.redis.dao.DeviceCacheDao;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.dao.dto.DeviceParameterValueCacheDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 设备-监测平台的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-07-12
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceIotServiceImpl extends ServiceImpl<DeviceIotMapper, DeviceIotEntity> implements DeviceIotService {

    private final BizSequenceService bizSequenceService;
    private final MonitorApi monitorApi;


    @Resource
    DeviceIotMapper deviceIotMapper;
    @Resource
    ProductDeviceParameterMapper productDeviceParameterMapper;
    @Resource
    DeviceParameterDetailMapper deviceParameterDetailMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    ProjectMapper projectMapper;
    @Resource
    private DeviceCacheDao deviceCacheDao;

    @Transactional
    @Override
    public DeviceIotEntity add(DeviceIotRequest addInfo) {
        TenantContext.setIgnore(true);
        DeviceIotEntity saveEntity = new DeviceIotEntity();
        BeanUtil.copyProperties(addInfo, saveEntity);
        //项目名称
        //ProjectDetailsResponse project= projectMapper.selectProjectDetails(addInfo.getBizProjectId());
        ProjectEntity project = projectMapper.selectById(addInfo.getProjectId());
        //产品
        ProductEntity product = productMapper.selectById(addInfo.getProductId());
        boolean exists = deviceIotMapper.exists(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getSourceDeviceId, addInfo.getSourceDeviceId()));
        if (exists)
            throw new ServiceException("1", "外部设备ID重复，请确认");
        //设备业务id
        String bizDeviceId = bizSequenceService.next(BizSequenceEnum.DEVICE);
        saveEntity.setBizDeviceId(bizDeviceId);
        saveEntity.setBizProductId(product.getBizId());
        saveEntity.setProductId(addInfo.getProductId());
        saveEntity.setBizCategoryId(product.getCategoryId());
        saveEntity.setProjectName(project.getName());
        saveEntity.setBizProjectId(project.getBizProjectId());
        List<DeviceParameterValueCacheDTO> deviceParameterValueCacheDTOList = new ArrayList<>();
        //其他参数
        List<DeviceParameterDetailEntity> parameters = new ArrayList<>();
        if (addInfo.getDeviceParameters() != null && addInfo.getDeviceParameters().size() > 0) {
            addInfo.getDeviceParameters().forEach(param -> {
                //ProductDeviceParameterEntity pdpe = productDeviceParameterMapper.selectById(param.getProductParameterId());
                ProductDeviceParameterEntity pdpe = productDeviceParameterMapper.getParameter(product.getId(), param.getIdentifier());
                DeviceParameterDetailEntity dpde = new DeviceParameterDetailEntity();
                BeanUtil.copyProperties(pdpe, dpde);
                dpde.setValue(param.getValue());
                dpde.setId(null);
                dpde.setBizDeviceId(bizDeviceId);
                //deviceParameterDetailMapper.insert(dpde);
                if (StringUtils.isNotBlank(dpde.getValue())) {
                    parameters.add(dpde);
                }
                DeviceParameterValueCacheDTO cacheDTO = new DeviceParameterValueCacheDTO();
                cacheDTO.setIdentifier(param.getIdentifier()).setValue(param.getValue());
                deviceParameterValueCacheDTOList.add(cacheDTO);
            });
        }
        TenantContext.setIgnore(false);
        this.save(saveEntity);

        parameters.forEach(entity -> deviceParameterDetailMapper.insert(entity));

        DeviceInfoCacheDTO deviceInfoCacheDTO = BeanUtil.copyProperties(saveEntity, DeviceInfoCacheDTO.class);
        deviceCacheDao.saveDeviceInfoCache(deviceInfoCacheDTO);
        deviceCacheDao.saveDeviceParameterValueCache(saveEntity.getBizDeviceId(), deviceParameterValueCacheDTOList);
        return saveEntity;

    }

    public static void main(String[] args) {
        System.out.println(StringUtils.isBlank("0"));
    }

    @Transactional
    @Override
    public void edit(DeviceIotRequest editInfo) {
        TenantContext.setIgnore(true);
        DeviceIotEntity entity = this.getById(editInfo.getId());
        if (null == entity) {
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(editInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        //产品
        ProductEntity product = productMapper.selectById(editInfo.getProductId());
        entity.setBizProductId(product.getBizId());
        entity.setBizCategoryId(product.getCategoryId());
        //项目
        ProjectEntity project = projectMapper.selectById(editInfo.getProjectId());
        entity.setBizProjectId(project.getBizProjectId());
        entity.setProjectName(project.getName());

        this.updateById(entity);
        List<DeviceParameterValueCacheDTO> deviceParameterValueCacheDTOList = new ArrayList<>();
        //设备参数
        editInfo.getDeviceParameters().forEach(new Consumer<DeviceParameterDetailRequest>() {
            @Override
            public void accept(DeviceParameterDetailRequest parameter) {
                //DeviceParameterDetailEntity detail = deviceParameterDetailMapper.selectById(parameter.getId());
                DeviceParameterDetailEntity detail = deviceParameterDetailMapper.getParameter(editInfo.getBizDeviceId(), parameter.getIdentifier());
                if (detail != null) {
                    detail.setValue(parameter.getValue());
                    deviceParameterDetailMapper.updateById(detail);
                } else {
                    if (StringUtils.isNotBlank(parameter.getValue())) {
                        //添加设备参数
                        ProductDeviceParameterEntity productParameter = productDeviceParameterMapper.selectOne(new LambdaQueryWrapper<ProductDeviceParameterEntity>().
                                eq(ProductDeviceParameterEntity::getProductId, editInfo.getProductId()).
                                eq(ProductDeviceParameterEntity::getIdentifier, parameter.getIdentifier()));

                        DeviceParameterDetailEntity newEntity = new DeviceParameterDetailEntity();
                        BeanUtil.copyProperties(productParameter, newEntity);
                        newEntity.setId(null);
                        newEntity.setTenantId(TenantContext.getTenantId());
                        newEntity.setBizDeviceId(editInfo.getBizDeviceId());
                        newEntity.setValue(parameter.getValue());
                        deviceParameterDetailMapper.insert(newEntity);
                    }
                }
                DeviceParameterValueCacheDTO cacheDTO = new DeviceParameterValueCacheDTO();
                cacheDTO.setIdentifier(parameter.getIdentifier()).setValue(parameter.getValue());
                deviceParameterValueCacheDTOList.add(cacheDTO);
            }
        });
        // 更新缓存
        List<GatewayDeviceIdRelationDTO> updateCacheList = deviceIotMapper.listGatewayDeviceIdRelationByDeviceId(editInfo.getId(), entity.getTenantId());
        for (GatewayDeviceIdRelationDTO dto : updateCacheList) {
            deviceCacheDao.updateMessagingIdRelationCache(dto.getBizDeviceId(), dto.getBizProductId(), dto.getBizGatewayId(), dto.getSourceDeviceId());
        }
        DeviceInfoCacheDTO deviceInfoCacheDTO = BeanUtil.copyProperties(entity, DeviceInfoCacheDTO.class);
        deviceCacheDao.saveDeviceInfoCache(deviceInfoCacheDTO);
        deviceCacheDao.saveDeviceParameterValueCache(entity.getBizDeviceId(), deviceParameterValueCacheDTOList);
    }

    private void mergePage(IPage origin, IPage target) {
        target.setPages(origin.getPages());
        target.setTotal(origin.getTotal());
        target.setSize(origin.getSize());
        target.setCurrent(origin.getCurrent());
    }

    @Override
    public IPage<DeviceIotResponse> getProjectStaData(DeviceIotRequest qry) {
        IPage<DeviceIotResponse> iotDevicePage = new Page<>(qry.getPageNo(), qry.getPageSize());
        IPage<DeviceIotEntity> deviceEntityPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        LambdaQueryWrapper<DeviceIotEntity> lqw = new LambdaQueryWrapper<>();

        lqw.eq(DeviceIotEntity::getProductId, qry.getProductId());
        if (StringUtils.isNotBlank(qry.getProjectName())) {
            lqw.like(DeviceIotEntity::getProjectName, qry.getProjectName());
        }
        if (StringUtils.isNotBlank(qry.getName())) {
            lqw.like(DeviceIotEntity::getName, qry.getName());
        }
        lqw.orderByDesc(DeviceIotEntity::getBizDeviceId);

        deviceEntityPage = deviceIotMapper.selectPage(deviceEntityPage, lqw);
        this.mergePage(deviceEntityPage, iotDevicePage);
        List<DeviceIotResponse> devices = deviceEntityPage.getRecords().stream().map(deviceIotEntity -> {
            DeviceIotResponse response = new DeviceIotResponse();
            BeanUtils.copyProperties(deviceIotEntity, response);
            List<DeviceParameterDetailEntity> parameters = deviceParameterDetailMapper.selectList(new LambdaQueryWrapper<DeviceParameterDetailEntity>().eq(DeviceParameterDetailEntity::getBizDeviceId, response.getBizDeviceId()));
            response.setDeviceParameters(parameters.stream().map(deviceParameterDetailEntity -> new DeviceParameterDetailResponse(
                    deviceParameterDetailEntity.getId(), null, deviceParameterDetailEntity.getIdentifier(), deviceParameterDetailEntity.getFunctionName(), deviceParameterDetailEntity.getValue())
            ).collect(Collectors.toList()));

            return response;
        }).collect(Collectors.toList());
        iotDevicePage.setRecords(devices);
        return iotDevicePage;
    }

    @Override
    public DeviceIotInfoResponse info(Long id) {
        DeviceIotEntity device = deviceIotMapper.selectById(id);
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        DeviceIotInfoResponse response = new DeviceIotInfoResponse();
        BeanUtil.copyProperties(device, response);
        List<DeviceParameterDetailResponse> parameters = deviceParameterDetailMapper.selectList(
                        new LambdaQueryWrapper<DeviceParameterDetailEntity>()
                                .eq(DeviceParameterDetailEntity::getBizDeviceId, response.getBizDeviceId())
                ).stream()
                .map(dto -> new DeviceParameterDetailResponse(dto.getId(), null, dto.getIdentifier(), dto.getFunctionName(), dto.getValue()))
                .toList();
        response.setDeviceParameters(parameters);
        return response;
    }


    private String getErrMsg(int index, DeviceIotOfExcel device, ProjectEntity project, ProductEntity product, ProjectSpaceEntity space, String bizDeviceId) {
        StringBuilder strb = new StringBuilder();
        if (StringUtils.isBlank(device.getProjectName()))
            strb.append("所属项目不允许为空;");

        if (StringUtils.isBlank(device.getProductName()))
            strb.append("所属产品不允许为空;");

        if (StringUtils.isBlank(device.getDeviceName()))
            strb.append("设备名称不允许为空;");

        if (StringUtils.isBlank(device.getDeviceCode()))
            strb.append("设备编码不允许为空;");

        if (StringUtils.isBlank(device.getBizDeviceId()))
            strb.append("外部ID不允许为空;");

//        if(StringUtils.isBlank(device.getSpaceName()))
//            strb.append("所属空间不允许为空;");

//        if(StringUtils.isBlank(device.getLocationDesc()))
//            strb.append("设备位置不允许为空;");
        if (project == null)
            strb.append(String.format("项目-%s-查无数据", device.getProjectName()));

        if (product == null)
            strb.append(String.format("产品-%s-查无数据", device.getProductName()));

        if (space == null)
            strb.append(String.format("空间-%s-查无数据", device.getSpaceName()));

        if (deviceIotMapper.exists(new LambdaQueryWrapper<DeviceIotEntity>().eq(DeviceIotEntity::getBizDeviceId, bizDeviceId)))
            strb.append(String.format("重复的设备业务编码-%s", bizDeviceId));

        if (!strb.isEmpty()) {
            return String.format("第%s条数据校验有误: %s", index, strb.toString());
        } else {
            return null;
        }
    }

    private List<String> checkRepeatDevice(List<DeviceIotOfExcel> list) {
        List<String> errMsg = new ArrayList<>();
        Map<String, List<DeviceIotOfExcel>> nameMap = list.stream().collect(Collectors.groupingBy(DeviceIotOfExcel::getDeviceName));
        Map<String, List<DeviceIotOfExcel>> codeMap = list.stream().collect(Collectors.groupingBy(DeviceIotOfExcel::getDeviceCode));
        Map<String, List<DeviceIotOfExcel>> outIdMap = list.stream().collect(Collectors.groupingBy(DeviceIotOfExcel::getBizDeviceId));
        nameMap.forEach((s, devices) -> {
            if (devices.size() > 1) {
                errMsg.add(String.format("%s:有重复的设备名称", s));
            }
        });
        codeMap.forEach((s, devices) -> {
            if (devices.size() > 1) {
                errMsg.add(String.format("%s:有重复的设备编码", s));
            }
        });
        outIdMap.forEach((s, devices) -> {
            if (devices.size() > 1) {
                errMsg.add(String.format("%s:有重复的外部ID", s));
            }
        });
        return errMsg;
    }


    @Override
    public List<String> importFile(MultipartFile file, Long productId) throws IOException {
        TenantContext.setIgnore(true);
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());

        reader.addHeaderAlias("所属项目", "projectName");
        reader.addHeaderAlias("所属产品", "productName");
        reader.addHeaderAlias("设备名称", "deviceName");
        reader.addHeaderAlias("设备编码", "deviceCode");
        reader.addHeaderAlias("外部ID", "bizDeviceId");
        reader.addHeaderAlias("所属空间", "spaceName");
        reader.addHeaderAlias("设备位置", "locationDesc");
        reader.addHeaderAlias("设备描述", "deviceDesc");

        List<DeviceIotOfExcel> list = reader.readAll(DeviceIotOfExcel.class);
        List<String> errMsgList = new ArrayList<>();
        List<DeviceIotEntity> devices = new ArrayList<>();
        int index = 1;
        //判断设备是否重复
        errMsgList = checkRepeatDevice(list);
        if (errMsgList != null && errMsgList.size() > 0)
            return errMsgList;

        for (DeviceIotOfExcel device : list) {
            //查找项目
            ProjectEntity project = deviceIotMapper.getProject(device.getProjectName());
            //匹配产品
            ProductEntity product = deviceIotMapper.getProduct(device.getProductName());
            //匹配空间
            ProjectSpaceEntity space = deviceIotMapper.getSpace(device.getSpaceName(), project.getId());
            String bizDeviceId = bizSequenceService.next(BizSequenceEnum.DEVICE);
            String errMsg = this.getErrMsg(index, device, project, product, space, bizDeviceId);
            if (errMsg == null) {
                //组装device entity
                DeviceIotEntity deviceMonitorEntity = new DeviceIotEntity();
                deviceMonitorEntity.setBizDeviceId(bizSequenceService.next(BizSequenceEnum.DEVICE));
                deviceMonitorEntity.setSourceDeviceId(device.getBizDeviceId());
                deviceMonitorEntity.setDeviceDesc(device.getDeviceDesc());
                deviceMonitorEntity.setBizAreaId(String.valueOf(space.getId()));
                deviceMonitorEntity.setBizProductId(product.getBizId());
                deviceMonitorEntity.setCode(device.getDeviceCode());
                deviceMonitorEntity.setLocationDesc(device.getLocationDesc());
                deviceMonitorEntity.setName(device.getDeviceName());
                deviceMonitorEntity.setBizCategoryId(product.getCategoryId());
                deviceMonitorEntity.setBizProjectId(project.getBizProjectId());
                deviceMonitorEntity.setProjectName(project.getName());
                deviceMonitorEntity.setProjectId(project.getId());
                deviceMonitorEntity.setProductId(product.getId());
                devices.add(deviceMonitorEntity);
            } else {
                errMsgList.add(errMsg);
            }
            index++;
        }

        //List<Object> column1 = reader.readColumn(8,1);
        if (errMsgList != null && errMsgList.size() > 0) {
            return errMsgList;
        } else {
            if (devices != null && devices.size() > 0) {
                //其他参数
                List<ProductDeviceParameterEntity> parameters = productDeviceParameterMapper.getParameters(productId);
                List<DeviceParameterDetailEntity> dpdes = new ArrayList<>();
                for (int i = 1; i <= devices.size(); i++) {
//                  deviceIotMapper.insert(devices.get(i-1));
                    int colunNumBegin = 7;
                    for (int j = 1; j <= parameters.size(); j++) {
                        Object value = reader.readColumn(colunNumBegin + j, i, i).get(0);
                        String code = parameters.get(j - 1).getIdentifier();
                        ProductDeviceParameterEntity pdpe = productDeviceParameterMapper.getParameter(productId, code);
                        DeviceParameterDetailEntity dpde = new DeviceParameterDetailEntity();
                        BeanUtils.copyProperties(pdpe, dpde);
                        dpde.setId(null);
                        dpde.setBizDeviceId(devices.get(i - 1).getBizDeviceId());
                        dpde.setValue(value.toString());
                        dpdes.add(dpde);
                    }
                }
                TenantContext.setIgnore(false);
                devices.forEach(device -> deviceIotMapper.insert(device));

                dpdes.forEach(entity -> deviceParameterDetailMapper.insert(entity));
                //同步物联平台
                devices.forEach(device -> {
                    DeviceMonitorVO vo = new DeviceMonitorVO();
                    BeanUtils.copyProperties(device, vo);
                    monitorApi.add(vo);
                });
            }
            return errMsgList;
        }

    }

    @Override
    public void deleteParameters(Long id) {
        TenantContext.setIgnore(true);
        DeviceIotEntity entity = deviceIotMapper.selectById(id);
        deviceParameterDetailMapper.delete(new LambdaQueryWrapper<DeviceParameterDetailEntity>().eq(DeviceParameterDetailEntity::getBizDeviceId, entity.getBizDeviceId()));
    }

    @Override
    public List<ProductDeviceParameterEntity> getParameters(Long productId) {
        return productDeviceParameterMapper.getParameters(productId);
    }
}
