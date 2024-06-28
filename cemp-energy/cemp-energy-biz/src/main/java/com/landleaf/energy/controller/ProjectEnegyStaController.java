package com.landleaf.energy.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.domain.dto.ProjectStaKpiDTO;
import com.landleaf.energy.domain.entity.DeviceCategoryKpiConfigEntity;
import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import com.landleaf.energy.domain.vo.*;
import com.landleaf.energy.domain.vo.rjd.*;
import com.landleaf.energy.service.*;
import com.landleaf.energy.util.DateUtils;
import com.landleaf.energy.util.KpiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 能源统计接口
 *
 * @author hebin
 * @since 2023-06-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sta")
@Tag(name = "能源统计接口", description = "能源统计接口")
public class ProjectEnegyStaController {

private final ProjectService projectServiceImpl;


    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private static final String EXCEL_PROJECT_TEMPLATE_FILE_NAME = "项目报表";
    private static final String EXCEL_DEVICE_TEMPLATE_FILE_NAME = "设备报表";

    @Resource
    public DeviceMonitorService deviceMonitorService;

    @Resource
    public ProjectStaService projectStaService;

    @Resource
    public ProjectService projectService;

    @Resource
    public ProjectKpiConfigService projectKpiConfigService;

    @Resource
    public DeviceCategoryKpiConfigService deviceCategoryKpiConfigService;


    /**
     * 项目统计-项目统计数据
     *
     * @return 项目统计-项目统计数据
     */
    @GetMapping("/getProjectKpi")
    @Operation(summary ="项目统计-项目指标和维度", description = "项目统计-项目指标和维度")
    public Response<List<ProjectKpiSelectVO>> getProjectKpi(@RequestParam("bizProjectId")String bizProjectId,
                                                          @RequestParam("timePeriod")String timePeriod){
        List<ProjectKpiSelectVO> projects= projectStaService.getProjectKpi(bizProjectId,timePeriod);
        return Response.success(projects);
    }

//    /**
//     * 项目统计-查询项目
//     *
//     * @return 项目统计-查询项目
//     */
//    @GetMapping("/getProjects")
//    @Operation(summary ="项目统计-查询项目", description = "项目统计-查询项目")
//    public Response<List<ProjectEntity>> getProjects(@RequestBody ProjectQueryDTO queryInfo){
//        List<ProjectEntity> projects= projectServiceImpl.list(queryInfo);
//        return Response.success(projects);
//    }

    /**
     * 项目统计-项目树
     *
     * @return 项目统计-项目树
     */
    @GetMapping("/getProjectsTree")
    @Operation(summary ="项目统计-项目树", description = "项目统计-项目树")
    public Response<List<CommonProjectTreeVO>> getProjectsTree(){
        List<CommonProjectTreeVO> projectTreeVOS  =  projectStaService.getProjectTree();
        return Response.success(projectTreeVOS);
    }

    /**
     * 设备看板-设备类型
     *
     * @return 返回设备看板-设备类型
     */
    @GetMapping("/getDeviceCategory")
    @Operation(summary ="设备看板-设备类型", description = "设备看板-设备类型")
    public Response<List<SelectedVO>> getDeviceCategory(){

        List<DeviceCategoryKpiConfigEntity> configs = deviceCategoryKpiConfigService.listCategory();
        List<SelectedVO> SelectedVOs= configs.stream().map(config -> new SelectedVO(config.getCategoryName(),config.getBizCategoryId())).collect(Collectors.toList());
        return Response.success(SelectedVOs);
    }

    /**
     * 设备看板-选择设备
     *
     * @return 返回设备看板-设备类型
     */
    @GetMapping("/getDeviceByCategory")
    @Operation(summary ="设备看板-选择设备", description = "设设备看板-选择设备")
    public Response<List<SelectedVO>> getDeviceByCategory(@RequestParam("bizCategoryId")String bizCategoryId){
        List<SelectedVO> selectedVOS= deviceMonitorService.getDeviceByCategory(bizCategoryId);
        return Response.success(selectedVOS);
    }

