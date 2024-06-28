package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.monitor.domain.entity.DeviceMonitorTableLabelEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * 设备监控表格表头
 *
 * @author yue lin
 * @since 2023/7/20 13:19
 */
public interface DeviceMonitorTableLabelMapper extends ExtensionMapper<DeviceMonitorTableLabelEntity> {

    /**
     * 查询品类下的表头显示情况
     *
     * @param categoryBizId 品类业务ID
     * @return 结果
     */
    default List<DeviceMonitorTableLabelEntity> selectListByBizId(@NotBlank String categoryBizId) {
        return selectList(Wrappers.<DeviceMonitorTableLabelEntity>lambdaQuery()
                .eq(DeviceMonitorTableLabelEntity::getCategoryBizId, categoryBizId)
                .orderByAsc(DeviceMonitorTableLabelEntity::getSort)
        );
    }

    /**
     * 查询品类下具体的表头
     *
     * @param categoryBizId 品类业务ID
     * @param fieldKey 列code
     * @return 结果
     */
    default DeviceMonitorTableLabelEntity selectOneByBizIAadKey(@NotBlank String categoryBizId,
                                                                  @NotBlank String fieldKey) {
        return selectOne(Wrappers.<DeviceMonitorTableLabelEntity>lambdaQuery()
                .eq(DeviceMonitorTableLabelEntity::getCategoryBizId, categoryBizId)
                .eq(DeviceMonitorTableLabelEntity::getFieldKey, fieldKey)
        );
    }

}
