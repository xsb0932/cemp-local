package com.landleaf.jjgj.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.jjgj.domain.dto.CheckinDayAddDTO;
import com.landleaf.jjgj.domain.dto.CheckinDayQueryDTO;
import com.landleaf.jjgj.domain.entity.CheckinDayEntity;
import com.landleaf.jjgj.domain.vo.CheckinDayVO;
import com.landleaf.jjgj.domain.wrapper.CheckinDayWrapper;
import com.landleaf.jjgj.service.CheckinDayService;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

/**
 * JjgjCheckinDayEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2023-10-16
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/checkin-day")
@Tag(name = "checkinDayEntity对象的控制层接口定义", description = "checkinDayEntity对象的控制层接口定义")
public class CheckinDayController {
    /**
     * checkinDayEntity对象的相关逻辑操作句柄
     */
    private final CheckinDayService checkinDayServiceImpl;

    private static final String TEMPLATE_FILE_NAME = "入住率导入模板";

    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private JobLogApi jobLogApi;
    /**
     * 新增或修改checkinDayEntity对象数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入CheckinDayAddDTO")
    public Response<CheckinDayAddDTO> save(@RequestBody CheckinDayAddDTO addInfo) {
        Long tenantId = TenantContext.getTenantId();
        addInfo.setTenantId(tenantId);
        if (null == addInfo.getId()) {
            addInfo = checkinDayServiceImpl.save(addInfo);
        } else {
            checkinDayServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 入住率月统计
     *

     * @return
     */
    @PostMapping("/sta/month")
    @Operation(summary = "入住率月统计", description = "入住率月统计")
    public Response<Void> staMonth(@RequestBody JobRpcRequest request) {
        TenantContext.setTenantId(2L);
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                // 这种定制的懒得搞了 开发生产id目前一致 写死得了
                .setTenantId(2L)
                .setProjectIds("PJ00000001")
                .setProjectNames("锦江体验中心酒店")
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        //测试代码
//        LocalDateTime dt = LocalDateTime.now();
//        request.setExecTime(dt);

        try {

            if (null != request.getExecTime()) {
                // 手动执行
                LocalDateTime time  = request.getExecTime().minusMonths(1L);
                YearMonth ym = YearMonth.of(time.getYear(),time.getMonthValue());
                checkinDayServiceImpl.staMonth(ym,request );
            }
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("统计月入住异常", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success();

    }

    /**
     * 根据编号，删除CheckinDayEntity对象数据（逻辑删除）
     *
     * @param ids 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除CheckinDayEntity对象信息", description = "传入ids,多个以逗号分隔")
    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
        checkinDayServiceImpl.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 根据编号，查询CheckinDayEntity对象详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询CheckinDayEntity对象详情", description = "传入ids,多个以逗号分隔")
    public Response<CheckinDayVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Integer id) {
        CheckinDayEntity entity = checkinDayServiceImpl.selectById(id);
        return Response.success(CheckinDayWrapper.builder().entity2VO(entity));
    }

    /**
     * 查询JjgjCheckinDayEntity对象列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询JjgjCheckinDayEntity对象列表数据", description = "")
    public Response<List<CheckinDayVO>> list(CheckinDayQueryDTO queryInfo) {
        List<CheckinDayEntity> cdList = checkinDayServiceImpl.list(queryInfo);
        return Response.success(CheckinDayWrapper.builder().listEntity2VO(cdList));
    }

    /**
     * 分页查询JjgjCheckinDayEntity对象列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询JjgjCheckinDayEntity对象列表数据", description = "")
    public Response<PageDTO<CheckinDayVO>> page(CheckinDayQueryDTO queryInfo) {
        IPage<CheckinDayEntity> page = checkinDayServiceImpl.page(queryInfo);
        return Response.success(CheckinDayWrapper.builder().pageEntity2VO(page));
    }

    /**
     * 导出设备
     *
     * @return
     */
    @GetMapping("/export")
    @Operation(summary = "下载模板", description = "下载模板")
    public void export(HttpServletResponse resp) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //所有kpi名称
        TenantContext.setIgnore(true);
        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

            List<String> rowDatas = new ArrayList<>();
            rowDatas.add("日期");
            rowDatas.add("入住数");
            rowDatas.add("");
            rowDatas.add("示例格式：日期：2023-08-01    入住数：50");
            writer.writeHeadRow(rowDatas);
            // 设置示例居左展示
            writer.getOrCreateCellStyle(3,0).setAlignment(HorizontalAlignment.LEFT);
            writer.autoSizeColumn(3);
            writer.flush(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/import/{bizProjectId}")
    @Operation(summary = "批量导入", description = "批量导入")
    public Response<List<String>> importFamilyInfo(@PathVariable("bizProjectId") String bizProjectId, @RequestParam(value = "file", required = true) MultipartFile file, HttpServletResponse response) throws IOException {
        List<String> errMsg = checkinDayServiceImpl.importFile(bizProjectId, file);
        if (errMsg != null && errMsg.size() > 0) {
            return Response.error("500", String.join(";", errMsg));
        } else {
            return Response.success();
        }
    }
}
