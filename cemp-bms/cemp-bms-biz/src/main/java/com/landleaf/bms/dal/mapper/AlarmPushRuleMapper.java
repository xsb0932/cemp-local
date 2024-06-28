package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.AlarmPushRuleEntity;
import com.landleaf.bms.domain.response.AlarmPushRulePageResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 告警推送规则的数据库操作句柄
 *
 * @author hebin
 * @since 2024-05-31
 */
public interface AlarmPushRuleMapper extends ExtensionMapper<AlarmPushRuleEntity> {

    Page<AlarmPushRulePageResponse> pageQuery(@Param("page") Page<AlarmPushRulePageResponse> page,
                                              @Param("ruleName") String ruleName,
                                              @Param("ruleStatus") String ruleStatus);
}