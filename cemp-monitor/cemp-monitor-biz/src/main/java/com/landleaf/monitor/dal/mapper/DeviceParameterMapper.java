package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.monitor.api.dto.MeterDeviceDTO;
import com.landleaf.monitor.domain.entity.DeviceParameterEntity;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * 设备参数明细表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-07-27
 */
public interface DeviceParameterMapper extends BaseMapper<DeviceParameterEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("select * from tb_device_parameter where deleted = 0 and biz_device_id = #{bizDeviceId} and identifier = #{code} limit 1")
    DeviceParameterEntity getParameter(@Param("bizDeviceId") String bizDeviceId, @Param("code") String code);


    /**
     * 查询具体设备的位置标识符值
     *
     * @param bizDeviceId 设备业务ID
     * @param identifier  唯一标识符
     * @return 结果
     */
    default DeviceParameterEntity searchDeviceParameter(@NotBlank String bizDeviceId, @NotBlank String identifier) {
        return selectOne(
                Wrappers.<DeviceParameterEntity>lambdaQuery()
                        .eq(DeviceParameterEntity::getBizDeviceId, bizDeviceId)
                        .eq(DeviceParameterEntity::getIdentifier, identifier)
        );
    }

    List<MeterDeviceDTO> getDeviceByProjectCategoryParameter(@Param("bizProjectIds") Collection<String> bizProjectIds, @Param("bizCategoryId") String bizCategoryId, @Param("meterRead") String meterRead, @Param("meterReadCycle") String meterReadCycle);
}
