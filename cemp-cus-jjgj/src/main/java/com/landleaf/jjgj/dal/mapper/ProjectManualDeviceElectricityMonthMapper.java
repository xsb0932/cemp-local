package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.jjgj.domain.entity.ProjectManualDeviceElectricityMonthEntity;
import com.landleaf.jjgj.domain.request.ElectricityMonthQueryRequest;
import com.landleaf.jjgj.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.time.YearMonth;

/**
 * 手抄表-月Mapper
 *
 * @author Tycoon
 * @since 2023/8/17 9:47
 **/
public interface ProjectManualDeviceElectricityMonthMapper extends ExtensionMapper<ProjectManualDeviceElectricityMonthEntity> {

    /**
     * 分页查询抄表数据
     *
     * @param page    分页
     * @param request 参数
     * @return  结果
     */
    Page<DeviceElectricityTabulationResponse> searchPageData(@Param("page") Page<Object> page,
                                                             @Param("request") ElectricityMonthQueryRequest request);

    /**
     * 查询某天的手抄表数据
     *
     * @param bizDeviceId 设备ID
     * @param time        时间
     * @return 结果
     */
    default ProjectManualDeviceElectricityMonthEntity searchProjectTime(@NotBlank String bizDeviceId, @NotNull YearMonth time) {
        return selectOne(
                Wrappers.<ProjectManualDeviceElectricityMonthEntity>lambdaQuery()
                        .eq(ProjectManualDeviceElectricityMonthEntity::getBizProjectId, bizDeviceId)
                        .eq(ProjectManualDeviceElectricityMonthEntity::getYear, String.valueOf(time.getYear()))
                        .eq(ProjectManualDeviceElectricityMonthEntity::getMonth, String.valueOf(time.getMonthValue()))
        );
    }

}