    /**
     * 统计周期列表
     *
     * @param
     * @return 统计周期列表
     */
    @GetMapping("/getStaTimePeriod")
    @Operation(summary ="统计周期列表", description = "统计周期列表")
    public Response<List<SelectedVO>> getStaTimePeriod(){
        //todo
        List<SelectedVO> selectedVOS= new ArrayList<>();
        selectedVOS.add(new SelectedVO("小时","1"));
        selectedVOS.add(new SelectedVO("日","2"));
        selectedVOS.add(new SelectedVO("月","3"));
        selectedVOS.add(new SelectedVO("年","4"));
        return Response.success(selectedVOS);
    }

    List<EnergySelectedVO> getKpi(String bizCategoryId){
        TenantContext.setIgnore(true);
        List<EnergySelectedVO> kpis = deviceCategoryKpiConfigService.getKpi(bizCategoryId);
        return kpis;
    }

    @GetMapping("/device/kpi")
    @Operation(summary ="设备报表-维度和指标", description = "设备报表-维度和指标")
    public Response<List<EnergySelectedVO>> getDeviceKpi(@RequestParam(value = "bizCategoryId",required = false)String bizCategoryId){
        //todo 获取设备指标，根据文档 写死
        return Response.success(this.getKpi(bizCategoryId));
    }

    /**
     * 分页查询设备指标
     *
     * @return 设备列表
     */
    @PostMapping("/device/list/data")
    @Operation(summary = "设备统计-分页查询设备指标", description = "设备统计-分页查询设备指标")
    public Response<IPage<ProjectStaKpiDeviceVO>> getDeviceData(@RequestBody ProjectStaKpiDTO qry) {
        if(StringUtils.isBlank(qry.getBizCategoryId()))
            return Response.error("param_error","输入设备类型.");

        if(StringUtils.isBlank(qry.getStaTimePeriod()))
            return Response.error("param_error","输入统计周期.");

        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getDeviceData(qry);


        return Response.success(page);
    }
//    /**
//     * 设备指标-导出
//     *
//     * @return 设备列表
//     */
//    @PostMapping("/device/list/data/export")
//    @Operation(summary = "设备统计-分页查询设备-导出", description = "设备统计-分页查询设备-导出")
//    public void getDeviceDataExport(HttpServletResponse resp, @RequestBody ProjectStaKpiDTO qry) throws IOException {
//        qry.setPageNo(1);
//        qry.setPageSize(65536);
//        ExcelWriter writer = ExcelUtil.getWriter(true);
//        //所有kpi名称
//        List<EnergySelectedVO> allKpi = this.getKpi(null);
//        Map<String,String> kpiNameMap  = allKpi.stream().collect(Collectors.toMap(EnergySelectedVO::getValue,EnergySelectedVO::getLabel));
//
//        //设置头信息
//        writer.addHeaderAlias("projectName", "项目名称");
//        writer.addHeaderAlias("deviceName", "设备编码");
//        writer.addHeaderAlias("deviceCode", "设备名称");
//        writer.addHeaderAlias("staTime", "统计时间");
//        qry.getKpiCodes().forEach(kpi -> writer.addHeaderAlias(KpiUtils.kpiToProperty(kpi), kpiNameMap.get(kpi)));  //指标列
//
//        List<String> header = Lists.newArrayList();
//        header.add("项目名称");
//        header.add("设备编码");
//        header.add("设备名称");
//        header.add("统计时间");
//        qry.getKpiCodes().forEach(kpi -> header.add(kpiNameMap.get(kpi)));  //指标列
//        //writer.writeRow(header);
//        //写数据
//        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getDeviceData(qry);
//        List<ProjectStaKpiDeviceVO> data =  page.getRecords();
//        List<Map<String,Object>> rowDatas = new ArrayList<>();
//        for(ProjectStaKpiDeviceVO deviceVO : data){
//            Map<String,Object> rowData = new HashMap<>();
//            for(Map.Entry<String,String> entry:writer.getHeaderAlias().entrySet() ){
//                String property = entry.getKey();
//                String methodNanme = "get".concat(StrUtil.upperFirst(property));
//                Object value = KpiUtils.getValue(deviceVO,ProjectStaKpiDeviceVO.class,methodNanme);
//                rowData.put(property,value);
//            }
//            rowDatas.add(rowData);
//        }
//
//        writer.write(rowDatas, true);
//        //导出
//        String fileName = URLUtil.encode("设备数据");
//        //String fileName = DateUtil.today().concat("_").concat(UUID.fastUUID().toString()).concat(".xlsx");
//        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
//        resp.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
//        resp.setHeader("Content-Disposition", "attachment;filename=" + fileName+".xlsx");
//        ServletOutputStream out = resp.getOutputStream();
//        writer.flush(out, true);
//    }
    /**
     * 设备指标-导出
     *
     * @return 设备列表
     */
    @PostMapping("/device/list/data/export")
    @Operation(summary = "设备统计-分页查询设备-导出", description = "设备统计-分页查询设备-导出")
    public void getDeviceDataExport(HttpServletResponse resp, @RequestBody ProjectStaKpiDTO qry) throws IOException {
        qry.setPageNo(1);
        qry.setPageSize(65536);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //所有kpi名称
        List<EnergySelectedVO> allKpi = this.getKpi(null);
        Map<String,EnergySelectedVO> kpiNameMap  = allKpi.stream().collect(Collectors.toMap(EnergySelectedVO::getValue, t -> t));

        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_DEVICE_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + DateUtils.getCurrent(LocalDateTime.now())+".xlsx");

