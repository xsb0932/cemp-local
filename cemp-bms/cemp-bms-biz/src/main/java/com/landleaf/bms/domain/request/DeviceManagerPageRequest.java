package com.landleaf.bms.domain.request;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Data
@Schema(description = "设备管理分页列表查询参数封装")
public class DeviceManagerPageRequest extends PageParam {
    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "分组树节点id")
    private String nodeId1;

    @Schema(description = "分组树节点类型")
    private Integer type1;

    @Schema(description = "产品树节点id")
    private String nodeId2;

    @Schema(description = "产品树节点类型")
    private Integer type2;

    @Schema(description = "通讯状态：0-离线 1-在线")
    private Integer cst;

    public void validateParam() {
        if (type1 != null && type2 != null) {
            throw new IllegalArgumentException("分组查询和产品查询不可同时存在");
        } else if (type1 != null && StrUtil.isBlank(nodeId1)) {
            throw new IllegalArgumentException("分组树节点id为空");
        } else if (Objects.equals(type1, 2) && !NumberUtil.isLong(nodeId1)) {
            throw new IllegalArgumentException("分组树节点id错误");
        } else if (type2 != null && StrUtil.isBlank(nodeId2)) {
            throw new IllegalArgumentException("产品树节点id为空");
        } else if (Objects.equals(type2, 0) && !NumberUtil.isLong(nodeId2)) {
            throw new IllegalArgumentException("产品树节点id错误");
        }
    }

    public boolean selectByProject() {
        return type1 != null;
    }

    public boolean SelectByProduct() {
        return type2 != null;
    }
}
