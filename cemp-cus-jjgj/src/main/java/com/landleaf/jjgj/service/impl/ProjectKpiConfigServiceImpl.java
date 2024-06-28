package com.landleaf.jjgj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.jjgj.dal.mapper.ProjectCnfSubareaMapper;
import com.landleaf.jjgj.dal.mapper.ProjectCnfSubitemMapper;
import com.landleaf.jjgj.dal.mapper.ProjectKpiConfigMapper;
import com.landleaf.jjgj.domain.dto.ProjectKpiConfigAddDTO;
import com.landleaf.jjgj.domain.dto.ProjectKpiConfigQueryDTO;
import com.landleaf.jjgj.domain.entity.ProjectKpiConfigEntity;
import com.landleaf.jjgj.domain.vo.ProjectKpiVO;
import com.landleaf.jjgj.domain.vo.ProjectKpiVODetail;
import com.landleaf.jjgj.service.ProjectKpiConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.landleaf.jjgj.domain.vo.*;
import com.landleaf.jjgj.domain.vo.rjd.*;
import com.landleaf.jjgj.domain.vo.station.*;
import com.landleaf.comm.vo.*;
import com.landleaf.jjgj.domain.entity.*;
/**
 * ProjectKpiConfigEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-25
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectKpiConfigServiceImpl extends ServiceImpl<ProjectKpiConfigMapper, ProjectKpiConfigEntity> implements ProjectKpiConfigService {

    /**
     * 数据库操作句柄
     */
    @Resource
    ProjectKpiConfigMapper projectKpiConfigMapper;

    @Resource
    ProjectCnfSubitemMapper projectCnfSubitemMapper;

    @Resource
    ProjectCnfSubareaMapper projectCnfSubareaMapper;


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectKpiConfigAddDTO save(ProjectKpiConfigAddDTO addInfo) {
        ProjectKpiConfigEntity entity = new ProjectKpiConfigEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectKpiConfigMapper.insert(entity);
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
    public void update(ProjectKpiConfigAddDTO updateInfo) {
        ProjectKpiConfigEntity entity = projectKpiConfigMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        projectKpiConfigMapper.updateById(entity);
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
        projectKpiConfigMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectKpiConfigEntity selectById(Long id) {
        ProjectKpiConfigEntity entity = projectKpiConfigMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectKpiConfigEntity> list(ProjectKpiConfigQueryDTO queryInfo) {
        return projectKpiConfigMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectKpiConfigEntity> page(ProjectKpiConfigQueryDTO queryInfo) {
        IPage<ProjectKpiConfigEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectKpiConfigMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectKpiConfigEntity> getCondition(ProjectKpiConfigQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectKpiConfigEntity> wrapper = new QueryWrapper<ProjectKpiConfigEntity>().lambda().eq(ProjectKpiConfigEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(ProjectKpiConfigEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(ProjectKpiConfigEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 指标ID
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectKpiConfigEntity::getId, queryInfo.getId());
        }
        // 指标code
        if (!StringUtils.hasText(queryInfo.getCode())) {
            wrapper.like(ProjectKpiConfigEntity::getCode, "%" + queryInfo.getCode() + "%");
        }
        // 指标名称
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(ProjectKpiConfigEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // f分项大类 水 气 电 ...
        if (!StringUtils.hasText(queryInfo.getKpiType())) {
            wrapper.like(ProjectKpiConfigEntity::getKpiType, "%" + queryInfo.getKpiType() + "%");
        }
        // 分项类型代码
        if (!StringUtils.hasText(queryInfo.getKpiSubtype())) {
            wrapper.like(ProjectKpiConfigEntity::getKpiSubtype, "%" + queryInfo.getKpiSubtype() + "%");
        }
        // 统计间隔-小时-1
        if (null != queryInfo.getStaIntervalHour()) {
            wrapper.eq(ProjectKpiConfigEntity::getStaIntervalHour, queryInfo.getStaIntervalHour());
        }
        // 统计间隔-日月年-1
        if (null != queryInfo.getStaIntervalYmd()) {
            wrapper.eq(ProjectKpiConfigEntity::getStaIntervalYmd, queryInfo.getStaIntervalYmd());
        }
        wrapper.orderByDesc(ProjectKpiConfigEntity::getUpdateTime);
        return wrapper;
    }

    @Override
    public List<ProjectKpiVO> getKpi() {
        List<ProjectKpiVO> kpis = new ArrayList<>();
        //维度
        ProjectKpiVO kpiWD = new ProjectKpiVO();
        String[] wds = new String[]{"项目面积", "项目名称", "租户"};
        List<ProjectKpiVODetail> kpiWds = new ArrayList<>();
        Arrays.asList(wds).forEach(s -> {
            ProjectKpiVODetail detail = new ProjectKpiVODetail();
            detail.setName(s);
            kpiWds.add(detail);
        });
        kpiWD.setDetails(kpiWds);
        kpiWD.setName("维度");
        kpis.add(kpiWD);
        //查询分项大类
        LambdaQueryWrapper<ProjectKpiConfigEntity> lqwPKCE = new LambdaQueryWrapper<>();

        List<ProjectKpiConfigEntity> kpiConfigs = this.baseMapper.selectList(lqwPKCE);
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigsType = kpiConfigs.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiType));
        for (Map.Entry<String, List<ProjectKpiConfigEntity>> entry : kpiConfigsType.entrySet()) {
            String kpiType = entry.getKey();
            List<ProjectKpiConfigEntity> kpiTypeDetail = entry.getValue();
            List<ProjectKpiVODetail> kpiTypeDetailVOs = kpiTypeDetail.stream().map(projectKpiConfigEntity -> {
                ProjectKpiVODetail detail = new ProjectKpiVODetail();
                detail.setName(projectKpiConfigEntity.getName());
                detail.setCode(projectKpiConfigEntity.getCode());
                return detail;
            }).collect(Collectors.toList());
            ProjectKpiVO kpi = new ProjectKpiVO();
            kpi.setName(kpiType);
            kpi.setDetails(kpiTypeDetailVOs);
            kpis.add(kpi);
        }

        //分项
        LambdaQueryWrapper<ProjectCnfSubitemEntity> lqwSubitem = new LambdaQueryWrapper<>();
        List<ProjectCnfSubitemEntity> subitems = projectCnfSubitemMapper.selectList(lqwSubitem);
        for (ProjectCnfSubitemEntity subitem : subitems) {
            List<ProjectKpiConfigEntity> kpiconfigs = this.projectKpiConfigMapper.getByKpiType(subitem.getKpiType());
            List<ProjectKpiVODetail> kpiTypeDetailVOs = kpiconfigs.stream().map(projectKpiConfigEntity -> {
                ProjectKpiVODetail detail = new ProjectKpiVODetail();
                detail.setName(projectKpiConfigEntity.getName());
                detail.setCode(projectKpiConfigEntity.getCode());
                return detail;
            }).collect(Collectors.toList());
            ProjectKpiVO kpi = new ProjectKpiVO();
            kpi.setName(subitem.getName());
            kpi.setDetails(kpiTypeDetailVOs);
            kpis.add(kpi);
        }

        //分区
        LambdaQueryWrapper<ProjectCnfSubareaEntity> lqwSubarea = new LambdaQueryWrapper<>();
        List<ProjectCnfSubareaEntity> subareas = projectCnfSubareaMapper.selectList(lqwSubarea);
        ProjectKpiVO kpiSubarea = new ProjectKpiVO();
        kpiSubarea.setName("分区");
        List<ProjectKpiVODetail> subareaDetails = new ArrayList<>();
        for (ProjectCnfSubareaEntity subarea : subareas) {
            ProjectKpiVODetail detail = new ProjectKpiVODetail();
            detail.setName(subarea.getName());
            subareaDetails.add(detail);
        }
        kpiSubarea.setDetails(subareaDetails);
        kpis.add(kpiSubarea);

        return kpis;


    }
}
