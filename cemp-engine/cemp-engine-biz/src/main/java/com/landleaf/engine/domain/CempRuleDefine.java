package com.landleaf.engine.domain;

import lombok.Data;
import org.jeasy.rules.api.Rule;

@Data
public class CempRuleDefine {
    private Long ruleId;

    private Rule rule;
}
