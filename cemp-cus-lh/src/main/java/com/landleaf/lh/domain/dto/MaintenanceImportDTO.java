package com.landleaf.lh.domain.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Data
public class MaintenanceImportDTO {
    private List<String> errMsg;
    private HashSet<String> projectNameList;
    private HashMap<String, String> projectMap;
    private List<MaintenanceImportRowDTO> dataList;

    public String formatErrMsg() {
        return StrUtil.join(Strings.LINE_SEPARATOR, errMsg);
    }

    {
        this.errMsg = new ArrayList<>();
        this.projectNameList = new HashSet<>();
        this.dataList = new ArrayList<>();
    }
}
