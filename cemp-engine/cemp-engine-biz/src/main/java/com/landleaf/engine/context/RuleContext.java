package com.landleaf.engine.context;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.engine.domain.dto.RuleQueryDTO;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import com.landleaf.engine.domain.vo.RuleTriggerVO;
import com.landleaf.engine.domain.vo.RuleVO;
import com.landleaf.engine.enums.RuleStatus;
import com.landleaf.engine.service.RuleService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class RuleContext {
    @Autowired
    private RuleService ruleServiceImpl;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CondContext condContext;

    @Autowired
    private ActionContext actionContext;

    /**
     * 从db加载规则到redis。防止redis数据清掉
     */
    @PostConstruct
    private void init() {
        TenantContext.setIgnore(true);
        RuleQueryDTO queryDTO = new RuleQueryDTO();
        queryDTO.setRuleStatus(RuleStatus.ENABLED.getCode());
        queryDTO.setPageSize(Integer.MAX_VALUE);
        PageDTO<RuleVO> page = ruleServiceImpl.page(queryDTO);
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            for (RuleVO record : page.getRecords()) {
                // 如果redis已经包含，直接返回
                if (!redisUtils.hHasKey(KeyConstance.RULE, String.valueOf(record.getId()))) {
                    loadRuleInfoById(record.getId());
                }
            }
        }
    }

    /**
     * 根据ruleId信息，将rule信息加载到redis里
     *
     * @param ruleId
     */
    public void loadRuleInfoById(Long ruleId) {
        RuleDetailVO detail = ruleServiceImpl.getDetail(ruleId);
        if (RuleStatus.ENABLED.getCode().equals(detail.getRuleStatus())) {
            if (null == detail.getActionVO() || null == detail.getTriggerVO() || CollectionUtils.isEmpty(detail.getConditionVOList())) {
                log.info("规则异常，规则信息错误:{}", JSON.toJSONString(detail));
                return;
            }
        }
        loadRuleInfo(detail);
    }

    /**
     * 将rule信息加载到redis里,调用此方法时，请确保rule的信息完整
     *
     * @param detail 信息相请
     */
    public void loadRuleInfo(RuleDetailVO detail) {
        // rule中包含设备信息，为了方便，将设备信息按照设备编号缓存到redis中
        RuleTriggerVO trigger = detail.getTriggerVO();
        String bizDeviceIds = trigger.getTargetBizDeviceIds();
        if (StringUtils.hasText(bizDeviceIds)) {
            String[] bizDevIdArr = bizDeviceIds.split(StrUtil.COMMA);
            redisUtils.hset(KeyConstance.RULE, String.valueOf(detail.getId()), JSON.toJSONString(bizDevIdArr));
            for (String bizDeviceId : bizDevIdArr) {
                if (redisUtils.hHasKey(KeyConstance.RULE_TRIGGER, bizDeviceId)) {
                    String rule = String.valueOf(redisUtils.hget(KeyConstance.RULE_TRIGGER, bizDeviceId));
                    JSONArray arr = JSONArray.parseArray(rule);
                    JSONObject obj = new JSONObject();
                    obj.put("ruleId", detail.getId());
                    obj.put("rule", JSON.toJSONString(detail));
                    arr.add(obj);
                    redisUtils.hset(KeyConstance.RULE_TRIGGER, bizDeviceId, JSON.toJSONString(arr));
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("ruleId", detail.getId());
                    obj.put("rule", JSON.toJSONString(detail));
                    JSONArray arr = new JSONArray();
                    arr.add(obj);
                    redisUtils.hset(KeyConstance.RULE_TRIGGER, bizDeviceId, JSON.toJSONString(arr));
                }
            }
        }
    }

    public void removeRuleInfo(Long ruleId) {
        String bizDevIdArrStr = String.valueOf(redisUtils.hget(KeyConstance.RULE, String.valueOf(ruleId)));
        List<String> bizDevIdArr = JSONArray.parseArray(bizDevIdArrStr, String.class);
        redisUtils.hdel(KeyConstance.RULE, String.valueOf(ruleId));
        for (String bizDeviceId : bizDevIdArr) {
            String rule = String.valueOf(redisUtils.hget(KeyConstance.RULE_TRIGGER, bizDeviceId));
            JSONArray arr = JSONArray.parseArray(rule);
            if (1 == arr.size()) {
                redisUtils.hdel(KeyConstance.RULE_TRIGGER, bizDeviceId);
            } else {
                // 多条规则， 删除指定的rule的一条
                int index = -1;
                for (int i = 0; i < arr.size(); i++) {
                    if (arr.getJSONObject(i).getLong("ruleId").equals(ruleId)) {
                        index = i;
                    }
                }
                if (index >= 0) {
                    arr.remove(index);
                }
                redisUtils.hset(KeyConstance.RULE_TRIGGER, bizDeviceId, JSON.toJSONString(arr));
            }
        }
    }

    /**
     * 执行规则
     *
     * @param bizDeviceId 设备编号
     * @param obj         设备当前数据
     */
    public void executeRule(String bizDeviceId, JSONObject obj) {
        Object val = redisUtils.hget(KeyConstance.RULE_TRIGGER, bizDeviceId);
        if (null == val) {
            return;
        }
        JSONArray arr = JSONArray.parseArray(String.valueOf(val));
        log.info("执行规则，设备编号为：{}", bizDeviceId);

        for (int i = 0; i < arr.size(); i++) {
            RuleDetailVO detail = JSON.parseObject(String.valueOf(arr.getJSONObject(i).getString("rule")), RuleDetailVO.class);
            obj.put("bizDeviceId", bizDeviceId);

            // step1：验证trigger是否ok，本次迭代不做，都是报文触发，没有时间，所以不做。

            // step2：验证条件是否都ok
            List<RuleConditionVO> condList = detail.getConditionVOList();

            boolean flag = condContext.checkCondition(condList, obj);

            log.info("执行规则，条件判断结果为：{}", flag);

            boolean hasExecuted = actionContext.hasExecuted(bizDeviceId, detail.getActionVO());
            log.info("执行规则，是否已经执行：{}", hasExecuted);
            if (flag) {
                // 如果当前没有currentStatus,则执行action
                if (!hasExecuted) {
                    // 执行action
                    actionContext.executeTrigger(bizDeviceId, detail, obj);
                }
            } else {
                // 判断是否复归
                if (hasExecuted) {
                    // 复归
                    actionContext.executeRelapse(bizDeviceId, detail, obj);
                }
            }
        }
    }
}
