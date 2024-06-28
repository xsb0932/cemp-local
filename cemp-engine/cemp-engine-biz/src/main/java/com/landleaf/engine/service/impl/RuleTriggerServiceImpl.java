package com.landleaf.engine.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.bms.api.dto.ProductDetailResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.engine.domain.vo.RuleTriggerVO;
import com.landleaf.engine.enums.RuleTriggerType;
import com.landleaf.engine.enums.TargetMessageType;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.engine.dal.mapper.RuleTriggerMapper;
import com.landleaf.engine.domain.dto.RuleTriggerAddDTO;
import com.landleaf.engine.domain.dto.RuleTriggerQueryDTO;
import com.landleaf.engine.domain.entity.RuleTriggerEntity;
import com.landleaf.engine.service.RuleTriggerService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RuleTriggerEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-04-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class RuleTriggerServiceImpl extends ServiceImpl<RuleTriggerMapper, RuleTriggerEntity> implements RuleTriggerService {

    /**
     * 数据库操作句柄
     */
    private final RuleTriggerMapper ruleTriggerMapper;

    private final ProductApi productApi;

    private final DeviceIotApi deviceIotApi;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleTriggerAddDTO save(RuleTriggerAddDTO addInfo) {
        RuleTriggerEntity entity = new RuleTriggerEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = ruleTriggerMapper.insert(entity);
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
    public void update(RuleTriggerAddDTO updateInfo) {
        RuleTriggerEntity entity = ruleTriggerMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        ruleTriggerMapper.updateById(entity);
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
        ruleTriggerMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleTriggerEntity selectById(Long id) {
        RuleTriggerEntity entity = ruleTriggerMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleTriggerEntity> list(RuleTriggerQueryDTO queryInfo) {
        return ruleTriggerMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<RuleTriggerEntity> page(RuleTriggerQueryDTO queryInfo) {
        IPage<RuleTriggerEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = ruleTriggerMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public void deleteByBizRuleId(String bizRuleId) {
        ruleTriggerMapper.delete(new QueryWrapper<RuleTriggerEntity>().lambda().eq(RuleTriggerEntity::getBizRuleId, bizRuleId));
    }

    @Override
    public RuleTriggerVO selectByRuleId(String bizRuleId) {
        RuleTriggerVO result = new RuleTriggerVO();
        RuleTriggerEntity entity = ruleTriggerMapper.selectOne(new QueryWrapper<RuleTriggerEntity>().lambda().eq(RuleTriggerEntity::getBizRuleId, bizRuleId));
        if (null == entity) {
            return result;
        }
        BeanUtil.copyProperties(entity, result, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        result.setMessageTypeDesc(TargetMessageType.getDescMap().get(result.getMessageType()));
        result.setTriggerTypeDesc(RuleTriggerType.getDescMap().get(result.getTriggerType()));
        // 查询产品编号
        if (StringUtils.hasText(result.getTargetBizProdId())) {
            Response<List<ProductDetailResponse>> prodResp = productApi.getByBizids(Arrays.asList(result.getTargetBizProdId()));
            if (prodResp.isSuccess() && null != prodResp.getResult() && 1 == prodResp.getResult().size()) {
                result.setTargetBizProdName(prodResp.getResult().get(0).getName());
            }
        }

        // 查询设备
        if (StringUtils.hasText(result.getTargetBizDeviceIds())) {
            String[] bizDeviceIds = result.getTargetBizDeviceIds().split(StrUtil.COMMA);
            Response<List<DeviceIoResponse>> deviceResp = deviceIotApi.searchDeviceIot(Arrays.asList(bizDeviceIds));
            if (deviceResp.isSuccess() && null != deviceResp.getResult()) {
                Map<String, String> nameMap = deviceResp.getResult().stream().collect(Collectors.toMap(DeviceIoResponse::getBizDeviceId, DeviceIoResponse::getDeviceName));
                StringBuilder sb = new StringBuilder();
                for (String bizDeviceId : bizDeviceIds) {
                    sb.append(StrUtil.nullToEmpty(nameMap.get(bizDeviceId))).append(",");
                }
                result.setTargetBizDeviceNames(sb.substring(0, sb.length() - 1));
            }
        }
        return result;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<RuleTriggerEntity> getCondition(RuleTriggerQueryDTO queryInfo) {
        LambdaQueryWrapper<RuleTriggerEntity> wrapper = new QueryWrapper<RuleTriggerEntity>().lambda().eq(RuleTriggerEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(RuleTriggerEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(RuleTriggerEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 规则编号
        if (null != queryInfo.getId()) {
            wrapper.eq(RuleTriggerEntity::getId, queryInfo.getId());
        }
        // 触发类型：见字典RULE_TRIGGER_TYPE
        if (!StringUtils.hasText(queryInfo.getTriggerType())) {
            wrapper.like(RuleTriggerEntity::getTriggerType, "%" + queryInfo.getTriggerType() + "%");
        }
        // 触发报文类型：见字典TARGET_MESSAGE_TYPE
        if (!StringUtils.hasText(queryInfo.getMessageType())) {
            wrapper.like(RuleTriggerEntity::getMessageType, "%" + queryInfo.getMessageType() + "%");
        }
        // 产品业务id
        if (!StringUtils.hasText(queryInfo.getTargetBizProdId())) {
            wrapper.like(RuleTriggerEntity::getTargetBizProdId, "%" + queryInfo.getTargetBizProdId() + "%");
        }
        // 触发设备，多个以逗号分隔
        if (!StringUtils.hasText(queryInfo.getTargetBizDeviceIds())) {
            wrapper.like(RuleTriggerEntity::getTargetBizDeviceIds, "%" + queryInfo.getTargetBizDeviceIds() + "%");
        }
        // 规则的业务编码
        if (!StringUtils.hasText(queryInfo.getBizRuleId())) {
            wrapper.like(RuleTriggerEntity::getBizRuleId, "%" + queryInfo.getBizRuleId() + "%");
        }
        // 重复时间，逗号分隔
        if (!StringUtils.hasText(queryInfo.getRepeatTime())) {
            wrapper.like(RuleTriggerEntity::getRepeatTime, "%" + queryInfo.getRepeatTime() + "%");
        }
        // 重复类型
        if (!StringUtils.hasText(queryInfo.getRepeatType())) {
            wrapper.like(RuleTriggerEntity::getRepeatType, "%" + queryInfo.getRepeatType() + "%");
        }
        // 触发时间
        if (!StringUtils.hasText(queryInfo.getTargetTime())) {
            wrapper.like(RuleTriggerEntity::getTargetTime, "%" + queryInfo.getTargetTime() + "%");
        }
        wrapper.orderByDesc(RuleTriggerEntity::getUpdateTime);
        return wrapper;
    }
}