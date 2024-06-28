package com.landleaf.engine.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.bms.api.dto.ProductDetailResponse;
import com.landleaf.bms.api.dto.ProductDeviceAttrResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.engine.domain.entity.RuleTriggerEntity;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.domain.vo.RuleTriggerVO;
import com.landleaf.engine.enums.ComparatorType;
import com.landleaf.engine.enums.RepeatType;
import com.landleaf.engine.enums.RuleConditionType;
import com.landleaf.engine.service.RuleTriggerService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.engine.dal.mapper.RuleConditionMapper;
import com.landleaf.engine.domain.dto.RuleConditionAddDTO;
import com.landleaf.engine.domain.dto.RuleConditionQueryDTO;
import com.landleaf.engine.domain.entity.RuleConditionEntity;
import com.landleaf.engine.service.RuleConditionService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RuleConditionEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-04-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class RuleConditionServiceImpl extends ServiceImpl<RuleConditionMapper, RuleConditionEntity> implements RuleConditionService {

    /**
     * 数据库操作句柄
     */
    private final RuleConditionMapper ruleConditionMapper;

    private final ProductApi productApi;

    private final DeviceIotApi deviceIotApi;

    private final RuleTriggerService ruleTriggerServiceImpl;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleConditionAddDTO save(RuleConditionAddDTO addInfo) {
        RuleConditionEntity entity = new RuleConditionEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = ruleConditionMapper.insert(entity);
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
    public void update(RuleConditionAddDTO updateInfo) {
        RuleConditionEntity entity = ruleConditionMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        ruleConditionMapper.updateById(entity);
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
        ruleConditionMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleConditionEntity selectById(Long id) {
        RuleConditionEntity entity = ruleConditionMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleConditionEntity> list(RuleConditionQueryDTO queryInfo) {
        return ruleConditionMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<RuleConditionEntity> page(RuleConditionQueryDTO queryInfo) {
        IPage<RuleConditionEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = ruleConditionMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public void deleteByBizRuleId(String bizRuleId) {
        ruleConditionMapper.delete(new QueryWrapper<RuleConditionEntity>().lambda().eq(RuleConditionEntity::getBizRuleId, bizRuleId));
    }

    @Override
    public List<RuleConditionVO> selectListByRuleId(String bizRuleId) {
        List<RuleConditionVO> result = new ArrayList<RuleConditionVO>();
        List<RuleConditionEntity> list = ruleConditionMapper.selectList(new QueryWrapper<RuleConditionEntity>().lambda().eq(RuleConditionEntity::getBizRuleId, bizRuleId).orderByAsc(RuleConditionEntity::getConditionType).orderByAsc(RuleConditionEntity::getId));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        RuleConditionVO temp = null;
        Map<String, String> conditionDescMap = RuleConditionType.getDescMap();
        Map<String, String> comparatorDescMap = ComparatorType.getDescMap();
        Map<String, String>repeatDescMap = RepeatType.getDescMap();
        for (RuleConditionEntity ruleConditionEntity : list) {
            temp = new RuleConditionVO();
            BeanUtil.copyProperties(ruleConditionEntity, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            temp.setComparatorDesc(comparatorDescMap.get(temp.getComparator()));
            temp.setConditionTypeDesc(conditionDescMap.get(temp.getConditionType()));
            temp.setRepeatTypeDesc(repeatDescMap.get(temp.getRepeatType()));
            if (RuleConditionType.DEVICE.getCode().equals(temp.getConditionType())) {
                String bizCorBizProdId = null;
                if ("0".equals(temp.getCorBizProdId())) {
                    // 本产品
                    temp.setCorBizProdName("当前产品");
                    // 获取trigger中的产品信息
                    RuleTriggerVO triggerVO = ruleTriggerServiceImpl.selectByRuleId(bizRuleId);
                    if (null != triggerVO) {
                        bizCorBizProdId = triggerVO.getTargetBizProdId();
                    }
                } else {
                    Response<List<ProductDetailResponse>> prodResp = productApi.getByBizids(Arrays.asList(temp.getCorBizProdId()));
                    if (prodResp.isSuccess() && null != prodResp.getResult() && 1 == prodResp.getResult().size()) {
                        temp.setCorBizProdName(prodResp.getResult().get(0).getName());
                    }
                }
                if ("0".equals(temp.getCorBizDeviceId())) {
                    // 本设别
                    temp.setCorBizDeviceName("当前设备");
                } else {
                    Response<List<DeviceIoResponse>> deviceResp = deviceIotApi.searchDeviceIot(Arrays.asList(temp.getCorBizDeviceId()));
                    if (deviceResp.isSuccess() && null != deviceResp.getResult() && 1 == deviceResp.getResult().size()) {
                        temp.setCorBizDeviceName(deviceResp.getResult().get(0).getDeviceName());
                    }
                }
                // 通过attrCode获取functionName;
                if (null != bizCorBizProdId) {
                    Response<Map<String, List<ProductDeviceAttrResponse>>> attrResp = productApi.getProjectAttrs(Arrays.asList(bizCorBizProdId));
                    String attrCode = temp.getAttrCode();
                    if (attrResp.isSuccess() && null != attrResp.getResult() && 1 == attrResp.getResult().size()) {
                        List<ProductDeviceAttrResponse> attrList = attrResp.getResult().get(bizCorBizProdId);
                        temp.setAttrCodeDesc(attrList.stream().filter(i -> i.getAttrCode().equals(attrCode)).map(ProductDeviceAttrResponse::getAttrName).findFirst().orElse(StrUtil.EMPTY));
                    }
                }
            }
            result.add(temp);
        }
        return result;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<RuleConditionEntity> getCondition(RuleConditionQueryDTO queryInfo) {
        LambdaQueryWrapper<RuleConditionEntity> wrapper = new QueryWrapper<RuleConditionEntity>().lambda().eq(RuleConditionEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(RuleConditionEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(RuleConditionEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 规则编号
        if (null != queryInfo.getId()) {
            wrapper.eq(RuleConditionEntity::getId, queryInfo.getId());
        }
        // 条件类型：见字典RULE_CONDITION_TYPE
        if (!StringUtils.hasText(queryInfo.getConditionType())) {
            wrapper.like(RuleConditionEntity::getConditionType, "%" + queryInfo.getConditionType() + "%");
        }
        // 关联产品业务id
        if (!StringUtils.hasText(queryInfo.getCorBizProdId())) {
            wrapper.like(RuleConditionEntity::getCorBizProdId, "%" + queryInfo.getCorBizProdId() + "%");
        }
        // 关联设备
        if (!StringUtils.hasText(queryInfo.getCorBizDeviceId())) {
            wrapper.like(RuleConditionEntity::getCorBizDeviceId, "%" + queryInfo.getCorBizDeviceId() + "%");
        }
        // 规则的业务编码
        if (!StringUtils.hasText(queryInfo.getBizRuleId())) {
            wrapper.like(RuleConditionEntity::getBizRuleId, "%" + queryInfo.getBizRuleId() + "%");
        }
        // 重复时间，逗号分隔
        if (!StringUtils.hasText(queryInfo.getRepeatTime())) {
            wrapper.like(RuleConditionEntity::getRepeatTime, "%" + queryInfo.getRepeatTime() + "%");
        }
        // 重复类型
        if (!StringUtils.hasText(queryInfo.getRepeatType())) {
            wrapper.like(RuleConditionEntity::getRepeatType, "%" + queryInfo.getRepeatType() + "%");
        }
        // 触发时间开始
        if (!StringUtils.hasText(queryInfo.getJudgeTimeStart())) {
            wrapper.like(RuleConditionEntity::getJudgeTimeStart, "%" + queryInfo.getJudgeTimeStart() + "%");
        }
        // 比较值
        if (!StringUtils.hasText(queryInfo.getCompareVal())) {
            wrapper.like(RuleConditionEntity::getCompareVal, "%" + queryInfo.getCompareVal() + "%");
        }
        // 比较符号，见字典COMPARATOR
        if (!StringUtils.hasText(queryInfo.getComparator())) {
            wrapper.like(RuleConditionEntity::getComparator, "%" + queryInfo.getComparator() + "%");
        }
        // 属性code
        if (!StringUtils.hasText(queryInfo.getAttrCode())) {
            wrapper.like(RuleConditionEntity::getAttrCode, "%" + queryInfo.getAttrCode() + "%");
        }
        // 触发时间结束
        if (!StringUtils.hasText(queryInfo.getJudgeTimeEnd())) {
            wrapper.like(RuleConditionEntity::getJudgeTimeEnd, "%" + queryInfo.getJudgeTimeEnd() + "%");
        }
        wrapper.orderByDesc(RuleConditionEntity::getUpdateTime);
        return wrapper;
    }
}