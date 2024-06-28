package com.landleaf.lh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.lh.domain.dto.MaintenanceImportDTO;
import com.landleaf.lh.domain.entity.MaintenanceSheetEntity;
import com.landleaf.lh.domain.request.MaintenanceAddRequest;
import com.landleaf.lh.domain.request.MaintenanceEditRequest;
import com.landleaf.lh.domain.request.MaintenanceExportRequest;
import com.landleaf.lh.domain.request.MaintenancePageRequest;
import com.landleaf.lh.domain.response.MaintenanceInfoResponse;
import com.landleaf.lh.domain.response.MaintenancePageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * MaintenanceSheetEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2024-05-22
 */
public interface MaintenanceSheetService extends IService<MaintenanceSheetEntity> {
    Page<MaintenancePageResponse> selectPage(MaintenancePageRequest request);

    void add(MaintenanceAddRequest request);

    void edit(MaintenanceEditRequest request);

    void delete(Long id);

    MaintenanceInfoResponse info(Long id);

    void export(MaintenanceExportRequest request, HttpServletResponse response);

    MaintenanceImportDTO excelImportCheck(MultipartFile file);

    void excelImportSave(MaintenanceImportDTO dto);
}