            //设置头信息
            writer.addHeaderAlias("projectName", "项目名称");
            writer.addHeaderAlias("deviceName", "设备名称");
            writer.addHeaderAlias("deviceCode", "设备编码");
            writer.addHeaderAlias("staTime", "统计时间");
            qry.getKpiCodes().forEach(kpi -> writer.addHeaderAlias(KpiUtils.kpiToProperty(kpi,ProjectStaKpiDeviceVO.class), String.format("%s(%s)",kpiNameMap.get(kpi).getLabel(),kpiNameMap.get(kpi).getUnit())));  //指标列

            List<String> header = Lists.newArrayList();
            header.add("项目名称");
            header.add("设备编码");
            header.add("设备名称");
            header.add("统计时间");
            qry.getKpiCodes().forEach(kpi -> header.add(kpiNameMap.get(kpi).getLabel()));  //指标列
            //writer.writeRow(header);
            //写数据
            IPage<ProjectStaKpiDeviceVO> page = projectStaService.getDeviceData(qry);
            List<ProjectStaKpiDeviceVO> data =  page.getRecords();
            List<Map<String,Object>> rowDatas = new ArrayList<>();
            for(ProjectStaKpiDeviceVO deviceVO : data){
                Map<String,Object> rowData = new HashMap<>();
                for(Map.Entry<String,String> entry:writer.getHeaderAlias().entrySet() ){
                    String property = entry.getKey();
                    String methodNanme = "get".concat(StrUtil.upperFirst(property));
                    Object value = KpiUtils.getValue(deviceVO,ProjectStaKpiDeviceVO.class,methodNanme);
                    rowData.put(property,value);
                }
                rowDatas.add(rowData);
            }
            writer.write(rowDatas);
            writer.flush(os);
        }catch (IOException e) {
            e.printStackTrace();
        }



    }


    /**
     * 设备指标-导出
     *
     * @return 设备列表
     */
    @GetMapping("/device/list/data/export2")
    @Operation(summary = "设备统计-分页查询设备-导出", description = "设备统计-分页查询设备-导出")
    public void testExport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TenantContext.setIgnore(true);
        ProjectStaKpiDTO qry = new ProjectStaKpiDTO();
        qry.setBizDeviceIds(Arrays.asList(new String[]{"D000000000052"}));
        qry.setBizCategoryId("PC0004");
        qry.setStaTimePeriod("1");
        qry.setKpiCodes(Arrays.asList(new String[]{"airConditionerController.onlineTime.total"}));
        qry.setBegin("2023-06-29 00:00:00");
        qry.setEnd("2023-06-29 23:00:00");
        qry.setPageNo(1);
        qry.setPageSize(100);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //所有kpi名称
        List<EnergySelectedVO> allKpi = this.getKpi(null);
        Map<String,String> kpiNameMap  = allKpi.stream().collect(Collectors.toMap(EnergySelectedVO::getValue,EnergySelectedVO::getLabel));

        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_PROJECT_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

            //设置头信息
            writer.addHeaderAlias("projectName", "项目名称");
            writer.addHeaderAlias("deviceName", "设备编码");
            writer.addHeaderAlias("deviceCode", "设备名称");
            writer.addHeaderAlias("staTime", "统计时间");
            qry.getKpiCodes().forEach(kpi -> writer.addHeaderAlias(KpiUtils.kpiToProperty(kpi), kpiNameMap.get(kpi)));  //指标列

            List<String> header = Lists.newArrayList();
            header.add("项目名称");
            header.add("设备编码");
            header.add("设备名称");
            header.add("统计时间");
            qry.getKpiCodes().forEach(kpi -> header.add(kpiNameMap.get(kpi)));  //指标列
            //writer.writeRow(header);
            //写数据
            IPage<ProjectStaKpiDeviceVO> page = projectStaService.getDeviceData(qry);
            List<ProjectStaKpiDeviceVO> data =  page.getRecords();
            List<Map<String,Object>> rowDatas = new ArrayList<>();
            for(ProjectStaKpiDeviceVO deviceVO : data){
                Map<String,Object> rowData = new HashMap<>();
                for(Map.Entry<String,String> entry:writer.getHeaderAlias().entrySet() ){
                    String property = entry.getKey();
                    String methodNanme = "get".concat(StrUtil.upperFirst(property));
                    Object value = KpiUtils.getValue(deviceVO,ProjectStaKpiDeviceVO.class,methodNanme);
                    rowData.put(property,value);
                }
                rowDatas.add(rowData);
            }
            writer.write(rowDatas);
            writer.flush(os);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 分页查询设备指标-电
