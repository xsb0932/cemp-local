package com.landleaf.monitor.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 设备-监测平台的查询时的参数封装
 *
 * @author xshibai
 * @since 2023-06-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "DeviceMonitorCurrentDTO对象", description = "设备-监测平台-当前状态参数封装")
public class DeviceMonitorCurrentDTO extends PageParam {

    /**
     * 品类id（全局唯一id）
     */
    @Schema(description = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 项目id 集合
     */
    @Schema(description = "项目id集合")
    private List<String> projectIds;

	/**
	 * 设备名称
	 */
    @Schema(description = "设备名称")
    private String name;

}
