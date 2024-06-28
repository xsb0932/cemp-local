package com.landleaf.energy.service.impl;

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
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.domain.dto.ProjectAddDTO;
import com.landleaf.energy.domain.dto.ProjectQueryDTO;
import com.landleaf.energy.domain.entity.ProjectEntity;
import com.landleaf.energy.domain.vo.rjd.KanbanRJDProjectVO;
import com.landleaf.energy.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {

    /**
     * 数据库操作句柄
     */
    private final ProjectMapper projectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectAddDTO save(ProjectAddDTO addInfo) {
        ProjectEntity entity = new ProjectEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectMapper.insert(entity);
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
    public void update(ProjectAddDTO updateInfo) {
        ProjectEntity entity = projectMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        projectMapper.updateById(entity);
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
        projectMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectEntity selectById(Long id) {
        ProjectEntity entity = projectMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectEntity> list(ProjectQueryDTO queryInfo) {
        return projectMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectEntity> page(ProjectQueryDTO queryInfo) {
        IPage<ProjectEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public KanbanRJDProjectVO getByBizProjectId(String bizProjectId) {
        KanbanRJDProjectVO info = new KanbanRJDProjectVO();
        LambdaQueryWrapper<ProjectEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectEntity::getBizProjectId, bizProjectId);
        List<ProjectEntity> projects = this.baseMapper.selectList(lqw);
        if (projects != null && projects.size() > 0) {
            info.setArea(projects.get(0).getArea().toString());
        }
        info.setNum(102);
        return info;

    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectEntity> getCondition(ProjectQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectEntity> wrapper = new QueryWrapper<ProjectEntity>().lambda().eq(ProjectEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(ProjectEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(ProjectEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 项目id
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectEntity::getId, queryInfo.getId());
        }
        // 项目业务id（全局唯一id）
        if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
            wrapper.like(ProjectEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
        }
        // 项目名称
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(ProjectEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // 项目编码
        if (!StringUtils.hasText(queryInfo.getCode())) {
            wrapper.like(ProjectEntity::getCode, "%" + queryInfo.getCode() + "%");
        }
        // 项目业态code（字典配置）
        if (!StringUtils.hasText(queryInfo.getBizType())) {
            wrapper.like(ProjectEntity::getBizType, "%" + queryInfo.getBizType() + "%");
        }
        // 面积
        if (null != queryInfo.getArea()) {
            wrapper.eq(ProjectEntity::getArea, queryInfo.getArea().doubleValue());
        }
        // 能源类型（固定类型，枚举or字典配置，前端多选，后台`,`拼接存储，例：1,2,3对应水/电/气）
        if (!StringUtils.hasText(queryInfo.getEnergyType())) {
            wrapper.like(ProjectEntity::getEnergyType, "%" + queryInfo.getEnergyType() + "%");
        }
        // 项目状态（字典配置 0规划 1建设 2运维）
        if (null != queryInfo.getStatus()) {
            wrapper.eq(ProjectEntity::getStatus, queryInfo.getStatus());
        }
        // 负责人
        if (!StringUtils.hasText(queryInfo.getDirector())) {
            wrapper.like(ProjectEntity::getDirector, "%" + queryInfo.getDirector() + "%");
        }
        // 负责人电话
        if (!StringUtils.hasText(queryInfo.getMobile())) {
            wrapper.like(ProjectEntity::getMobile, "%" + queryInfo.getMobile() + "%");
        }
        // 项目地址
        if (!StringUtils.hasText(queryInfo.getAddress())) {
            wrapper.like(ProjectEntity::getAddress, "%" + queryInfo.getAddress() + "%");
        }
        // 项目归属管理节点业务id
        if (!StringUtils.hasText(queryInfo.getParentBizNodeId())) {
            wrapper.like(ProjectEntity::getParentBizNodeId, "%" + queryInfo.getParentBizNodeId() + "%");
        }
        // 项目行政区域（tb_address）
        if (!StringUtils.hasText(queryInfo.getAddressCode())) {
            wrapper.like(ProjectEntity::getAddressCode, "%" + queryInfo.getAddressCode() + "%");
        }
        // 高德-纬度
        if (!StringUtils.hasText(queryInfo.getGdLatitude())) {
            wrapper.like(ProjectEntity::getGdLatitude, "%" + queryInfo.getGdLatitude() + "%");
        }
        // 高德-经度
        if (!StringUtils.hasText(queryInfo.getGdLongitude())) {
            wrapper.like(ProjectEntity::getGdLongitude, "%" + queryInfo.getGdLongitude() + "%");
        }
        // 租户id
        if (null != queryInfo.getTenantId()) {
            wrapper.eq(ProjectEntity::getTenantId, queryInfo.getTenantId());
        }
        // 权限路径path（冗余）
        if (!StringUtils.hasText(queryInfo.getPath())) {
            wrapper.like(ProjectEntity::getPath, "%" + queryInfo.getPath() + "%");
        }
        // 项目对应的管理节点业务id
        if (!StringUtils.hasText(queryInfo.getBizNodeId())) {
            wrapper.like(ProjectEntity::getBizNodeId, "%" + queryInfo.getBizNodeId() + "%");
        }
        wrapper.orderByDesc(ProjectEntity::getUpdateTime);
        return wrapper;
    }
}
