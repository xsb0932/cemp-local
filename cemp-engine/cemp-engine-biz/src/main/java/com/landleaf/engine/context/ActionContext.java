package com.landleaf.engine.context;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.context.action.AlarmAction;
import com.landleaf.engine.context.action.ServiceAction;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import com.landleaf.engine.enums.RuleActionType;
import org.springframework.stereotype.Component;

@Component
public class ActionContext {

    private static AlarmAction alarmAction = new AlarmAction();

    private static ServiceAction serviceAction = new ServiceAction();

    public boolean hasExecuted(String bizDeviceId, RuleActionVO ruleActionVO) {
        if (RuleActionType.ALARM.getCode().equals(ruleActionVO.getActionType())) {
            return alarmAction.hasExecuted(bizDeviceId, ruleActionVO);
        } else if (RuleActionType.COMMAND.getCode().equals(ruleActionVO.getActionType())) {
            return serviceAction.hasExecuted(bizDeviceId, ruleActionVO);
        }
        return false;
    }

    public void executeTrigger(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {
        if (RuleActionType.ALARM.getCode().equals(detail.getActionVO().getActionType())) {
            alarmAction.executeTrigger(bizDeviceId, detail, obj);
        } else if (RuleActionType.COMMAND.getCode().equals(detail.getActionVO().getActionType())) {
            serviceAction.executeTrigger(bizDeviceId, detail, obj);
        }
    }

    public void executeRelapse(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {
        if (RuleActionType.ALARM.getCode().equals(detail.getActionVO().getActionType())) {
            alarmAction.executeRelapse(bizDeviceId, detail, obj);
        } else if (RuleActionType.COMMAND.getCode().equals(detail.getActionVO().getActionType())) {
            serviceAction.executeRelapse(bizDeviceId, detail, obj);
        }
    }
}
