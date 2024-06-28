package com.landleaf.engine.context;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.engine.domain.CempRuleDefine;
import com.landleaf.engine.domain.RuleCondition;
import com.landleaf.engine.domain.RuleDescription;
import com.landleaf.engine.domain.RulePerformance;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供rule的context,没啥卵用，我准备废了它。
 */
@Component
public class RuleContextBak {

    private ConcurrentHashMap<String, List<CempRuleDefine>> ruleMap = new ConcurrentHashMap<>();

    /**
     * 自动加载数据库配置
     */
    @PostConstruct
    private void init() {
        // load exists rule info from db 2 redis。

    }

    /**
     * 重新加载rule
     *
     * @param description
     * @param condList
     * @param performance
     */
    public synchronized void reloadRule(RuleDescription description, List<RuleCondition> condList, RulePerformance performance) {
        // update rule
        if (StringUtils.hasText(description.getOrgBizDeviceIds())) {
            // 删除对应的规则
            String[] bizDeviceIds = description.getOrgBizDeviceIds().split(StrUtil.COMMA);
            for (String bizDeviceId : bizDeviceIds) {
                rmRule(description.getRuleId(), bizDeviceId);
            }
        }
        if (StringUtils.hasText(description.getBizDeviceIds())) {
            // 添加对应的规则
            String[] bizDeviceIds = description.getBizDeviceIds().split(StrUtil.COMMA);
            Rule rule = buildRule(description, condList, performance);
            for (String bizDeviceId : bizDeviceIds) {
                addRule(bizDeviceId, description.getRuleId(), rule);
            }
        }
    }

    /**
     * 构建规则
     *
     * @return
     */
    private Rule buildRule(RuleDescription description, List<RuleCondition> condList, RulePerformance performance) {
//        Rule ruleBuilder = new MVELR();
//        ruleBuilder.name(description.getRuleName());
//        ruleBuilder.description(description.getRuleDesc());
//        ruleBuilder.when(buildCondition(condList));
//        return ruleBuilder.build();
        return null;
    }

    private Facts buildCondition(List<RuleCondition> condList) {
        return null;
    }

    private void addRule(String bizDeviceId, Long ruleId, Rule rule) {
        CempRuleDefine cempRuleDefine = new CempRuleDefine();
        cempRuleDefine.setRule(rule);
        cempRuleDefine.setRuleId(ruleId);
        if (ruleMap.contains(bizDeviceId)) {
            ruleMap.get(bizDeviceId).add(cempRuleDefine);
        } else {
            List<CempRuleDefine> ruleList = new ArrayList<>();
            ruleList.add(cempRuleDefine);
            ruleMap.put(bizDeviceId, ruleList);
        }
    }

    /**
     * 从map中，删除rule缓存
     *
     * @param ruleId
     * @param bizDeviceId
     */
    private void rmRule(Long ruleId, String bizDeviceId) {
        List<CempRuleDefine> list = ruleMap.get(bizDeviceId);
        List<CempRuleDefine> rmList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(list)) {
            list.forEach(i -> {
                if (i.getRuleId().equals(ruleId)) {
                    // 若为原先的规则， 则删除
                    rmList.add(i);
                }
            });
            if (!CollectionUtil.isEmpty(rmList)) {
                list.removeAll(rmList);
                ruleMap.put(bizDeviceId, list);
            }
        }
    }

    /**
     * 执行规则
     */
    public void executeRule() {
        // TODO execute rule
    }
}
