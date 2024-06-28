package com.landleaf.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.bms.api.dto.ProductDetailResponse;
import com.landleaf.bms.api.enums.AlarmConfirmTypeEnum;
import com.landleaf.bms.api.enums.AlarmLevelEnum;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.engine.dal.mapper.RuleActionMapper;
import com.landleaf.engine.domain.dto.RuleActionAddDTO;
import com.landleaf.engine.domain.dto.RuleActionQueryDTO;
import com.landleaf.engine.domain.entity.RuleActionEntity;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.enums.RuleActionType;
import com.landleaf.engine.service.RuleActionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RuleActionEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-04-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class RuleActionServiceImpl extends ServiceImpl<RuleActionMapper, RuleActionEntity> implements RuleActionService {

    /**
     * 数据库操作句柄
     */
    private final RuleActionMapper ruleActionMapper;

    private final ProductApi productApi;

    private final DeviceIotApi deviceIotApi;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleActionAddDTO save(RuleActionAddDTO addInfo) {
        RuleActionEntity entity = new RuleActionEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = ruleActionMapper.insert(entity);
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
    public void update(RuleActionAddDTO updateInfo) {
        RuleActionEntity entity = ruleActionMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        ruleActionMapper.updateById(entity);
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
        ruleActionMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleActionEntity selectById(Long id) {
        RuleActionEntity entity = ruleActionMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleActionEntity> list(RuleActionQueryDTO queryInfo) {
        return ruleActionMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<RuleActionEntity> page(RuleActionQueryDTO queryInfo) {
        IPage<RuleActionEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = ruleActionMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public void deleteByBizRuleId(String bizRuleId) {
        ruleActionMapper.delete(new QueryWrapper<RuleActionEntity>().lambda().eq(RuleActionEntity::getBizRuleId, bizRuleId));
    }

    @Override
    public RuleActionVO selectByRuleId(String bizRuleId) {
        RuleActionVO result = new RuleActionVO();
        RuleActionEntity entity = ruleActionMapper.selectOne(new QueryWrapper<RuleActionEntity>().lambda().eq(RuleActionEntity::getBizRuleId, bizRuleId));
        if (null == entity) {
            return result;
        }
        BeanUtil.copyProperties(entity, result, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        result.setActionTypeDesc(RuleActionType.getDescMap().get(result.getActionType()));
        if ("0".equals(result.getActionBizProdId())) {
            // 本产品
            result.setActionBizProdName("当前产品");
        } else {
            Response<List<ProductDetailResponse>> prodResp = productApi.getByBizids(Arrays.asList(result.getActionBizProdId()));
            if (prodResp.isSuccess() && null != prodResp.getResult() && 1 == prodResp.getResult().size()) {
                result.setActionBizProdName(prodResp.getResult().get(0).getName());
            }
        }
        if ("0".equals(result.getActionBizDeviceId())) {
            // 本设别
            result.setActionBizDeviceName("当前设备");
        } else {
            Response<List<DeviceIoResponse>> deviceResp = deviceIotApi.searchDeviceIot(Arrays.asList(result.getActionBizDeviceId()));
            if (deviceResp.isSuccess() && null != deviceResp.getResult() && 1 == deviceResp.getResult().size()) {
                result.setActionBizDeviceName(deviceResp.getResult().get(0).getDeviceName());
            }
        }
        result.setAlarmTriggerLevelDesc(AlarmLevelEnum.getName(result.getAlarmTriggerLevel()));
        result.setAlarmRelapseLevelDesc(AlarmLevelEnum.getName(result.getAlarmRelapseLevel()));
        result.setAlarmRelapseConfirmTypeDesc(AlarmConfirmTypeEnum.getName(result.getAlarmRelapseConfirmType()));
        result.setAlarmTriggerConfirmTypeDesc(AlarmConfirmTypeEnum.getName(result.getAlarmTriggerConfirmType()));
        return result;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<RuleActionEntity> getCondition(RuleActionQueryDTO queryInfo) {
        LambdaQueryWrapper<RuleActionEntity> wrapper = new QueryWrapper<RuleActionEntity>().lambda().eq(RuleActionEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(RuleActionEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(RuleActionEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 规则编号
        if (null != queryInfo.getId()) {
            wrapper.eq(RuleActionEntity::getId, queryInfo.getId());
        }
        // 动作类型：见字典RULE_ACTION_TYPE
        if (!StringUtils.hasText(queryInfo.getActionType())) {
            wrapper.like(RuleActionEntity::getActionType, "%" + queryInfo.getActionType() + "%");
        }
        // 执行动作的产品业务id
        if (!StringUtils.hasText(queryInfo.getActionBizProdId())) {
            wrapper.like(RuleActionEntity::getActionBizProdId, "%" + queryInfo.getActionBizProdId() + "%");
        }
        // 执行动作的设备
        if (!StringUtils.hasText(queryInfo.getActionBizDeviceId())) {
            wrapper.like(RuleActionEntity::getActionBizDeviceId, "%" + queryInfo.getActionBizDeviceId() + "%");
        }
        // 规则的业务编码
        if (!StringUtils.hasText(queryInfo.getBizRuleId())) {
            wrapper.like(RuleActionEntity::getBizRuleId, "%" + queryInfo.getBizRuleId() + "%");
        }
        // 告警CODE
        if (!StringUtils.hasText(queryInfo.getAlarmCode())) {
            wrapper.like(RuleActionEntity::getAlarmCode, "%" + queryInfo.getAlarmCode() + "%");
        }
        // 告警触发等级 数据字典（ALARM_LEVEL）
        if (!StringUtils.hasText(queryInfo.getAlarmTriggerLevel())) {
            wrapper.like(RuleActionEntity::getAlarmTriggerLevel, "%" + queryInfo.getAlarmTriggerLevel() + "%");
        }
        // 告警复归等级 数据字典（ALARM_LEVEL）
        if (!StringUtils.hasText(queryInfo.getAlarmRelapseLevel())) {
            wrapper.like(RuleActionEntity::getAlarmRelapseLevel, "%" + queryInfo.getAlarmRelapseLevel() + "%");
        }
        // 告警触发确认方式 数据字典（ALARM_CONFIRM_TYPE）
        if (!StringUtils.hasText(queryInfo.getAlarmTriggerConfirmType())) {
            wrapper.like(RuleActionEntity::getAlarmTriggerConfirmType, "%" + queryInfo.getAlarmTriggerConfirmType() + "%");
        }
        // 告警触发描述
        if (!StringUtils.hasText(queryInfo.getAlarmTriggerDesc())) {
            wrapper.like(RuleActionEntity::getAlarmTriggerDesc, "%" + queryInfo.getAlarmTriggerDesc() + "%");
        }
        // 告警复归描述
        if (!StringUtils.hasText(queryInfo.getAlarmRelapseDesc())) {
            wrapper.like(RuleActionEntity::getAlarmRelapseDesc, "%" + queryInfo.getAlarmRelapseDesc() + "%");
        }
        // 告警复归确认方式 数据字典（ALARM_CONFIRM_TYPE）
        if (!StringUtils.hasText(queryInfo.getAlarmRelapseConfirmType())) {
            wrapper.like(RuleActionEntity::getAlarmRelapseConfirmType, "%" + queryInfo.getAlarmRelapseConfirmType() + "%");
        }
        // 服务id
        if (null != queryInfo.getServiceId()) {
            wrapper.eq(RuleActionEntity::getServiceId, queryInfo.getServiceId());
        }
        // 服务参数
        if (null != queryInfo.getServiceParameter()) {
            wrapper.eq(RuleActionEntity::getServiceParameter, queryInfo.getServiceParameter());
        }
        // 服务下发间隔时间
        if (null != queryInfo.getServiceSendingInterval()) {
            wrapper.eq(RuleActionEntity::getServiceSendingInterval, queryInfo.getServiceSendingInterval());
        }
        wrapper.orderByDesc(RuleActionEntity::getUpdateTime);
        return wrapper;
    }
}