package com.landleaf.bms.domain.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.landleaf.bms.domain.entity.DictTypeEntity;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OperateLogResponse
 *
 * @author xushibai
 * @since 2024/4/25
 **/
@Data
@Schema(name = "操作日志返回", description = "操作日志返回")
public class OperateLogResponse {

    @Schema(description = "日志内容")
    private String logContent;

    @Schema(description = "日志时间")
    private String logTime;

    public OperateLogResponse(String logContent, String logTime) {
        this.logContent = logContent;
        this.logTime = logTime;
    }
}
