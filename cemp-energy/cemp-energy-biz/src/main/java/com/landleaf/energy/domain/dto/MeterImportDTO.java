package com.landleaf.energy.domain.dto;

import cn.hutool.core.util.StrUtil;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class MeterImportDTO {
    private MeterReadCycleEnum cycle;
    private List<String> errMsg;
    private LocalDate dayTime;
    private YearMonth monthTime;
    private String bizProjectId;
    private String projectName;
    private List<String> bizDeviceIdList;
    private HashSet<String> deviceNameList;
    private List<MeterImportDataDTO> dataList;
    private String categoryCode;

    public String formatErrMsg() {
        return StrUtil.join(Strings.LINE_SEPARATOR, errMsg);
    }

    {
        this.errMsg = new ArrayList<>();
        this.bizDeviceIdList = new ArrayList<>();
        this.deviceNameList = new HashSet<>();
        this.dataList = new ArrayList<>();
    }
}
