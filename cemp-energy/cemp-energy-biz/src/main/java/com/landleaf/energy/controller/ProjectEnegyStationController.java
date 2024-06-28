package com.landleaf.energy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.dto.DeviceMonitorQueryDTO;
import com.landleaf.energy.domain.dto.ProjectKpiConfigQueryDTO;
import com.landleaf.energy.domain.dto.ProjectQueryDTO;
import com.landleaf.energy.domain.dto.ProjectStaKpiDTO;
import com.landleaf.energy.domain.entity.ProjectEntity;
import com.landleaf.energy.domain.enums.RegionTypeEnum;
import com.landleaf.energy.domain.vo.CommonStaVO;
import com.landleaf.energy.domain.vo.ProjectKpiVODetail;
import com.landleaf.energy.domain.vo.ProjectStaKpiDeviceVO;
import com.landleaf.energy.domain.vo.SelectedVO;
import com.landleaf.energy.domain.vo.rjd.*;
import com.landleaf.energy.domain.vo.station.StationCurrentStatusVO;
import com.landleaf.energy.domain.vo.station.StationRegionVO;
import com.landleaf.energy.service.DeviceMonitorService;
import com.landleaf.energy.service.ProjectService;
import com.landleaf.energy.service.ProjectStaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 站点系统图接口
 *
 * @author hebin
 * @since 2023-06-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/station")
@Tag(name = "站点系统图接口", description = "站点系统图接口")
public class ProjectEnegyStationController {

private final ProjectService projectServiceImpl;

    @Resource
    public DeviceMonitorService deviceMonitorService;

    @Resource
    public ProjectStaService projectStaService;

    @Resource
    public ProjectService projectService;


    /**
     * 查询区域
     *
     * @return 查询区域
     */
    @GetMapping("/region/list")
    @Operation(summary ="查询区域", description = "查询区域")
    public Response<List<StationRegionVO>> getRegion(@RequestParam("bizProjectId")String bizProjectId){
        List<StationRegionVO> regions= projectStaService.getRegions(bizProjectId);
        return Response.success(regions);
    }

    /**
     * 当前设备状态值
     *
     * @return 当前设备状态值
     */
    @GetMapping("/device/current/status")
    @Operation(summary ="当前设备状态值", description = "当前设备状态值")
    public Response<StationCurrentStatusVO> getDeviceCurrent(@RequestParam(value = "bizProjectId",required = true)String bizProjectId,
                                                             @RequestParam(value = "regionName",required = true)String regionName){
        StationCurrentStatusVO status= projectStaService.getDeviceCurrent(bizProjectId,regionName);
        return Response.success(status);
    }

    /**
     * 锦江房屋类型
     *
     * @return 锦江房屋类型
     */
    @GetMapping("/region/type")
    @Operation(summary ="锦江房屋类型", description = "锦江房屋类型")
    public Response<String> getRoomType(@RequestParam(value = "bizProjectId",required = true)String bizProjectId,
                                                             @RequestParam(value = "regionName",required = true)String regionName){
        String regionType = "";
        if(regionName.equals("8203") || regionName.equals("8303") ||regionName.equals("8312") || regionName.equals("8503") || regionName.equals("8505")){
            regionType = RegionTypeEnum.REGION_TYPE_1.getCode();
        }else if (regionName.equals("大堂")){
            regionType = RegionTypeEnum.REGION_TYPE_2.getCode();
        }else if(regionName.equals("二层东") || regionName.equals("二层西") ||regionName.equals("三层东") || regionName.equals("三层西") || regionName.equals("五层东") ||regionName.equals("五层西")) {
            regionType = RegionTypeEnum.REGION_TYPE_3.getCode();
        }
        return Response.success(regionType);
    }

    /**
     * 当日实时数据
     *
     * @return 当日实时数据
     */
    @GetMapping("/device/today/status")
    @Operation(summary ="当日实时数据", description = "当日实时数据")
    public Response<List<CommonStaVO>> getDeviceToday(@RequestParam(value = "bizProjectId",required = true)String bizProjectId,
                                                      @RequestParam(value = "regionName",required = true)String regionName){
        List<CommonStaVO> todayStatus= projectStaService.getDeviceToday(bizProjectId,regionName);
        return Response.success(todayStatus);
    }

    /**
     * 最近1月日统计
     *
     * @return 最近1月日统计
     */
    @GetMapping("/device/month/status")
    @Operation(summary ="最近1月日统计", description = "最近1月日统计")
    public Response<List<CommonStaVO>> getDeviceMonth(@RequestParam(value = "bizProjectId",required = true)String bizProjectId,
                                                      @RequestParam(value = "regionName",required = true)String regionName){
        List<CommonStaVO> monthStatus= projectStaService.getDeviceMonth(bizProjectId,regionName);
        return Response.success(monthStatus);
    }


}
