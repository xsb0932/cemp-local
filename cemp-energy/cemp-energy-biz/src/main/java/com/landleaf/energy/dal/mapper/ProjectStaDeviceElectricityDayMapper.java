package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.energy.domain.request.ElectricityDayQueryRequest;
import com.landleaf.energy.domain.response.DeviceElectricityTabulationResponse;
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


    @Select("select \n" +
            "t1.biz_device_id,\n" +
            "t1.biz_product_id,\n" +
            "t1.biz_category_id,\n" +
            "t1.biz_project_id,\n" +
            "t2.code as project_code,\n" +
            "t1.tenant_id,\n" +
            "t3.code as tenant_code\n" +
            "\n" +
            "from tb_device_monitor t1  \n" +
            "left join tb_project t2 on t1.biz_project_id = t2.biz_project_id\n" +
            "left join tb_tenant t3 on t1.tenant_id = t3.id\n" +
            "\n" +
            "where t1.deleted = 0 and t1.biz_device_id = #{bizDeviceId} and t1.tenant_id = #{tenantId}")
    ProjectStaDeviceElectricityDayEntity getDeviceByBizid(@Param("bizDeviceId") String bizDeviceId, @Param("tenantId") Long tenantId);

    ProjectStaDeviceElectricityDayEntity getManualInsertData(@Param("bizDeviceId") String bizDeviceId);
}