//     *
//     * @return 设备列表
//     */
//    @PostMapping("/device/list/electricity")
//    @Operation(summary = "设备统计-分页查询设备指标-电", description = "设备统计-分页查询设备指标-电")
//    public Response<IPage<ProjectStaKpiDeviceVO>> getElectricityData(@RequestBody ProjectStaKpiDTO qry) {
//        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getElectricityData(qry);
//        return Response.success(page);
//    }
//
//    /**
//     * 分页查询设备指标-空调
//     *
//     * @return 设备列表
//     */
//    @PostMapping("/device/list/air")
//    @Operation(summary = "设备统计-分页查询设备指标-空调", description = "设备统计-分页查询设备指标-空调")
//    public Response<IPage<ProjectStaKpiDeviceVO>> getAirData(@RequestBody ProjectStaKpiDTO qry) {
//        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getAirData(qry);
//        return Response.success(page);
//    }
//
//    /**
//     * 分页查询设备指标-气表
//     *
//     * @return 设备列表
//     */
//    @PostMapping("/device/list/gas")
//    @Operation(summary = "设备统计-分页查询设备指标-气表", description = "设备统计-分页查询设备指标-气表")
//    public Response<IPage<ProjectStaKpiDeviceVO>> getGasData(@RequestBody ProjectStaKpiDTO qry) {
//        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getGasData(qry);
//        return Response.success(page);
//    }
//
//    /**
//     * 分页查询设备指标-水表
//     *
//     * @return 设备列表
//     */
//    @PostMapping("/device/list/water")
//    @Operation(summary = "设备统计-分页查询设备指标-水表", description = "设备统计-分页查询设备指标-水表")
//    public Response<IPage<ProjectStaKpiDeviceVO>> getWaterData(@RequestBody ProjectStaKpiDTO qry) {
//        IPage<ProjectStaKpiDeviceVO> page = projectStaService.getWaterData(qry);
//        return Response.success(page);
//    }

    /**
     * 分页查询项目指标-头信息
     *
     * @return
     */
    @PostMapping("/project/list/title")
    @Operation(summary = "分页查询项目指标-头信息", description = "")
    public Response<List<String>> getProjectStaTitle(@RequestBody ProjectStaKpiDTO qry) {
        List<String> title = new ArrayList<>();
        return Response.success(title);
    }


    /**
     * 分页查询项目指标-数据
     *
     * @return
     */
    @PostMapping("/project/list/data")
    @Operation(summary = "分页查询项目数据", description = "分页查询项目数据")
    public Response<IPage<Map<String,Object>>> getProjectStaData(@RequestBody ProjectStaKpiDTO qry) {
        IPage<Map<String,Object>> page = projectStaService.getProjectStaData(qry);
        return Response.success(page);
    }

    /**
     * 查询项目数据-导出
     *
     * @return
     */
    @PostMapping("/project/list/data/export")
    @Operation(summary = "分页查询项目数据-导出", description = "分页查询项目数据-导出")
    public void getProjectStaDataExport(HttpServletResponse resp,@RequestBody ProjectStaKpiDTO qry) throws IOException {
        TenantContext.setIgnore(true);
//        ProjectStaKpiDTO qry = new ProjectStaKpiDTO();
//        qry.setStaTimePeriod("4");
//        qry.setBegin("2022");
//        qry.setEnd("2023");
//        qry.setKpiCodes(Arrays.asList(new String[]{"project.carbon.gasUsageSO2.total"}));
//        qry.setBizProjectId("bizProjectId");
        qry.setPageNo(1);
        qry.setPageSize(100);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //查询分项api
        List<ProjectKpiConfigEntity> allKpi =  projectKpiConfigService.list(new LambdaQueryWrapper<>());
        Map<String,ProjectKpiConfigEntity> kpiNameMap  = allKpi.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, t -> t));

        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_PROJECT_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + DateUtils.getCurrent(LocalDateTime.now())+".xlsx");

            //设置头信息
            writer.addHeaderAlias("projectName", "项目名称");
            writer.addHeaderAlias("projectCode", "项目编码");
            writer.addHeaderAlias("staTime", "统计时间");
            qry.getKpiCodes().forEach(kpi -> {
                if(kpi.contains("_")){      //分区处理
                    String areaKpiPrefix = kpi.split("_")[0];
                    String label = String.format("%s_%s(%s)",kpiNameMap.get(areaKpiPrefix).getName(),kpi.split("_")[1],kpiNameMap.get(areaKpiPrefix).getUnit());
                    writer.addHeaderAlias(kpi,label);
                }else{                      //分项处理
                    writer.addHeaderAlias(kpi, String.format("%s(%s)",kpiNameMap.get(kpi).getName(),kpiNameMap.get(kpi).getUnit()));
                }
            });  //指标列
            //写数据
            IPage<Map<String,Object>> page = projectStaService.getProjectStaData(qry);
            List<Map<String,Object>> data =  page.getRecords();
            writer.write(data);
            writer.flush(os);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    @GetMapping("/project/list/data/export2")
    @Operation(summary = "分页查询项目数据-导出测试", description = "分页查询项目数据-导出测试")
    public void getProjectStaDataExport2(HttpServletResponse resp) throws IOException {
        TenantContext.setIgnore(true);
        ProjectStaKpiDTO qry = new ProjectStaKpiDTO();
        qry.setStaTimePeriod("4");
        qry.setBegin("2022");
        qry.setEnd("2023");
        qry.setKpiCodes(Arrays.asList(new String[]{"project.carbon.gasUsageSO2.total"}));
        qry.setBizProjectId("bizProjectId");
        qry.setPageNo(1);
        qry.setPageSize(100);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //查询分项api
        List<ProjectKpiConfigEntity> allKpi =  projectKpiConfigService.list(new LambdaQueryWrapper<>());
        Map<String,String> kpiNameMap  = allKpi.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode,ProjectKpiConfigEntity::getName));

        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_PROJECT_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

            //设置头信息
            writer.addHeaderAlias("projectName", "项目名称");
            writer.addHeaderAlias("projectCode", "项目编码");
            writer.addHeaderAlias("staTime", "统计时间");
            qry.getKpiCodes().forEach(kpi -> writer.addHeaderAlias(kpi, kpiNameMap.get(kpi)));  //指标列
            //写数据
            IPage<Map<String,Object>> page = projectStaService.getProjectStaData(qry);
            List<Map<String,Object>> data =  page.getRecords();
            writer.write(data);
            writer.flush(os);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 锦江看板-项目数据
     *
     * @return
     */
    @GetMapping("/board/RJD/projectInfo")
    @Operation(summary = "锦江看板-项目数据", description = "锦江看板-项目数据")
    public Response<KanbanRJDProjectVO> getRJDProjectInfo(@RequestParam("bizProjectId")String bizProjectId) {
        KanbanRJDProjectVO projectInfo = projectService.getByBizProjectId(bizProjectId);
        return Response.success(projectInfo);
    }

    /**
     * 锦江看板-当年电耗分析
     *
     * @return 锦江看板-当年电耗分析
     */
    @GetMapping("/board/RJD/electricity/year")
    @Operation(summary ="锦江看板-当年电耗分析", description = "锦江看板-当年电耗分析")
    public Response<KanbanRJDYearEleVO> getThisYearEle(@RequestParam(value = "bizProjectId",required = true)String bizProjectId,
                                                             @RequestParam(value = "boardType",required = true)String boardType){
        KanbanRJDYearEleVO yearEleData= projectStaService.getThisYearEle2(bizProjectId,boardType);
        return Response.success(yearEleData);
    }

    /**
     * 锦江看板-能耗数据
     * @param bizProjectId
     * @return
     */
    @GetMapping("/board/RJD/enegy")
    @Operation(summary = "锦江看板-能耗数据", description = "锦江看板-能耗数据")
    public Response<KanbanRJDEnergyVO> getRJDEnegyInfo(@RequestParam("bizProjectId")String bizProjectId) {
        KanbanRJDEnergyVO enegy = projectStaService.getRJDEnegy2(bizProjectId);
        return Response.success(enegy);
    }

    /**
     * 锦江看板-七日能耗
     * @param bizProjectId
     * @return
     */
    @GetMapping("/board/RJD/energy/day")
    @Operation(summary = "锦江看板-日用点曲线", description = "锦江看板-日用点曲线")
    public Response<KanbanRJDElectricDayVO> getRJDElectricDay(@RequestParam("bizProjectId")String bizProjectId) {
        KanbanRJDElectricDayVO enegy = projectStaService.getRJDElectricDay(bizProjectId);
        return Response.success(enegy);
    }

    /**
     * 锦江看板-七日能耗
     * @param bizProjectId
     * @return
     */
    @GetMapping("/board/RJD/energy/week")
    @Operation(summary = "锦江看板-七日能耗", description = "锦江看板-七日能耗")
    public Response<List<KanbanRJDEnergyWeeksVO>> getRJDEnegyWeek(@RequestParam("bizProjectId")String bizProjectId) {
        List<KanbanRJDEnergyWeeksVO> enegy = projectStaService.getRJDEnegyWeek2(bizProjectId);
        return Response.success(enegy);
    }

