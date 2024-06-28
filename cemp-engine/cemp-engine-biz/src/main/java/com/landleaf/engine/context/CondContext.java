package com.landleaf.engine.context;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.context.cond.DeviceAttrCondition;
import com.landleaf.engine.context.cond.TimeCondition;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.enums.RuleConditionType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 条件判断的类
 */
@Service
public class CondContext {

    private static DeviceAttrCondition attrCondition = new DeviceAttrCondition();

    private static TimeCondition timeCondition = new TimeCondition();


    /**
     * 验证是否满足条件
     *
     * @param condList
     * @param obj
     */
    public boolean checkCondition(List<RuleConditionVO> condList, JSONObject obj) {
        for (RuleConditionVO condition : condList) {
            if (RuleConditionType.TIME.getCode().equals(condition.getConditionType())) {
                if (!timeCondition.check(condition, obj)) {
                    return false;
                }
            } else if (RuleConditionType.DEVICE.getCode().equals(condition.getConditionType())) {
                if (!attrCondition.check(condition, obj)) {
                    return false;
                }
            }
        }
        return true;
    }
}
