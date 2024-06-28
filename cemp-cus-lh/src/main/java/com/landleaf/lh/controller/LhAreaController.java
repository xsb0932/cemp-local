package com.landleaf.lh.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.lh.domain.response.*;
import com.landleaf.lh.service.LhAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绿慧区域看板
 *
 * @author xushibai
 * @since 2024/06/11
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/area")
@Tag(name = "绿慧区域看板", description = "绿慧区域看板接口")
public class LhAreaController {

    private final LhAreaService areaService;

    /**
     * 区域列表
     *
     */
    @GetMapping("/nodes/list")
    @Operation(summary = "区域列表")
    public Response<List<LhAreaInfoResponse>> nodeList() {
//        List<LhAreaInfoResponse> result = new ArrayList<>();
//        result.add(new LhAreaInfoResponse("N00001210","南京自营"));
//        result.add(new LhAreaInfoResponse("N00001211","南京自营"));
        List<LhAreaInfoResponse> result = areaService.getNodeList();
        return Response.success(result);
    }

    /**
     * 区域项目信息
     *
     */
    @GetMapping("/nodes/project")
    @Operation(summary = "区域项目信息")
    public Response<LhAreaProjectInfoResponse> projectInfo(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId) {
        LhAreaProjectInfoResponse response = new LhAreaProjectInfoResponse("7","");
        response = areaService.getAreaProjectInfo( nodeId);
        return Response.success(response);
    }

    /**
     * 概览
     *
     */
    @GetMapping("/overview")
    @Operation(summary = "概览")
    public Response<LhAreaOverviewResponse> overview(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId) {
//        LhAreaOverviewResponse response = new LhAreaOverviewResponse("9999","12","9999","12","9999","12","9999","12");
        LhAreaOverviewResponse response = areaService.getOverview( nodeId);
        return Response.success(response);
    }

    /**
     * 项目报修占比
     *
     */
    @GetMapping("/maintenance/ratio/list")
    @Operation(summary = "项目报修占比")
    public Response<CommonStaVO> maintenanceRatioList(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId , @Parameter(description = "查询时间-年月") @RequestParam("ym") String ym) {
//        CommonStaVO response = new CommonStaVO(
//                "项目报修占比",
//                Arrays.asList(new String[]{"温度","湿度","新风相关"}),
//                Arrays.asList(new String[]{"50","40","10"}),
//                null
//        );
        //ym - yearMOnth
        YearMonth staDate = YearMonth.parse(ym);
        CommonStaVO response = areaService.getMaintenanceRatioList(nodeId, YearMonth.of(staDate.getYear(),staDate.getMonthValue()));
        return Response.success(response);
    }


    /**
     * 项目报修排名
     *
     */
        @GetMapping("/maintenance/order")
    @Operation(summary = "项目报修排名")
    public Response<List<LhAreaMaintenanceOrderResponse>> maintenanceOrder(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId ,
                                                                           @Parameter(description = "查询时间-年月 yyyy-mm") @RequestParam("ym") String ym,
                                                                           @Parameter(description = "排序1.正序2.倒序") @RequestParam("order") String order) {
//        List<LhAreaMaintenanceOrderResponse> response = new ArrayList<>();
//        response.add(new LhAreaMaintenanceOrderResponse("溧水紫熙府","10000","10"));
//        response.add(new LhAreaMaintenanceOrderResponse("南京蔚蓝","10000","8"));
        YearMonth staDate = YearMonth.parse(ym);
        List<LhAreaMaintenanceOrderResponse> response = areaService.maintenanceOrder(nodeId, YearMonth.of(staDate.getYear(),staDate.getMonthValue()),order);
        return Response.success(response);
    }

    /**
     * 区域年能耗数据
     *
     */
    @GetMapping("/sta/year")
    @Operation(summary = "区域年能耗数据")
    public Response<List<CommonStaVO>> staYear(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId ,
                                                   @Parameter(description = "查询时间-年 yyyy") @RequestParam("year") String year,
                                                   @Parameter(description = "查询类型 1:电,2:水") @RequestParam("type") String type) {
//        List<CommonStaVO> response = new ArrayList<>();
//        response.add(new CommonStaVO(
//                "计划值",
//                Arrays.asList(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"}),
//                Arrays.asList(new String[]{"1000","1000","1000","1000","1000","1000","1000","1000","1000","1000","1000","1000"}),
//                null
//        ));
//        response.add(new CommonStaVO(
//                "实际值",
//                Arrays.asList(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"}),
//                Arrays.asList(new String[]{"900","900","900","900","900","900","900","900","900","900","900","900"}),
//                null
//        ));
//        response.add(new CommonStaVO(
//                "平均气温",
//                Arrays.asList(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"}),
//                Arrays.asList(new String[]{"27","27","27","27","27","27","27","27","27","27","27","27"}),
//                null
//        ));
        List<CommonStaVO> response = areaService.getStaYear(nodeId,year,type);

        return Response.success(response);
    }

    /**
     * 项目单方空调用电
     *
     */
    @GetMapping("/air/ele")
    @Operation(summary = "项目单方空调用电")
    public Response<List<CommonStaVO>> eleAirData(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId ,
                                               @Parameter(description = "查询时间-年月 yyyy-mm") @RequestParam("ym") String ym,
                                               @Parameter(description = "排序1.正序2.倒序") @RequestParam("order") String order) {

        YearMonth staDate = YearMonth.parse(ym);
        List<CommonStaVO> response = areaService.getEleAirData(nodeId,YearMonth.of(Integer.valueOf(staDate.getYear()),staDate.getMonthValue()),order);
        return Response.success(response);
    }

    /**
     * 项目单方空项目单方热水用电调用电
     *
     */
    @GetMapping("/water/ele")
    @Operation(summary = "项目单方热水用电")
    public Response<List<CommonStaVO>> eleWaterData(@Parameter(description = "区域节点id") @RequestParam("nodeId") String nodeId ,
                                               @Parameter(description = "查询时间-年月 yyyy-mm") @RequestParam("ym") String ym,
                                               @Parameter(description = "排序1.正序2.倒序") @RequestParam("order") String order) {
        YearMonth staDate = YearMonth.parse(ym);
        List<CommonStaVO> response = areaService.getEleWaterData(nodeId,YearMonth.of(Integer.valueOf(staDate.getYear()),staDate.getMonthValue()),order);
        return Response.success(response);
    }




}
