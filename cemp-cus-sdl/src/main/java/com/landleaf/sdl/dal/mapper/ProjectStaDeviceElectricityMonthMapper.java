package com.landleaf.sdl.dal.mapper;

import com.landleaf.pgsql.extension.ExtensionMapper;
import com.landleaf.sdl.domain.entity.ProjectStaDeviceElectricityMonthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计表-设备指标-电表-统计月的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceElectricityMonthMapper extends ExtensionMapper<ProjectStaDeviceElectricityMonthEntity> {


    @Select("SELECT sum(t1.energymeter_epimport_total) FROM tb_project_sta_device_electricity_day t1 \n" +
            "where t1.deleted = 0 and t1.biz_device_id = #{bizDeviceId} and  t1.year = #{year} and t1.month =#{month}\n" +
            "union\n" +
            "SELECT sum(t1.energymeter_epimport_total) FROM tb_project_sta_device_electricity_hour t1 \n" +
            "where t1.deleted = 0 and t1.biz_device_id = #{bizDeviceId} and  t1.year = #{year} and  t1.month =#{month} and t1.day = #{day}")
    List<BigDecimal> getPTotalMonth(@Param("bizDeviceId") String bizDeviceId,
                                    @Param("year") String year,
                                    @Param("month") String month,
                                    @Param("day") String day);

    @Select("SELECT sum(t1.energymeter_epimport_total) FROM tb_project_sta_device_electricity_month t1 \n" +
            "where t1.deleted = 0 and t1.biz_device_id = #{bizDeviceId} and  t1.year = #{year}")
    BigDecimal getPTotalYear(@Param("bizDeviceId") String bizDeviceId,
                             @Param("year") String year);
}
