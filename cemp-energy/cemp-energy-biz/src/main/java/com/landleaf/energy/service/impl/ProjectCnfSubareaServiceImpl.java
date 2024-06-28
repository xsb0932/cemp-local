package com.landleaf.energy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.ProjectSpaceApi;
import com.landleaf.bms.api.dto.ProjectSpaceTreeApiResponse;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.DeviceMonitorMapper;
import com.landleaf.energy.dal.mapper.ProjectCnfSubareaMapper;
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.dal.mapper.ProjectSubareaDeviceMapper;
import com.landleaf.energy.domain.dto.ProjectCnfSubareaAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfSubareaQueryDTO;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.entity.ProjectEntity;
import com.landleaf.energy.domain.entity.ProjectSubareaDeviceEntity;
import com.landleaf.energy.domain.enums.DeviceComputeEnum;
import com.landleaf.energy.domain.enums.EnergyDictConstants;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import com.landleaf.energy.domain.vo.ProjectCnfSubareaVO;
import com.landleaf.energy.domain.wrapper.ProjectCnfSubareaWrapper;
import com.landleaf.energy.enums.KpiSubtypeSubareaEnum;
import com.landleaf.energy.service.ProjectCnfSubareaService;
import com.landleaf.redis.dict.DictUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ProjectCnfSubareaEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-25
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfSubareaServiceImpl extends ServiceImpl<ProjectCnfSubareaMapper, ProjectCnfSubareaEntity> implements ProjectCnfSubareaService {
    /**
     * 数据库操作句柄
     */
    private final ProjectCnfSubareaMapper projectCnfSubareaMapper;

    @Autowired
    ProjectSubareaDeviceMapper projectSubareaDeviceMapper;

    @Autowired
    DeviceMonitorMapper deviceMonitorMapper;

    @Autowired
    DictUtils dictUtils;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    ProjectSpaceApi projectSpaceApi;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCnfSubareaAddDTO save(ProjectCnfSubareaAddDTO addInfo) {
        ProjectCnfSubareaEntity entity = new ProjectCnfSubareaEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectCnfSubareaMapper.insert(entity);
        if (0 == effectNum) {
            // 插入失败
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
        }
        BeanUtil.copyProperties(entity, addInfo);
        return addInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProjectCnfSubareaAddDTO addInfo) {
        if (addInfo.getDevices() == null || addInfo.getDevices().size() == 0) {
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "需要绑定计量设备.");
        } else {
            addInfo.getDevices().forEach(device -> {
                if (org.apache.commons.lang3.StringUtils.isBlank(device.getBizDeviceId())) {
                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "计量设备名称不允许为空.");
                } else if (org.apache.commons.lang3.StringUtils.isBlank(device.getComputeTag())) {
                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "计量设备算法不允许为空.");
                }
            });
        }

        //获取码值
        addInfo.setType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_SUBAREA_TYPE, addInfo.getTypeCode()));
        addInfo.setKpiType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_TYPE, addInfo.getKpiTypeCode()));
        if (KpiSubtypeSubareaEnum.AREAWATER.getKpiTypeCode().equals(addInfo.getKpiTypeCode())) {
            addInfo.setKpiSubtype(KpiSubtypeSubareaEnum.AREAWATER.getKpiSubtype());
        } else {
            addInfo.setKpiSubtype(KpiSubtypeSubareaEnum.AREALOAD.getKpiSubtype());
        }
        this.save(addInfo);
        this.updatePath(addInfo);
        this.saveDevices(addInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ProjectCnfSubareaAddDTO addInfo) {
        if (addInfo.getDevices() == null || addInfo.getDevices().size() == 0) {
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "需要绑定计量设备.");
        } else {
            addInfo.getDevices().forEach(device -> {
                if (org.apache.commons.lang3.StringUtils.isBlank(device.getBizDeviceId())) {
                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "计量设备名称不允许为空.");
                } else if (org.apache.commons.lang3.StringUtils.isBlank(device.getComputeTag())) {
                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), "计量设备算法不允许为空.");
                }
            });
        }
        this.update(addInfo);
        addInfo.setType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_SUBAREA_TYPE, addInfo.getTypeCode()));
        addInfo.setKpiType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_TYPE, addInfo.getKpiTypeCode()));
        this.updatePath(addInfo);
        this.updateDevices(addInfo);
    }

    public void saveDevices(ProjectCnfSubareaAddDTO addInfo) {
        List<DeviceMonitorVO> devices = addInfo.getDevices();
        for (DeviceMonitorVO d : devices) {
            ProjectSubareaDeviceEntity subareaDevice = new ProjectSubareaDeviceEntity();
            subareaDevice.setDeviceId(d.getBizDeviceId());
            subareaDevice.setComputeTag(d.getComputeTag());
            subareaDevice.setSubareadId(addInfo.getId());
            subareaDevice.setTenantId(TenantContext.getTenantId());
            projectSubareaDeviceMapper.insert(subareaDevice);
        }
    }

    public void updatePath(ProjectCnfSubareaAddDTO addInfo) {
        ProjectCnfSubareaEntity parent = projectCnfSubareaMapper.selectById(addInfo.getParentId());
        String path = parent == null ? "/".concat(String.valueOf(addInfo.getId())) : parent.getPath().concat("/").concat(String.valueOf(addInfo.getId()));
        projectCnfSubareaMapper.updatePath(addInfo.getId(), path);
    }

    public void updateDevices(ProjectCnfSubareaAddDTO addInfo) {
        //删除绑定的
        projectCnfSubareaMapper.unBind(addInfo.getId());
        //重新绑定
        this.saveDevices(addInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectCnfSubareaAddDTO updateInfo) {
        ProjectCnfSubareaEntity entity = projectCnfSubareaMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        projectCnfSubareaMapper.updateById(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        String[] idArray = ids.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String id : idArray) {
            idList.add(Long.valueOf(id));
        }
        projectCnfSubareaMapper.updateIsDeleted(idList, isDeleted);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        projectCnfSubareaMapper.unBind(Long.valueOf(id));
        projectCnfSubareaMapper.phDelete(Long.valueOf(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCnfSubareaEntity selectById(Long id) {
        ProjectCnfSubareaEntity entity = projectCnfSubareaMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectCnfSubareaEntity> list(ProjectCnfSubareaQueryDTO queryInfo) {
        return projectCnfSubareaMapper.selectList(getCondition(queryInfo));
    }

    @Override
    public List<ProjectCnfSubareaVO> listAll(String bizProjectId, String kpiTypeCode) {
        List<ProjectCnfSubareaEntity> entityAll = this.projectCnfSubareaMapper.selectList(new LambdaQueryWrapper<ProjectCnfSubareaEntity>().eq(ProjectCnfSubareaEntity::getProjectId, bizProjectId).eq(ProjectCnfSubareaEntity::getKpiTypeCode, kpiTypeCode).orderByAsc(ProjectCnfSubareaEntity::getId));
        if (entityAll == null || entityAll.size() == 0)
            return null;

        //分区下绑定的所有设备
        List<Long> subareaIds = entityAll.stream().map(ProjectCnfSubareaEntity::getId).collect(Collectors.toList());
        TenantContext.setIgnore(true);
//		List<ProjectSubareaDeviceEntity> subareaDevices =  projectSubareaDeviceMapper.selectList(new LambdaQueryWrapper<ProjectSubareaDeviceEntity>().in(ProjectSubareaDeviceEntity::getSubareadId,subareaIds));
        List<ProjectSubareaDeviceEntity> subareaDevices = projectSubareaDeviceMapper.getAvaDevices(TenantContext.getTenantId());
        List<String> deviceIds = subareaDevices.stream().distinct().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList());
        TenantContext.setIgnore(false);
        List<DeviceMonitorEntity> devices = deviceIds == null || deviceIds.isEmpty() ? new ArrayList<>() : deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>().in(DeviceMonitorEntity::getBizDeviceId, deviceIds));
        Map<String, DeviceMonitorEntity> deviceMap = devices.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, t -> t));

        Map<Long, List<ProjectSubareaDeviceEntity>> subareaDeviceMap = subareaDevices.stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getSubareadId));
        return getChildren(null, entityAll, subareaDeviceMap, deviceMap);
    }

    private List<ProjectCnfSubareaVO> getChildren(String areaId, List<ProjectCnfSubareaEntity> all, Map<Long, List<ProjectSubareaDeviceEntity>> subareaDeviceMap, Map<String, DeviceMonitorEntity> deviceMap) {
        List<ProjectCnfSubareaEntity> filterList = all.stream().filter(r -> Objects.equals(areaId, r.getParentId() == null ? null : String.valueOf(r.getParentId()))).collect(Collectors.toList());
        List<ProjectCnfSubareaVO> result = filterList.stream().map(r -> {
            ProjectCnfSubareaVO vo = new ProjectCnfSubareaVO();
            BeanUtils.copyProperties(r, vo);
            vo.setDevices(getMonitorDevices(r.getId(), subareaDeviceMap, deviceMap));
            if (vo.getDevices() != null && vo.getDevices().size() > 0) {
                vo.setDevicesDesc(getDesc(vo.getDevices()));
            }

            vo.setChildren(getChildren(String.valueOf(r.getId()), all, subareaDeviceMap, deviceMap));
            return vo;
        }).collect(Collectors.toList());
        return result.size() > 0 ? result : null;
    }

    private List<DeviceMonitorVO> getMonitorDevices(Long subareaId, Map<Long, List<ProjectSubareaDeviceEntity>> subareaDeviceMap, Map<String, DeviceMonitorEntity> deviceMap) {
        return subareaDeviceMap.get(subareaId) == null ? null : subareaDeviceMap.get(subareaId).stream().map(psd -> {
            DeviceMonitorVO vo = new DeviceMonitorVO();
            BeanUtils.copyProperties(deviceMap.get(psd.getDeviceId()), vo);
            vo.setComputeTag(psd.getComputeTag());
            return vo;
        }).collect(Collectors.toList());

    }

    private String getDesc(List<DeviceMonitorVO> devices) {
        StringBuilder desc = new StringBuilder();
        devices.forEach(d -> desc.append(d.getComputeTag().equals(DeviceComputeEnum.ADD.getType()) ? DeviceComputeEnum.ADD.getName() : DeviceComputeEnum.SUB.getName()).append(d.getName()));
        return desc.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectCnfSubareaEntity> page(ProjectCnfSubareaQueryDTO queryInfo) {
        IPage<ProjectCnfSubareaEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectCnfSubareaMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public ProjectCnfSubareaVO detail(Long id) {
        ProjectCnfSubareaEntity subareaEntity = this.selectById(id);
        ProjectCnfSubareaVO subareaVO = ProjectCnfSubareaWrapper.builder().entity2VO(subareaEntity);
        //查询绑定的设备
        List<DeviceMonitorVO> subs = projectSubareaDeviceMapper.getSubs(id);
        subareaVO.setDevices(subs);
        return subareaVO;
    }

    @Override
    public List<DeviceMonitorEntity> allDevices(String bizProjectId) {
        return deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>().eq(DeviceMonitorEntity::getBizProjectId, bizProjectId));
    }

    @Override
    public void batchImport(String bizProjectId) {
        ProjectEntity project = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>().eq(ProjectEntity::getBizProjectId, bizProjectId));
        //读取空间
        List<ProjectSpaceTreeApiResponse> reponse = projectSpaceApi.searchSpaces(project.getId()).getResult();
        if (reponse != null && reponse.size() > 0) {
            reponse.forEach(space -> dealSpace(reponse, null, project));
        }
    }

    void dealSpace(List<ProjectSpaceTreeApiResponse> spaces, String parentId, ProjectEntity project) {
        spaces.forEach(new Consumer<ProjectSpaceTreeApiResponse>() {
            @Override
            public void accept(ProjectSpaceTreeApiResponse space) {
                //写入分区
                ProjectCnfSubareaEntity subarea = new ProjectCnfSubareaEntity();
                subarea.setName(space.getSpaceName());
                subarea.setTypeCode(space.getSpaceType());
                subarea.setType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_SUBAREA_TYPE, space.getSpaceType()));
                subarea.setParentId(parentId);
                subarea.setProjectId(project.getBizProjectId());
                subarea.setKpiSubtype("areaLoad");
                projectCnfSubareaMapper.insert(subarea);
                if (space.getChildren() != null && space.getChildren().size() > 0) {
                    dealSpace(space.getChildren(), String.valueOf(subarea.getId()), project);
                }
            }
        });
    }


    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectCnfSubareaEntity> getCondition(ProjectCnfSubareaQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectCnfSubareaEntity> wrapper = new QueryWrapper<ProjectCnfSubareaEntity>().lambda().eq(ProjectCnfSubareaEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

        // 开始时间
        if (!StringUtils.isEmpty(queryInfo.getStartTime())) {
            long startTimeMillion = 0L;
            try {
                startTimeMillion = DateUtils
                        .parseDate(queryInfo.getStartTime() + " 00:00:00")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，startTime不符合格式{}", queryInfo.getStartTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.le(ProjectCnfSubareaEntity::getCreateTime, new Timestamp(startTimeMillion));
        }

        // 结束时间
        if (!StringUtils.isEmpty(queryInfo.getEndTime())) {
            long endTimeMillion = 0L;
            try {
                endTimeMillion = DateUtils
                        .parseDate(queryInfo.getEndTime() + " 23:59:59")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，endTime不符合格式{}", queryInfo.getEndTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.ge(ProjectCnfSubareaEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 分区id
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectCnfSubareaEntity::getId, queryInfo.getId());
        }
        // 分区名称
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(ProjectCnfSubareaEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // 分区类型
        if (!StringUtils.hasText(queryInfo.getType())) {
            wrapper.like(ProjectCnfSubareaEntity::getType, "%" + queryInfo.getType() + "%");
        }
        // 父ID
        if (null != queryInfo.getParentId()) {
            wrapper.eq(ProjectCnfSubareaEntity::getParentId, queryInfo.getParentId());
        }
        // 租户ID
        if (!StringUtils.hasText(queryInfo.getPath())) {
            wrapper.like(ProjectCnfSubareaEntity::getPath, "%" + queryInfo.getPath() + "%");
        }
        // 租户ID
        if (null != queryInfo.getTenantId()) {
            wrapper.eq(ProjectCnfSubareaEntity::getTenantId, queryInfo.getTenantId());
        }
        // 项目ID
        if (!StringUtils.hasText(queryInfo.getProjectId())) {
            wrapper.like(ProjectCnfSubareaEntity::getProjectId, "%" + queryInfo.getProjectId() + "%");
        }
        // 指标大类
        if (!StringUtils.hasText(queryInfo.getKpiType())) {
            wrapper.like(ProjectCnfSubareaEntity::getKpiType, "%" + queryInfo.getKpiType() + "%");
        }
        // 分区指标代码
        if (!StringUtils.hasText(queryInfo.getKpiSubtype())) {
            wrapper.like(ProjectCnfSubareaEntity::getKpiSubtype, "%" + queryInfo.getKpiSubtype() + "%");
        }
        wrapper.orderByDesc(ProjectCnfSubareaEntity::getUpdateTime);
        return wrapper;
    }
}
