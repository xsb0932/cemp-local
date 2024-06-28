package com.landleaf.bms.dal.mapper;

import com.landleaf.bms.domain.dto.AlarmPushRulePageUserDTO;
import com.landleaf.bms.domain.entity.AlarmPushUserEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AlarmPushUserEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2024-05-31
 */
public interface AlarmPushUserMapper extends ExtensionMapper<AlarmPushUserEntity> {

    List<AlarmPushRulePageUserDTO> selectPageUserList(@Param("ruleIdList") List<Long> ruleIdList);
}