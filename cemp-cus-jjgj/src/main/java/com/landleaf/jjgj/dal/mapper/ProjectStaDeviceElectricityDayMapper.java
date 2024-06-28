package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.jjgj.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.jjgj.domain.request.ElectricityDayQueryRequest;
import com.landleaf.jjgj.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计表-设备指标-电表-统计天的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceElectricityDayMapper extends ExtensionMapper<ProjectStaDeviceElectricityDayEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_sta_device_electricity_day where deleted = 0 and biz_project_id = #{bizProjectId} and biz_device_id = #{bizDeviceId} and  sta_time >= #{begin} and sta_time  < #{end} order by sta_time")
    List<ProjectStaDeviceElectricityDayEntity> list(@Param("bizDeviceId") String bizDeviceId,
                                                    @Param("bizProjectId") String bizProjectId,
                                                    @Param("begin") String begin,
                                                    @Param("end") String end);

    /**
     * 分页查询抄表数据
     *
     * @param page    分页
     * @param request 参数
     * @return 结果
     */
    Page<DeviceElectricityTabulationResponse> searchPageData(@Param("page") Page<Object> page,
                                                             @Param("request") ElectricityDayQueryRequest request);

}
