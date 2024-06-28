package com.landleaf.monitor.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Yang
 */
@Data
@Schema(name = "ViewPageRequest", description = "分页查询视图列表VO")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ViewPageRequest extends PageParam {
    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "视图名称")
    private String viewName;
}
