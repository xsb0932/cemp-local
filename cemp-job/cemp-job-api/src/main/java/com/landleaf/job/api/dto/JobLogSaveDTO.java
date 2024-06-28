package com.landleaf.job.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobLogSaveDTO {
    @NotNull
    private Long jobId;
    @NotNull
    private Long tenantId;

    private String projectIds;
    private String projectNames;
    @NotNull
    private Integer status;
    @NotNull
    private Integer execType;
    @NotNull
    private LocalDateTime execTime;
    @NotNull
    private Long execUser;
}
