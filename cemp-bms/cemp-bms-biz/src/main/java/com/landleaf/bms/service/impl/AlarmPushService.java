package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.landleaf.bms.api.dto.AlarmPushRequest;
import com.landleaf.bms.dal.mapper.AlarmPushConditionMapper;
import com.landleaf.bms.dal.mapper.AlarmPushRuleMapper;
import com.landleaf.bms.dal.mapper.AlarmPushStatusMapper;
import com.landleaf.bms.dal.mapper.AlarmPushUserMapper;
import com.landleaf.bms.dal.redis.AlarmPushRedisRepository;
import com.landleaf.bms.domain.dto.AlarmPushRuleRedisDTO;
import com.landleaf.bms.domain.entity.AlarmPushConditionEntity;
import com.landleaf.bms.domain.entity.AlarmPushRuleEntity;
import com.landleaf.bms.domain.entity.AlarmPushStatusEntity;
import com.landleaf.bms.domain.entity.AlarmPushUserEntity;
import com.landleaf.bms.domain.enums.RuleStatusEnum;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.mail.domain.param.AlarmPushMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserEmailDTO;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushService implements ApplicationRunner {
    private final AlarmPushRedisRepository alarmPushRedisRepository;
    private final AlarmPushRuleMapper alarmPushRuleMapper;
    private final AlarmPushConditionMapper alarmPushConditionMapper;
    private final AlarmPushStatusMapper alarmPushStatusMapper;
    private final AlarmPushUserMapper alarmPushUserMapper;
    private final MailService mailService;
    private final UserRpcApi userRpcApi;
    private LoadingCache<Long, List<AlarmPushRuleRedisDTO>> cache;
    private ThreadPoolTaskExecutor executor;

    private boolean conditionCheck(List<String> conditionList, String value) {
        return CollUtil.isNotEmpty(conditionList) && !conditionList.contains(value);
    }

    private void pushEmail(AlarmPushRequest request, List<Long> userIdList) {
        List<UserEmailDTO> userEmails = userRpcApi.getUsersEmail(userIdList).getCheckedData();
        for (UserEmailDTO userEmail : userEmails) {
            if (StrUtil.isBlank(userEmail.getEmail())) {
                log.info("用户邮箱为空 {}", userEmail);
                continue;
            }
            executor.execute(() -> {
                try {
                    mailService.sendMailAsync(AlarmPushMail.alarmToMail(
                            userEmail.getEmail(),
                            request.getProjectName(),
                            request.getObjName(),
                            request.getEventTime(),
                            request.getAlarmDesc()
                    ));
                    ThreadUtil.sleep(200);
                } catch (Exception e) {
                    log.error("告警异步邮件推送异常", e);
                }
            });

        }
    }

    private List<AlarmPushRuleRedisDTO> getRules(Long tenantId) {
        try {
            return cache.get(tenantId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("businessExecutor")
    public void alarmPush(AlarmPushRequest request) {
        log.info("alarmpush:{}", request);
        TenantContext.setIgnore(true);
        List<AlarmPushRuleRedisDTO> rules = getRules(request.getTenantId());
        if (CollUtil.isEmpty(rules)) {
            log.info("alarmpush:{}:规则为空", request);
            return;
        }
        for (AlarmPushRuleRedisDTO rule : rules) {
            List<String> bizProjectIdList = rule.getBizProjectIdList();
            if (!StrUtil.equals(bizProjectIdList.get(0), "0") && !bizProjectIdList.contains(request.getBizProjectId())) {
                log.info("alarmpush:{}:项目条件不匹配", request);
                continue;
            }
            if (conditionCheck(rule.getAlarmTypeList(), request.getAlarmType())) {
                log.info("alarmpush:{}:告警类型不匹配", request);
                continue;
            }
            if (conditionCheck(rule.getAlarmLevelList(), request.getAlarmLevel())) {
                log.info("alarmpush:{}:告警等级不匹配", request);
                continue;
            }
            if (conditionCheck(rule.getAlarmStatusList(), request.getAlarmStatus())) {
                log.info("alarmpush:{}:告警状态不匹配", request);
                continue;
            }
            if (CollUtil.isNotEmpty(rule.getEmailUserIdList())) {
                log.info("alarmpush:{}:告警推送 {}", request, rule.getEmailUserIdList());
                pushEmail(request, rule.getEmailUserIdList());
            }
            //TODO 以后会有短信和钉钉机器人的推送方式
        }
    }

    public void refresh(Long tenantId) {
        TenantContext.setIgnore(true);
        List<AlarmPushRuleEntity> rules = alarmPushRuleMapper.selectList(new LambdaQueryWrapper<AlarmPushRuleEntity>()
                .eq(tenantId != null, TenantBaseEntity::getTenantId, tenantId)
                .eq(AlarmPushRuleEntity::getRuleStatus, RuleStatusEnum.ENABLE.getCode()));
        HashMap<Long, List<AlarmPushRuleRedisDTO>> tenantRules = new HashMap<>();
        List<Long> ruleIds = rules.stream()
                .map(o -> {
                    if (!tenantRules.containsKey(o.getTenantId())) {
                        tenantRules.put(o.getTenantId(), new ArrayList<>());
                    }
                    return o.getId();
                })
                .toList();

        alarmPushRedisRepository.clear(tenantId);
        if (ruleIds.isEmpty()) {
            return;
        }

        Map<Long, AlarmPushConditionEntity> conditionMap = alarmPushConditionMapper.selectList(new LambdaQueryWrapper<AlarmPushConditionEntity>()
                        .in(AlarmPushConditionEntity::getRuleId, ruleIds))
                .stream()
                .collect(Collectors.toMap(AlarmPushConditionEntity::getRuleId, o -> o, (o1, o2) -> o1));
        Map<Long, AlarmPushStatusEntity> statusMap = alarmPushStatusMapper.selectList(new LambdaQueryWrapper<AlarmPushStatusEntity>()
                        .in(AlarmPushStatusEntity::getRuleId, ruleIds))
                .stream()
                .collect(Collectors.toMap(AlarmPushStatusEntity::getRuleId, o -> o, (o1, o2) -> o1));
        Map<Long, Map<Integer, List<AlarmPushUserEntity>>> userMap = alarmPushUserMapper.selectList(new LambdaQueryWrapper<AlarmPushUserEntity>()
                        .in(AlarmPushUserEntity::getRuleId, ruleIds))
                .stream()
                .collect(Collectors.groupingBy(AlarmPushUserEntity::getRuleId, Collectors.groupingBy(AlarmPushUserEntity::getPushType)));

        for (AlarmPushRuleEntity rule : rules) {
            Long id = rule.getId();
            AlarmPushRuleRedisDTO redisRule = new AlarmPushRuleRedisDTO().setId(id);
            AlarmPushConditionEntity condition = conditionMap.get(id);
            redisRule.setBizProjectIdList(condition.getBizProjectIdList());
            if (RuleStatusEnum.ENABLE.getCode().equals(condition.getAlarmTypeFlag())) {
                redisRule.setAlarmTypeList(condition.getAlarmTypeList());
            }
            if (RuleStatusEnum.ENABLE.getCode().equals(condition.getAlarmLevelFlag())) {
                redisRule.setAlarmLevelList(condition.getAlarmLevelList());
            }
            if (RuleStatusEnum.ENABLE.getCode().equals(condition.getAlarmStatusFlag())) {
                redisRule.setAlarmStatusList(condition.getAlarmStatusList());
            }
            AlarmPushStatusEntity status = statusMap.get(id);
            if (RuleStatusEnum.ENABLE.getCode().equals(status.getEmailStatus())) {
                redisRule.setEmailUserIdList(userMap.get(id).get(0).stream().map(AlarmPushUserEntity::getUserId).toList());
            }
            if (RuleStatusEnum.ENABLE.getCode().equals(status.getMessageStatus())) {
                redisRule.setMessageUserIdList(userMap.get(id).get(1).stream().map(AlarmPushUserEntity::getUserId).toList());
            }
            if (RuleStatusEnum.ENABLE.getCode().equals(status.getDingStatus())) {
                redisRule.setDingUrlList(userMap.get(id).get(1).stream().map(AlarmPushUserEntity::getDingUrl).toList());
            }
            tenantRules.get(rule.getTenantId()).add(redisRule);
        }

        for (Map.Entry<Long, List<AlarmPushRuleRedisDTO>> entry : tenantRules.entrySet()) {
            alarmPushRedisRepository.save(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        refresh(null);
    }

    {
        cache = CacheBuilder.newBuilder()
                // 设置缓存最大容量
                .maximumSize(500)
                // 数据写入缓存后1分钟后过期
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public List<AlarmPushRuleRedisDTO> load(Long tenantId) throws Exception {
                        return alarmPushRedisRepository.getRules(tenantId);
                    }
                });

        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(2000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("AlarmPushExecutor-Thread");
        // 丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
    }
}
