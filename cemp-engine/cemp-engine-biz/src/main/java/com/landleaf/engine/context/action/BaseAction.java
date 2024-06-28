package com.landleaf.engine.context.action;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import com.landleaf.redis.RedisUtils;

public abstract class BaseAction {

    protected static RedisUtils redisUtils = SpringUtil.getBean(RedisUtils.class);

    public abstract boolean hasExecuted(String bizDeviceId, RuleActionVO ruleActionVO);

    public abstract void executeTrigger(String bizDeviceId, RuleDetailVO detail, JSONObject obj);

    public abstract void executeRelapse(String bizDeviceId, RuleDetailVO detail, JSONObject obj);
}
