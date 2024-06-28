package com.landleaf.energy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.DeviceMonitorMapper;
import com.landleaf.energy.dal.mapper.ProjectCnfSubitemMapper;
import com.landleaf.energy.dal.mapper.ProjectKpiConfigMapper;
import com.landleaf.energy.dal.mapper.ProjectSubitemDeviceMapper;
import com.landleaf.energy.domain.dto.ProjectCnfSubitemAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfSubitemQueryDTO;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;
import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import com.landleaf.energy.domain.enums.DeviceComputeEnum;
import com.landleaf.energy.domain.enums.EnergyDictConstants;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import com.landleaf.energy.domain.vo.ProjectCnfSubitemVO;
import com.landleaf.energy.domain.wrapper.ProjectCnfSubitemWrapper;
import com.landleaf.energy.service.ProjectCnfSubitemService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProjectCnfSubitemEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-25
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfSubitemServiceImpl extends ServiceImpl<ProjectCnfSubitemMapper, ProjectCnfSubitemEntity> implements ProjectCnfSubitemService {

    /**
     * 数据库操作句柄
     */
    private final ProjectCnfSubitemMapper projectCnfSubitemMapper;

    @Autowired
    ProjectSubitemDeviceMapper projectSubitemDeviceMapper;

    @Autowired
    DeviceMonitorMapper deviceMonitorMapper;

    @Autowired
    ProjectKpiConfigMapper projectKpiConfigMapper;

    @Autowired
    DictUtils dictUtils;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCnfSubitemAddDTO save(ProjectCnfSubitemAddDTO addInfo) {
        ProjectCnfSubitemEntity entity = new ProjectCnfSubitemEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectCnfSubitemMapper.insert(entity);
        if (0 == effectNum) {
            // 插入失败
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
        }
        BeanUtil.copyProperties(entity, addInfo);
        return addInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectCnfSubitemAddDTO updateInfo) {
        ProjectCnfSubitemEntity entity = projectCnfSubitemMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        projectCnfSubitemMapper.updateById(entity);
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
        projectCnfSubitemMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCnfSubitemEntity selectById(Long id) {
        ProjectCnfSubitemEntity entity = projectCnfSubitemMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectCnfSubitemEntity> list(ProjectCnfSubitemQueryDTO queryInfo) {
        return projectCnfSubitemMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectCnfSubitemEntity> page(ProjectCnfSubitemQueryDTO queryInfo) {
        IPage<ProjectCnfSubitemEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectCnfSubitemMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProjectCnfSubitemAddDTO addInfo) {
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
        //分项类型
        TenantContext.setIgnore(true);
        ProjectKpiConfigEntity kpiConfig = projectKpiConfigMapper.selectList(new LambdaQueryWrapper<ProjectKpiConfigEntity>().eq(ProjectKpiConfigEntity::getKpiSubtype, addInfo.getKpiSubtype())).get(0);
        addInfo.setKpiTypeCode(kpiConfig.getKpiTypeCode());
        addInfo.setKpiType(kpiConfig.getKpiType());
        TenantContext.setIgnore(false);
        this.save(addInfo);
        this.updatePath(addInfo);
        this.saveDevices(addInfo);
        // 特殊逻辑 - 添加光伏负荷 - 同时增加光伏的碳排指标
        if ("pvEnergy".equals(addInfo.getKpiSubtype())) {
            this.addRela(addInfo, "carbonReductionPv");
        }
        // 特殊逻辑 - 添加项目总负荷 - 同时增加 电耗等效排放和 项目总排放
        if ("loadAll".equals(addInfo.getKpiSubtype())) {
            this.addRela(addInfo, "carbonEmissionLoad");
            this.addRela(addInfo, "carbonEmissionAll");
        }
    }

    private void addRela(ProjectCnfSubitemAddDTO addInfo, String subtypeCode) {
        ProjectCnfSubitemAddDTO newDto = new ProjectCnfSubitemAddDTO();
        BeanUtils.copyProperties(addInfo, newDto);
        newDto.setParentId(addInfo.getId().toString());
        newDto.setId(null);
        newDto.setKpiSubtype(subtypeCode);
        newDto.setKpiType("碳");
        newDto.setKpiTypeCode("4");
        newDto.setName(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_SUBITEM_TYPE, subtypeCode));
        this.save(newDto);
        projectCnfSubitemMapper.updatePath(newDto.getId(), newDto.getPath().concat("/").concat(String.valueOf(newDto.getId())));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ProjectCnfSubitemAddDTO addInfo) {
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
        addInfo.setKpiType(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_TYPE, addInfo.getKpiTypeCode()));
        this.updatePath(addInfo);
        this.updateDevices(addInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        projectCnfSubitemMapper.unBind(Long.valueOf(id));
        projectCnfSubitemMapper.phDelete(Long.valueOf(id));
    }

    @Override
    public ProjectCnfSubitemVO detail(Long id) {
        ProjectCnfSubitemEntity subitemEntity = this.selectById(id);
        ProjectCnfSubitemVO subitemVO = ProjectCnfSubitemWrapper.builder().entity2VO(subitemEntity);
        //查询绑定的设备
        List<DeviceMonitorVO> subs = projectSubitemDeviceMapper.getSubs(id);
        subitemVO.setDevices(subs);
        return subitemVO;
    }

    @Override
    public List<ProjectCnfSubitemVO> listAll(String bizProjectId, String kpiTypeCode) {
        String[] notShowList = new String[]{"carbonEmissionAll", "carbonEmissionGas", "carbonEmissionLoad", "carbonEmissionWater", "carbonReductionPv"};
        List<ProjectCnfSubitemEntity> entityAll = this.projectCnfSubitemMapper.selectList(new LambdaQueryWrapper<ProjectCnfSubitemEntity>()
                .eq(ProjectCnfSubitemEntity::getProjectId, bizProjectId)
                .eq(ProjectCnfSubitemEntity::getKpiTypeCode, kpiTypeCode)
                .notIn(ProjectCnfSubitemEntity::getKpiSubtype, Arrays.asList(notShowList)).orderByAsc(ProjectCnfSubitemEntity::getId)
        );
        if (entityAll == null || entityAll.size() == 0)
            return null;

        //分区下绑定的所有设备
        List<Long> subitemIds = entityAll.stream().map(ProjectCnfSubitemEntity::getId).collect(Collectors.toList());
        //List<ProjectSubitemDeviceEntity> subitemDevices =  projectSubitemDeviceMapper.selectList(new LambdaQueryWrapper<ProjectSubitemDeviceEntity>().in(ProjectSubitemDeviceEntity::getSubitemId,subitemIds));
        List<ProjectSubitemDeviceEntity> subitemDevices = projectSubitemDeviceMapper.listAllValid(subitemIds);
        List<String> deviceIds = subitemDevices.stream().distinct().map(ProjectSubitemDeviceEntity::getDeviceId).collect(Collectors.toList());
        List<DeviceMonitorEntity> devices = CollectionUtil.isEmpty(deviceIds) ? new ArrayList<DeviceMonitorEntity>() : deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>().in(DeviceMonitorEntity::getBizDeviceId, deviceIds));
        Map<String, DeviceMonitorEntity> deviceMap = devices.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, t -> t));

        Map<Long, List<ProjectSubitemDeviceEntity>> subitemDeviceMap = subitemDevices.stream().collect(Collectors.groupingBy(ProjectSubitemDeviceEntity::getSubitemId));
        return getChildren(null, entityAll, subitemDeviceMap, deviceMap);
    }

    @Override
    public String kpiDesc(String code) {
        String kpiStr = dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_SUBITEM_TYPE, code);
        return String.format("该类KPI统计: %s 小时统计指标:用电量; 日月年统计指标:用电量", kpiStr);
    }

    private List<ProjectCnfSubitemVO> getChildren(String subitemId, List<ProjectCnfSubitemEntity> all, Map<Long, List<ProjectSubitemDeviceEntity>> subitemDeviceMap, Map<String, DeviceMonitorEntity> deviceMap) {
        List<ProjectCnfSubitemEntity> filterList = all.stream().filter(r -> Objects.equals(subitemId, r.getParentId() == null ? null : String.valueOf(r.getParentId()))).collect(Collectors.toList());
        List<ProjectCnfSubitemVO> result = filterList.stream().map(r -> {
            ProjectCnfSubitemVO vo = new ProjectCnfSubitemVO();
            BeanUtils.copyProperties(r, vo);
            vo.setDevices(getMonitorDevices(r.getId(), subitemDeviceMap, deviceMap));
            if (vo.getDevices() != null && vo.getDevices().size() > 0) {
                vo.setDevicesDesc(getDesc(vo.getDevices()));
            }
            vo.setKpiSubtypeStr(dictUtils.selectDictLabel(EnergyDictConstants.ENERGY_DICT_CNF_KPI_SUBITEM_TYPE, vo.getKpiSubtype()));
            vo.setChildren(getChildren(String.valueOf(r.getId()), all, subitemDeviceMap, deviceMap));
            return vo;
        }).collect(Collectors.toList());
        return result.size() > 0 ? result : null;
    }

    private String getDesc(List<DeviceMonitorVO> devices) {
        StringBuilder desc = new StringBuilder();
        devices.forEach(d -> desc.append(d.getComputeTag().equals(DeviceComputeEnum.ADD.getType()) ? DeviceComputeEnum.ADD.getName() : DeviceComputeEnum.SUB.getName()).append(d.getName()));
        return desc.toString();
    }

    private List<DeviceMonitorVO> getMonitorDevices(Long subitemId, Map<Long, List<ProjectSubitemDeviceEntity>> subitemDeviceMap, Map<String, DeviceMonitorEntity> deviceMap) {
        if (subitemDeviceMap.get(subitemId) == null) {
            return null;
        } else {
            return subitemDeviceMap.get(subitemId).stream().map(psd -> {
                DeviceMonitorVO vo = new DeviceMonitorVO();
                BeanUtils.copyProperties(deviceMap.get(psd.getDeviceId()), vo);
                vo.setComputeTag(psd.getComputeTag());
                return vo;
            }).collect(Collectors.toList());
        }


    }

    public void updatePath(ProjectCnfSubitemAddDTO addInfo) {
        ProjectCnfSubitemEntity parent = projectCnfSubitemMapper.selectById(addInfo.getParentId());
        String path = parent == null ? "/".concat(String.valueOf(addInfo.getId())) : parent.getPath().concat("/").concat(String.valueOf(addInfo.getId()));
        addInfo.setPath(path);
        projectCnfSubitemMapper.updatePath(addInfo.getId(), path);
    }

    public void saveDevices(ProjectCnfSubitemAddDTO addInfo) {
        List<DeviceMonitorVO> devices = addInfo.getDevices();
        for (DeviceMonitorVO d : devices) {
            ProjectSubitemDeviceEntity subitemDevice = new ProjectSubitemDeviceEntity();
            subitemDevice.setDeviceId(d.getBizDeviceId());
            subitemDevice.setComputeTag(d.getComputeTag());
            subitemDevice.setSubitemId(addInfo.getId());
            subitemDevice.setTenantId(TenantContext.getTenantId());
            projectSubitemDeviceMapper.insert(subitemDevice);
        }
    }

    public void updateDevices(ProjectCnfSubitemAddDTO addInfo) {
        //删除绑定的
        projectCnfSubitemMapper.unBind(addInfo.getId());
        //重新绑定
        this.saveDevices(addInfo);
    }

    @Override
    public Long queryIdByKpiCode(String kpiCode, ProjectCnfSubitemEntity entity) {
        TenantContext.setIgnore(true);
        Long id = projectCnfSubitemMapper.queryIdByKpiCode(kpiCode, entity.getProjectId(), TenantContext.getTenantId());
        TenantContext.setIgnore(false);
        if (null == id) {
            // 插入
            projectCnfSubitemMapper.insert(entity);
            return entity.getId();
        }
        return id;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectCnfSubitemEntity> getCondition(ProjectCnfSubitemQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectCnfSubitemEntity> wrapper = new QueryWrapper<ProjectCnfSubitemEntity>().lambda().eq(ProjectCnfSubitemEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(ProjectCnfSubitemEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(ProjectCnfSubitemEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 分项id
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectCnfSubitemEntity::getId, queryInfo.getId());
        }
        // 分项名称
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(ProjectCnfSubitemEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // 父项ID
        if (!StringUtils.hasText(queryInfo.getParentId())) {
            wrapper.like(ProjectCnfSubitemEntity::getParentId, "%" + queryInfo.getParentId() + "%");
        }
        // 路径
        if (!StringUtils.hasText(queryInfo.getPath())) {
            wrapper.like(ProjectCnfSubitemEntity::getPath, "%" + queryInfo.getPath() + "%");
        }
        // 项目ID
        if (!StringUtils.hasText(queryInfo.getProjectId())) {
            wrapper.like(ProjectCnfSubitemEntity::getProjectId, "%" + queryInfo.getProjectId() + "%");
        }
        // 租户ID
        if (null != queryInfo.getTenantId()) {
            wrapper.eq(ProjectCnfSubitemEntity::getTenantId, queryInfo.getTenantId());
        }
        // 分项指标代码
        if (!StringUtils.hasText(queryInfo.getKpiSubtype())) {
            wrapper.like(ProjectCnfSubitemEntity::getKpiSubtype, "%" + queryInfo.getKpiSubtype() + "%");
        }
        // 指标大类
        if (!StringUtils.hasText(queryInfo.getKpiType())) {
            wrapper.like(ProjectCnfSubitemEntity::getKpiType, "%" + queryInfo.getKpiType() + "%");
        }
        wrapper.orderByDesc(ProjectCnfSubitemEntity::getUpdateTime);
        return wrapper;
    }
}
