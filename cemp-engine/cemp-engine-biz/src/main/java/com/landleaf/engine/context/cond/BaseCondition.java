package com.landleaf.engine.context.cond;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.redis.RedisUtils;

public abstract class BaseCondition {

    protected static RedisUtils redisUtils = SpringUtil.getBean(RedisUtils.class);

    public abstract boolean check(RuleConditionVO condition, JSONObject obj);
}
