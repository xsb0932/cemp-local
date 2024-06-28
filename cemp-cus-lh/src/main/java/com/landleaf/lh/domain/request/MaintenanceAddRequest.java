package com.landleaf.lh.domain.request;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.lh.domain.entity.MaintenanceSheetEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
@Schema(description = "报修单添加参数封装")
public class MaintenanceAddRequest {
    @Schema(description = "项目id")
    @NotBlank(message = "项目id不能为空")
    private String bizProjectId;
    @Schema(description = "房号")
    @NotBlank(message = "房号不能为空")
    private String room;
    @Schema(description = "报修月份")
    @NotBlank(message = "报修月份不能为空")
    private String yearMonth;
    @Schema(description = "报修日期")
    @NotBlank(message = "报修日期不能为空")
    private String maintenanceDate;
    @Schema(description = "报修单类别")
    @NotBlank(message = "报修单类别不能为空")
    private String maintenanceType;
    @Schema(description = "报修内容")
    private String content;

    public MaintenanceSheetEntity convertToEntity() {
        MaintenanceSheetEntity entity = new MaintenanceSheetEntity();
        try {
            YearMonth month = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
            entity.setMaintenanceYear(month.getYear())
                    .setMaintenanceMonth(month.getMonthValue())
                    .setMaintenanceYearMonth(month.atDay(1));
        } catch (Exception e) {
            throw new BusinessException("报修月份格式异常 {}", yearMonth, e);
        }
        try {
            LocalDate date = LocalDate.parse(maintenanceDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            entity.setMaintenanceDate(date);
        } catch (Exception e) {
            throw new BusinessException("报修日期格式异常 {}", maintenanceDate, e);
        }
        entity.setBizProjectId(bizProjectId)
                .setMaintenanceType(maintenanceType)
                .setRoom(room)
                .setContent(content)
                .setTenantId(TenantContext.getTenantId());
        return entity;
    }
}
