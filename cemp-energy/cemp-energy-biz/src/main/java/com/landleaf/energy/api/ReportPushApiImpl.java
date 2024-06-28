package com.landleaf.energy.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.api.dto.ProjectReportPushDTO;
import com.landleaf.energy.domain.dto.ProjectStaKpiDTO;
import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import com.landleaf.energy.enums.ApiConstants;
import com.landleaf.energy.service.ProjectKpiConfigService;
import com.landleaf.energy.service.ProjectStaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@Tag(name = "Feign 服务 - 报表推送")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportPushApiImpl {
    private final ProjectKpiConfigService projectKpiConfigService;
    private final ProjectStaService projectStaService;


    @Operation(summary = "查询设备当日的用电量")
    @PostMapping(ApiConstants.PREFIX + "/project/report-push-data")
    public void projectReportPushData(@RequestBody ProjectReportPushDTO request, HttpServletResponse response) {
        TenantContext.setIgnore(true);
        ProjectStaKpiDTO qry = new ProjectStaKpiDTO();
        BeanUtil.copyProperties(request, qry);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //查询分项api
        List<ProjectKpiConfigEntity> allKpi = projectKpiConfigService.list(new LambdaQueryWrapper<>());
        Map<String, ProjectKpiConfigEntity> kpiNameMap = allKpi.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, t -> t));

        try (ServletOutputStream os = response.getOutputStream()) {
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            //设置头信息
            writer.addHeaderAlias("projectName", "项目名称");
            writer.addHeaderAlias("projectCode", "项目编码");
            writer.addHeaderAlias("staTime", "统计时间");
            if (CollectionUtil.isNotEmpty(qry.getKpiCodes())) {
                qry.getKpiCodes().forEach(kpi -> {
                    if (kpi.contains("_")) {      //分区处理
                        String areaKpiPrefix = kpi.split("_")[0];
                        String label = String.format("%s_%s(%s)", kpiNameMap.get(areaKpiPrefix).getName(), kpi.split("_")[1], kpiNameMap.get(areaKpiPrefix).getUnit());
                        writer.addHeaderAlias(kpi, label);
                    } else {                      //分项处理
                        writer.addHeaderAlias(kpi, String.format("%s(%s)", kpiNameMap.get(kpi).getName(), kpiNameMap.get(kpi).getUnit()));
                    }
                });  //指标列
                //写数据
                IPage<Map<String, Object>> page = projectStaService.getProjectStaData(qry);
                List<Map<String, Object>> data = page.getRecords();
                writer.write(data);
            } else {
                writer.write(Collections.emptyList());
            }
            writer.flush(os);
        } catch (IOException e) {
            log.error("报表推送数据获取失败", e);
        }
    }
}
