package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品类管理，功能变更
 *
 * @author yue lin
 * @since 2023/7/11 15:09
 */
@Data
@Schema(description = "品类管理，功能变更")
public class CategoryFeatureChangeRequest {

    /**
     * 功能ID
     */
    @NotNull(message = "功能ID不能为空")
    @Schema(description = "功能ID")
    private Long functionId;

    /**
     * 功能名称
     */
    @Size(min = 1, max = 50, message = "功能名称长度{min}-{max}")
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 功能类别(不需要传递)
     * @see  [01， 02， 03， 04, 05]
     */
    @Schema(description = "功能类别")
    private String functionCategory;

    /**
     * 品类业务id
     */
    @NotBlank(message = "品类业务ID不能为空")
    @Schema(description = "品类业务id")
    private String categoryBizId;


    public CategoryDeviceAttributeEntity toDeviceAttribute() {
        CategoryDeviceAttributeEntity entity = new CategoryDeviceAttributeEntity();
        entity.setId(functionId);
        entity.setFunctionName(functionName);
        return entity;
    }
    public CategoryDeviceEventEntity toDeviceEvent() {
        CategoryDeviceEventEntity entity = new CategoryDeviceEventEntity();
        entity.setId(functionId);
        entity.setFunctionName(functionName);
        return entity;
    }
    public CategoryDeviceParameterEntity toDeviceParameter() {
        CategoryDeviceParameterEntity entity = new CategoryDeviceParameterEntity();
        entity.setId(functionId);
        entity.setFunctionName(functionName);
        return entity;
    }
    public CategoryDeviceServiceEntity toDeviceService() {
        CategoryDeviceServiceEntity entity = new CategoryDeviceServiceEntity();
        entity.setId(functionId);
        entity.setFunctionName(functionName);
        return entity;
    }
    public CategoryProductParameterEntity toProductParameter() {
        CategoryProductParameterEntity entity = new CategoryProductParameterEntity();
        entity.setId(functionId);
        entity.setFunctionName(functionName);
        return entity;
    }

}
