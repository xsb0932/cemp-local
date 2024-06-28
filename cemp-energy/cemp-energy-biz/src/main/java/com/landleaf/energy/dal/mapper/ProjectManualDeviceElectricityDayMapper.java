package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.energy.domain.entity.ProjectManualDeviceElectricityDayEntity;
import com.landleaf.energy.domain.request.ElectricityDayQueryRequest;
import com.landleaf.energy.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 手抄表-日Mapper
 *
 * @author Tycoon
 * @since 2023/8/17 9:46
 **/
public interface ProjectManualDeviceElectricityDayMapper extends ExtensionMapper<ProjectManualDeviceElectricityDayEntity> {

    /**
     * 分页查询抄表数据
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceElectricityTabulationResponse> searchPageData(@Param("page") Page<Object> page,
                                                             @Param("request") ElectricityDayQueryRequest request);

    /**
     * 查询某天的手抄表数据
     *
     * @param bizDeviceId 设备ID
     * @param time        时间
     * @return 结果
     */
    default ProjectManualDeviceElectricityDayEntity searchProjectTime(@NotBlank String bizDeviceId, @NotNull LocalDate time) {
        return selectOne(
                Wrappers.<ProjectManualDeviceElectricityDayEntity>lambdaQuery()
                        .eq(ProjectManualDeviceElectricityDayEntity::getBizDeviceId, bizDeviceId)
                        .eq(ProjectManualDeviceElectricityDayEntity::getYear, String.valueOf(time.getYear()))
                        .eq(ProjectManualDeviceElectricityDayEntity::getMonth, String.valueOf(time.getMonthValue()))
                        .eq(ProjectManualDeviceElectricityDayEntity::getDay, String.valueOf(time.getDayOfMonth()))
        );
    }

}
