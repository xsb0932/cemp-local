package com.landleaf.lh.domain.dto;

import com.landleaf.lh.domain.entity.MaintenanceSheetEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceImportRowDTO {
    private int row;
    private String projectName;
    private String bizProjectId;
    private String room;
    private LocalDate maintenanceYearMonth;
    private LocalDate maintenanceDate;
    private String maintenanceType;
    private String content;
    private Long tenantId;

    public MaintenanceSheetEntity convertToEntity() {
        MaintenanceSheetEntity entity = new MaintenanceSheetEntity()
                .setBizProjectId(bizProjectId)
                .setMaintenanceYear(maintenanceYearMonth.getYear())
                .setMaintenanceMonth(maintenanceYearMonth.getMonthValue())
                .setMaintenanceYearMonth(maintenanceYearMonth)
                .setMaintenanceDate(maintenanceDate)
                .setMaintenanceType(maintenanceType)
                .setRoom(room)
                .setContent(content);
        entity.setTenantId(tenantId);
        return entity;
    }
}
