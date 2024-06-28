package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorCurrentDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.request.MonitorDeviceQueryRequest;
import com.landleaf.monitor.domain.response.AVueDeviceListResponse;
import com.landleaf.monitor.domain.response.AVueDevicePageResponse;
import com.landleaf.monitor.domain.response.MonitorDeviceListResponse;
import com.landleaf.monitor.domain.vo.DeviceMonitorVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 设备-监测平台的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-05
 */
public interface DeviceMonitorMapper extends BaseMapper<DeviceMonitorEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    List<DeviceStaDTO> listStaDeviceByCategory(@Param("bizCategoryIds") List<String> bizCategoryIds);

    @Select("select * from tb_device_monitor where deleted = 0 and biz_device_id = #{bizDeviceId}")
    DeviceMonitorEntity getByBizid(@Param("bizDeviceId") String bizDeviceId);

    /**
     * 根据条件分页查询设备监控列表
     *
     * @param page              分页
     * @param query             查询条件
     * @param meterBizDeviceIds
     * @return 结果集
     */
    Page<DeviceMonitorVO> pageDeviceMonitor(@Param("page") Page<DeviceMonitorVO> page,
                                            @Param("query") DeviceMonitorCurrentDTO query,
                                            @Param("meterBizDeviceIds") List<String> meterBizDeviceIds);

    Page<MonitorDeviceListResponse> pageQuery(@Param("page") Page<MonitorDeviceListResponse> page,
                                              @Param("request") MonitorDeviceQueryRequest request);

    List<ProjectStaDTO> listStaProject();

    IPage<AVueDevicePageResponse> aVueGetDevices(@Param("page") Page<AVueDevicePageResponse> page, @Param("bizProjectId") String bizProjectId, @Param("categoryName") String categoryName, @Param("name") String name, @Param("code") String code);

    List<AVueDeviceListResponse> aVueGetDeviceAll(@Param("bizProjectId") String bizProjectId);

    List<String> selectProjectMeterBizDeviceIds(@Param("bizProjectId") String bizProjectId, @Param("bizCategoryIdList") List<String> bizCategoryIdList);

    List<String> selectProjectsMeterBizDeviceIds(@Param("bizProjectIdList") List<String> bizProjectIdList, @Param("bizCategoryId") String bizCategoryId);
}
