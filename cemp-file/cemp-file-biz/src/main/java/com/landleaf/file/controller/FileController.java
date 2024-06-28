package com.landleaf.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.file.domain.enums.ErrorCodeConstants;
import com.landleaf.file.domain.enums.StoreFileType;
import com.landleaf.file.service.FileService;
import com.landleaf.oauth.api.TenantApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 文件相关接口
 *
 * @author 张力方
 * @since 2023/6/13
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@Tag(name = "文件相关接口")
@Slf4j
public class FileController {
    private final FileService fileService;
    private final TenantApi tenantApi;

    /**
     * 文件上传
     *
     * @param file     文件
     * @param fileType 文件类型  IMAGE - 图片，FILE - 文件，OTHER 其他
     * @return 文件下载 url
     */
    @PostMapping("/upload")
    public Response<String> uploadFile(@RequestParam MultipartFile file, @RequestParam StoreFileType fileType) {
        try {
            String fileUrl = fileService.storeFile(file, fileType);
            return Response.success(fileUrl);
        } catch (Exception e) {
            log.error("文件上传错误", e);
            throw new ServiceException(ErrorCodeConstants.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName 桶名
     * @param objectPath 对象名称
     */
    @GetMapping("/download/{bucketName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucketName, @RequestParam String objectPath) {
        try {
            // 读取内容
            byte[] content = fileService.download(bucketName, objectPath);
            HttpHeaders headers = new HttpHeaders();
            if (objectPath.contains(StoreFileType.IMAGE.getPath())) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            if (content == null) {
                log.warn("[下载文件][path({}) 文件不存在]", objectPath);
                return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
            }
            headers.setContentLength(content.length);
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeConstants.FILE_DOWNLOAD_FAIL);
        }
    }

    @GetMapping("/download/meter-import-excel")
    @Operation(summary = "抄表导入模板下载", description = "抄表导入模板下载")
    public void meterImportExcel(HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()) {
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("抄表数据导入模板", UTF_8) + ".xlsx");


            Map<String, String> row1 = new LinkedHashMap<>();
            row1.put("项目名称", "xx项目");
            row1.put("设备名称", "xx设备");
            row1.put("计量周期", "2024-01-01/2024-01");
            row1.put("期初表显值", "自动");
            row1.put("期末表显值", "888");
            row1.put("本期用量", "自动");
            row1.put("", "本条为填写说明，导入时，请删除！请注意月抄表和日抄表的计量周期格式。");

            ArrayList<Map<String, String>> rows = CollUtil.newArrayList(row1);
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.setColumnWidth(0, 30);
            writer.setColumnWidth(1, 30);
            writer.setColumnWidth(2, 30);
            writer.setColumnWidth(3, 25);
            writer.setColumnWidth(4, 25);
            writer.setColumnWidth(5, 25);
            writer.setColumnWidth(6, 60);

            DataFormat format = writer.getWorkbook().createDataFormat();
            CellStyle cellStyle = writer.getCellStyle();
            cellStyle.setDataFormat(format.getFormat("@"));

            writer.write(rows, true);

            writer.flush(out, true);
            writer.close();
        } catch (IOException e) {
            log.error("抄表导入模板下载异常", e);
        }
    }

}
