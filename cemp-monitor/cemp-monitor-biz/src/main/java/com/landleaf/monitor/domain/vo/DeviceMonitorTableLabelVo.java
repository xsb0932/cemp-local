package com.landleaf.monitor.domain.vo;

import com.landleaf.monitor.domain.entity.DeviceMonitorTableLabelEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备监控表格表头
 *
 * @author yue lin
 * @since 2023/7/20 11:14
 */
@Data
@Schema(description = "设备监控表格表头")
public class DeviceMonitorTableLabelVo {

    /**
     * 品类业务id
     */
    @Schema(description = "品类业务id")
    private String categoryBizId;

    /**
     * prop
     */
    @Schema(description = "prop")
    private String prop;

    /**
     * label
     */
    @Schema(description = "label")
    private String label;

    /**
     * show
     */
    @Schema(description = "show")
    private Boolean show;

    /**
     * sort
     */
    @Schema(description = "sort")
    private Integer sort;

    @Schema(description = "width")
    private Integer width;

    public static DeviceMonitorTableLabelVo from(DeviceMonitorTableLabelEntity entity) {
        DeviceMonitorTableLabelVo tableLabel = new DeviceMonitorTableLabelVo();
        tableLabel.setCategoryBizId(entity.getCategoryBizId());
        tableLabel.setProp(entity.getFieldKey());
        tableLabel.setLabel(entity.getFieldLabel());
        tableLabel.setShow(entity.getFieldShow());
        tableLabel.setSort(entity.getSort());
        tableLabel.setWidth(entity.getWidth());
        return tableLabel;
    }

}
