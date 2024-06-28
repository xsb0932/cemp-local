package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasDayEntity;
import com.landleaf.energy.domain.request.GasMeterDetailResponse;
import com.landleaf.energy.domain.response.GasMeterPageResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 统计表-设备指标-气类-统计天的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceGasDayMapper extends ExtensionMapper<ProjectStaDeviceGasDayEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    IPage<GasMeterPageResponse> selectMeterPage(@Param("page") Page<GasMeterPageResponse> page,
                                                @Param("bizDeviceIds") List<String> bizDeviceIds,
                                                @Param("start") Timestamp start,
                                                @Param("end") Timestamp end);

    GasMeterDetailResponse detail(@Param("id") Long id);

    ProjectStaDeviceGasDayEntity getManualInsertData(@Param("bizDeviceId") String bizDeviceId);
}
