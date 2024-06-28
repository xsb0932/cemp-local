package com.landleaf.bms.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.ProductAlarmConfAddRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfCodeUniqueRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfEditRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfQueryRequest;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import com.landleaf.bms.service.ProductAlarmConfService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 产品-告警码管理
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/alarm")
@Tag(name = "产品-告警码管理")
public class ProductAlarmConfController {
    private final ProductAlarmConfService productAlarmConfService;
    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private static final String EXCEL_DEVICE_TEMPLATE_FILE_NAME = "产品告警码批量导入模板";

    /**
     * 新增产品告警配置
     *
     * @param request 新增请求
     */
    @PostMapping
    @Operation(summary = "新增产品告警配置", description = "新增产品告警配置")
    public Response<Void> addAlarmConf(@RequestBody @Validated ProductAlarmConfAddRequest request) {
        productAlarmConfService.addAlarmConf(request);
        return Response.success();
    }

    /**
     * 编辑产品告警配置
     *
     * @param request 编辑请求
     */
    @PutMapping
    @Operation(summary = "编辑产品告警配置", description = "编辑产品告警配置")
    public Response<Void> editAlarmConf(@RequestBody @Validated ProductAlarmConfEditRequest request) {
        productAlarmConfService.editAlarmConf(request);
        return Response.success();
    }

    /**
     * 删除产品告警配置
     *
     * @param id 配置id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品告警配置", description = "删除产品告警配置")
    public Response<Void> deleteAlarmConf(@PathVariable Long id) {
        productAlarmConfService.deleteAlarmConf(id);
        return Response.success();
    }

    /**
     * 分页查询告警配置
     *
     * @param request 查询条件
     * @return 告警配置列表
     */
    @GetMapping("/page-query")
    @Operation(summary = "分页查询告警配置", description = "分页查询告警配置")
    public Response<Page<ProductAlarmConfListResponse>> pageQuery(@Validated ProductAlarmConfQueryRequest request) {
        Page<ProductAlarmConfListResponse> productAlarmConfListResponsePage = productAlarmConfService.pageQuery(request);
        return Response.success(productAlarmConfListResponsePage);
    }

    /**
     * 校验告警码是否唯一
     * <p>
     * true 唯一， false 不唯一
     *
     * @return true 唯一， false 不唯一
     */
    @PostMapping("/check-code-unique")
    @Operation(summary = "校验告警码是否唯一", description = "校验告警码是否唯一")
    public Response<Boolean> checkCodeUnique(@RequestBody @Validated ProductAlarmConfCodeUniqueRequest request) {
        boolean b = productAlarmConfService.checkCodeUnique(request);
        return Response.success(b);
    }

    /**
     * excel 批量导入模版
     *
     * @return excel 模版
     */
    @GetMapping("/export/template")
    @Operation(summary = "下载模板", description = "下载模板")
    public void exportTemplate(HttpServletResponse resp) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //所有kpi名称
        TenantContext.setIgnore(true);
        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_DEVICE_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

            List<String> rowDatas = new ArrayList<>();
            rowDatas.add("告警码");
            rowDatas.add("告警类型");
            rowDatas.add("告警描述");
            rowDatas.add("触发等级");
            rowDatas.add("复归等级");
            rowDatas.add("确认方式");

            writer.writeHeadRow(rowDatas);
            writer.flush(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量导入产品告警码
     *
     * @param productId 产品id
     * @param file      excel
     */
    @PostMapping(value = "/import/{productId}")
    @Operation(summary = "批量导入产品告警码", description = "批量导入产品告警码")
    public Response<List<String>> importAlarm(@PathVariable("productId") Long productId, @RequestParam(value = "file") MultipartFile file) throws IOException {
        List<String> errMsg = productAlarmConfService.importFile(file, productId);
        if (!CollectionUtils.isEmpty(errMsg)) {
            return Response.error("500", String.join(";", errMsg));
        } else {
            return Response.success();
        }
    }
}
