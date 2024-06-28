package com.landleaf.engine.context.cond;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.enums.RepeatType;

import java.time.LocalDateTime;

public class TimeCondition extends BaseCondition {

    @Override
    public boolean check(RuleConditionVO condition, JSONObject obj) {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < 10) {
            sb.append(0);
        }
        sb.append(now.getHour()).append(":");
        if (now.getMinute() < 10) {
            sb.append(0);
        }
        sb.append(now.getMinute());
        String time = sb.toString();
        if (condition.getJudgeTimeStart().compareTo(time) > 0 || condition.getJudgeTimeEnd().compareTo(time) < 0) {
            // 不在时间范围内，return false;
            return false;
        }
        String repeatType = condition.getRepeatType();
        // 按照repeatType搞
        if (RepeatType.REPEAT_WEEK.getCode().equals(repeatType)) {
            if (!condition.getRepeatTime().contains(String.valueOf(now.getDayOfWeek().getValue()))) {
                return false;
            }
        } else if (RepeatType.REPEAT_MONTH.getCode().equals(repeatType)) {
            if (!condition.getRepeatTime().contains(String.valueOf(now.getDayOfMonth()))) {
                return false;
            }
        }
        return true;
    }
}
