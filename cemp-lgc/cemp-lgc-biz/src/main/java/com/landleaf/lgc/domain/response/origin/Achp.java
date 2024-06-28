package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备运行状态
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class Achp {

    class AchpDetail{
        @Schema(description = "开关状态")
        private String adOnOffState;
    }

    /**
     * 空调主机明细
     */
    @Schema(description = "空调主机明细")
    private List<AchpDetail> details;



}
