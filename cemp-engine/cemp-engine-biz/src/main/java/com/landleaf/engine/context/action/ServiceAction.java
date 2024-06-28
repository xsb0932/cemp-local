package com.landleaf.engine.context.action;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.domain.vo.RuleDetailVO;

public class ServiceAction extends BaseAction {
    @Override
    public boolean hasExecuted(String bizDeviceId, RuleActionVO ruleActionVO) {
        return true;
    }

    @Override
    public void executeTrigger(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {

    }

    @Override
    public void executeRelapse(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {

    }
}
