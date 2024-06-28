package com.landleaf.jjgj.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 抄表电表菜单返回值
 *
 * @author Tycoon
 * @since 2023/8/18 15:26
 **/
@Data
public class ElectricityMeterReadingTreeResponse {

    /**
     * 项目业务ID
     */
    @Schema(description = "项目业务ID")
    private String id;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String name;

    /**
     * 电表列表
     */
    @Schema(description = "电表列表")
    private List<Electricity> children;


    @Data
    public static class Electricity {

        /**
         * 电表业务ID
         */
        @Schema(description = "电表业务ID")
        private String id;

        /**
         * 电表名称
         */
        @Schema(description = "电表名称")
        private String name;

    }
}