//    /**
//     * 锦江看板-七日能耗
//     * @param bizProjectId
//     * @return
//     */
//    @GetMapping("/board/RJD/energy/week")
//    @Operation(summary = "锦江看板-七日能耗", description = "锦江看板-七日能耗")
//    public Response<KanbanRJDEnergyWeekVO> getRJDEnegyWeek(@RequestParam("bizProjectId")String bizProjectId) {
//        KanbanRJDEnergyWeekVO enegy = projectStaService.getRJDEnegyWeek(bizProjectId);
//        return Response.success(enegy);
//    }

    /**
     * 锦江看板-当年能耗成本
     * @param bizProjectId
     * @return
     */
    @GetMapping("/board/RJD/energy/cost")
    @Operation(summary = "锦江看板-当年能耗成本", description = "锦江看板-当年能耗成本")
    public Response<KanbanRJDEnergyCostVO> getRJDEnegyCost(@RequestParam("bizProjectId")String bizProjectId) {
        KanbanRJDEnergyCostVO enegy = projectStaService.getRJDEnegyCost(bizProjectId);
        return Response.success(enegy);
    }

    /**
     * 锦江看板-当年碳排
     * @param bizProjectId
     * @return
     */
    @GetMapping("/board/RJD/energy/carbon")
    @Operation(summary = "锦江看板-当年碳排", description = "锦江看板-当年碳排")
    public Response<KanbanRJDEnergyCarbonVO> getRJDEnegyCarbon(@RequestParam("bizProjectId")String bizProjectId) {
        KanbanRJDEnergyCarbonVO enegy = projectStaService.getRJDEnegyCarbon(bizProjectId);
        return Response.success(enegy);
    }

    /**
     *
     *
     * @return
     */
    @GetMapping("/board/RJD/test3")
    @Operation(summary = "锦江看板-项目数据", description = "锦江看板-项目数据")
    public Response<Object> test3() {

        String [] kpis = new String[]{"project.electricity.subGuestRoomEnergy.total","project.carbon.totalCoal.total"};
        //ProjectStaSubitemMonthVO vo = projectStaService.getMonthSta("2023","06",Arrays.asList(kpis),"PJ00000001");
        ProjectStaSubitemYearVO vo = projectStaService.getYearSta("2023",Arrays.asList(kpis),"PJ00000001");
        return Response.success(vo);

    }




}
