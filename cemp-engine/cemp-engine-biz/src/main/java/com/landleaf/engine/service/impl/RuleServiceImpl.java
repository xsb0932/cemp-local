package com.landleaf.engine.service.impl;

import java.util.*;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.engine.context.RuleContext;
import com.landleaf.engine.domain.dto.RuleConditionAddDTO;
import com.landleaf.engine.domain.dto.RuleDetailAddDTO;
import com.landleaf.engine.domain.vo.*;
import com.landleaf.engine.enums.RuleConditionType;
import com.landleaf.engine.enums.RuleStatus;
import com.landleaf.engine.enums.RuleType;
import com.landleaf.engine.service.RuleActionService;
import com.landleaf.engine.service.RuleConditionService;
import com.landleaf.engine.service.RuleTriggerService;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.engine.dal.mapper.RuleMapper;
import com.landleaf.engine.domain.dto.RuleAddDTO;
import com.landleaf.engine.domain.dto.RuleQueryDTO;
import com.landleaf.engine.domain.entity.RuleEntity;
import com.landleaf.engine.service.RuleService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RuleEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-04-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class RuleServiceImpl extends ServiceImpl<RuleMapper, RuleEntity> implements RuleService {

    /**
     * 数据库操作句柄
     */
    private final RuleMapper ruleMapper;

    private final BizSequenceService bizSequenceService;

    private final RuleActionService ruleActionServiceImpl;

    private final RuleConditionService ruleConditionServiceImpl;

    private final RuleTriggerService ruleTriggerServiceImpl;

    private final ProjectApi projectApi;

    private final RuleContext ruleContext;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleAddDTO save(RuleAddDTO addInfo) {
        RuleEntity entity = new RuleEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        entity.setBizRuleId(bizSequenceService.next(BizSequenceEnum.RULE));
        entity.setRuleStatus(RuleStatus.DISABLED.getCode());
        int effectNum = ruleMapper.insert(entity);
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
    public void update(RuleAddDTO updateInfo) {
        RuleEntity entity = ruleMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
//        // 仅支持状态为停用的修改
//        if (!RuleStatus.DISABLED.getCode().equals(entity.getRuleStatus())) {
//            throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getCode(), GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getMsg());
//        }
        // 启用的设备可以修改名称和描述

        if (StringUtils.hasText(updateInfo.getName())) {
            entity.setName(updateInfo.getName());
        }
        if (StringUtils.hasText(updateInfo.getRuleDesc())) {
            entity.setRuleDesc(updateInfo.getRuleDesc());
        }

        ruleMapper.updateById(entity);
        // 如果规则当前为启用状态，且告警内容中包含{规则标题}这个通配，则reload缓存
        if (RuleStatus.ENABLED.getCode().equals(entity.getRuleStatus())) {
            TenantContext.setIgnore(true);
            RuleActionVO actionVO = ruleActionServiceImpl.selectByRuleId(entity.getBizRuleId());
            if (null != actionVO && null != actionVO.getId()) {
                if (actionVO.getAlarmRelapseDesc().contains("{规则标题}") || actionVO.getAlarmTriggerDesc().contains("{规则标题}")) {
                    RuleDetailVO detail = getDetail(entity.getId());
                    ruleContext.removeRuleInfo(entity.getId());
                    ruleContext.loadRuleInfo(detail);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(Long id, Integer isDeleted) {
        RuleEntity entity = ruleMapper.selectById(id);
        if (null != entity && !RuleStatus.DISABLED.getCode().equals(entity.getRuleStatus())) {
            throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getCode(), GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getMsg());
        }
        ruleMapper.updateIsDeleted(Arrays.asList(id), isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleEntity selectById(Long id) {
        RuleEntity entity = ruleMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleEntity> list(RuleQueryDTO queryInfo) {
        return ruleMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<RuleVO> page(RuleQueryDTO queryInfo) {
        Long userId = LoginUserUtil.getLoginUserId();
        Page<RuleVO> page = ruleMapper.selectPageVO(Page.of(queryInfo.getPageNo(), queryInfo.getPageSize()), queryInfo, userId);
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            Map<String, String> statusDescMap = RuleStatus.getDescMap();
            Map<String, String> typeDescMap = RuleType.getDescMap();
            page.getRecords().forEach(i -> {
                // 设置ruleStatusDesc和ruleTypeDesc
                i.setRuleStatusDesc(statusDescMap.get(i.getRuleStatus()));
                i.setRuleTypeDesc(typeDescMap.get(i.getRuleType()));
            });
        }
        PageDTO<RuleVO> pageVO = new PageDTO<RuleVO>();
        if (null == page) {
            pageVO.setCurrent(0);
            pageVO.setTotal(0);
            pageVO.setPages(1);
            pageVO.setRecords(new ArrayList<RuleVO>());
            return pageVO;
        }
        pageVO.setCurrent(page.getCurrent());
        pageVO.setTotal(page.getTotal());
        pageVO.setPages(page.getPages());
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            Map<String, String> statusDescMap = RuleStatus.getDescMap();
            Map<String, String> typeDescMap = RuleType.getDescMap();
            page.getRecords().forEach(i -> {
                // 设置ruleStatusDesc和ruleTypeDesc
                i.setRuleStatusDesc(statusDescMap.get(i.getRuleStatus()));
                i.setRuleTypeDesc(typeDescMap.get(i.getRuleType()));
            });
            pageVO.setRecords(page.getRecords());
        }
        return pageVO;
    }

    @Override
    public void changeStatus(Long id, String status) {
        // 如果是需要启用规则，需要先判断规则的完整性
        RuleDetailVO detail = null;
        if (RuleStatus.ENABLED.getCode().equals(status)) {
            detail = getDetail(id);
            if (null == detail || null == detail.getActionVO() || null == detail.getTriggerVO() || CollectionUtils.isEmpty(detail.getConditionVOList())) {
                throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_RULE_CONTEXT.getCode(), GlobalErrorCodeConstants.ILLEGAL_RULE_CONTEXT.getMsg());
            }
        }
        RuleEntity entity = new RuleEntity();
        entity.setId(id);
        entity.setRuleStatus(status);
        ruleMapper.updateById(entity);
        // changeStatus
        if (RuleStatus.DISABLED.getCode().equals(status)) {
            // 停用直接从context中删除即可
            ruleContext.removeRuleInfo(id);
        } else {
            // 启用需要将内容加载到对应的缓存中
            detail.setRuleStatus(RuleStatus.ENABLED.getCode());
            detail.setRuleStatusDesc(RuleStatus.ENABLED.getDesc());
            ruleContext.loadRuleInfo(detail);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDetail(RuleDetailAddDTO addInfo) {
        // 首先，把能删的都删了
        RuleEntity rule = getById(addInfo.getId());
        if (!RuleStatus.DISABLED.getCode().equals(rule.getRuleStatus())) {
            throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getCode(), GlobalErrorCodeConstants.ILLEGAL_RULE_STATUS.getMsg());
        }
        TenantContext.setIgnore(true);
        String bizRuleId = rule.getBizRuleId();
        if (!CollectionUtils.isEmpty(addInfo.getConditionAddDTOList())) {
            // 当有条件节点时，需要判断，是否只有一个时间条件
            long count = addInfo.getConditionAddDTOList().stream().filter(i -> RuleConditionType.TIME.getCode().equals(i.getConditionType())).count();
            if (count > 1) {
                throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_RULE_CONDITION.getCode(), GlobalErrorCodeConstants.ILLEGAL_RULE_CONDITION.getMsg());
            }
        }

        // 根据ruleId删除对应的触发，条件，执行动作
        ruleActionServiceImpl.deleteByBizRuleId(bizRuleId);
        ruleConditionServiceImpl.deleteByBizRuleId(bizRuleId);
        ruleTriggerServiceImpl.deleteByBizRuleId(bizRuleId);

        // 删除完了，往里面加
        if (null != addInfo.getActionAddDTO()) {
            addInfo.getActionAddDTO().setId(null);
            addInfo.getActionAddDTO().setBizRuleId(bizRuleId);
            if (null == addInfo.getActionAddDTO().getServiceParameter()) {
                addInfo.getActionAddDTO().setServiceParameter(new HashMap<>());
            }
            ruleActionServiceImpl.save(addInfo.getActionAddDTO());
        }
        if (null != addInfo.getTriggerAddDTO()) {
            addInfo.getTriggerAddDTO().setId(null);
            addInfo.getTriggerAddDTO().setBizRuleId(bizRuleId);
            ruleTriggerServiceImpl.save(addInfo.getTriggerAddDTO());
        }
        if (!CollectionUtils.isEmpty(addInfo.getConditionAddDTOList())) {
            // 当有条件节点时，需要判断，是否只有一个时间条件
            for (RuleConditionAddDTO ruleConditionAddDTO : addInfo.getConditionAddDTOList()) {
                // 理论上条件不会太多，懒得做批量了，直接单插入算了
                ruleConditionAddDTO.setId(null);
                ruleConditionAddDTO.setBizRuleId(bizRuleId);
                ruleConditionServiceImpl.save(ruleConditionAddDTO);
            }
        }
        return true;
    }

    @Override
    public RuleDetailVO getDetail(Long id) {
        RuleDetailVO result = new RuleDetailVO();
        RuleEntity ruleEntity = getById(id);
        BeanUtil.copyProperties(ruleEntity, result, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        TenantContext.setIgnore(true);

        result.setRuleStatusDesc(RuleStatus.getDescMap().get(result.getRuleStatus()));
        result.setRuleTypeDesc(RuleType.getDescMap().get(result.getRuleType()));

        // 保存project信息
        Response<Map<String, String>> resp = projectApi.getProjectNames(Arrays.asList(result.getBizProjectId()));
        if (resp.isSuccess() && 1 == resp.getResult().size()) {
            result.setProjectName(resp.getResult().get(result.getBizProjectId()));
        }

        // 补充触发，条件，执行节点的数据
        RuleTriggerVO triggerVO = ruleTriggerServiceImpl.selectByRuleId(ruleEntity.getBizRuleId());
        if (null != triggerVO && null != triggerVO.getId()) {
            result.setTriggerVO(triggerVO);
        }

        List<RuleConditionVO> conditionVOList = ruleConditionServiceImpl.selectListByRuleId(ruleEntity.getBizRuleId());
        result.setConditionVOList(conditionVOList);

        RuleActionVO actionVO = ruleActionServiceImpl.selectByRuleId(ruleEntity.getBizRuleId());
        if (null != actionVO && null != actionVO.getId()) {
            result.setActionVO(actionVO);
        }
        return result;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<RuleEntity> getCondition(RuleQueryDTO queryInfo) {
        LambdaQueryWrapper<RuleEntity> wrapper = new QueryWrapper<RuleEntity>().lambda().eq(RuleEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

        // 规则编号
        if (null != queryInfo.getId()) {
            wrapper.eq(RuleEntity::getId, queryInfo.getId());
        }
        // 规则的业务编码
        if (!StringUtils.hasText(queryInfo.getBizRuleId())) {
            wrapper.like(RuleEntity::getBizRuleId, "%" + queryInfo.getBizRuleId() + "%");
        }
        // 规则名称
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(RuleEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // 规则描述
        if (!StringUtils.hasText(queryInfo.getRuleDesc())) {
            wrapper.like(RuleEntity::getRuleDesc, "%" + queryInfo.getRuleDesc() + "%");
        }
        // 规则类型，见字典RULE_TYPE
        if (!StringUtils.hasText(queryInfo.getRuleType())) {
            wrapper.like(RuleEntity::getRuleType, "%" + queryInfo.getRuleType() + "%");
        }
        // 规则状态，见字典RULE_STATUS
        if (!StringUtils.hasText(queryInfo.getRuleStatus())) {
            wrapper.like(RuleEntity::getRuleStatus, "%" + queryInfo.getRuleStatus() + "%");
        }
        // 项目id（全局唯一id）
        if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
            wrapper.like(RuleEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
        }
        wrapper.orderByDesc(RuleEntity::getUpdateTime);
        return wrapper;
    }
}