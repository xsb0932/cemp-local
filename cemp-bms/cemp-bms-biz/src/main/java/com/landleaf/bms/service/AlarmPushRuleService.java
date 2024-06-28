package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.bms.domain.entity.AlarmPushRuleEntity;
import com.landleaf.bms.domain.request.AlarmPushRuleAddRequest;
import com.landleaf.bms.domain.request.AlarmPushRuleConfigSaveRequest;
import com.landleaf.bms.domain.request.AlarmPushRuleEditRequest;
import com.landleaf.bms.domain.request.AlarmPushRulePageRequest;
import com.landleaf.bms.domain.response.AlarmPushRuleConfigResponse;
import com.landleaf.bms.domain.response.AlarmPushRulePageResponse;

/**
 * 告警推送规则的业务逻辑接口定义
 *
 * @author hebin
 * @since 2024-05-31
 */
public interface AlarmPushRuleService extends IService<AlarmPushRuleEntity> {

    Page<AlarmPushRulePageResponse> selectPage(AlarmPushRulePageRequest request);

    void add(AlarmPushRuleAddRequest request);

    void edit(AlarmPushRuleEditRequest request);

    void delete(Long id);

    void enable(Long id);

    void disable(Long id);

    AlarmPushRuleConfigResponse config(Long id);

    void configSave(AlarmPushRuleConfigSaveRequest request);
}