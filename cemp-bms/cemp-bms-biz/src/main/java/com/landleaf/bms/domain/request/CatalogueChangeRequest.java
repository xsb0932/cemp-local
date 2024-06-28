package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.CategoryCatalogueEntity;
import com.landleaf.web.validation.Create;
import com.landleaf.web.validation.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品类管理-目录变更参数
 *
 * @author yue lin
 * @since 2023/7/6 13:24
 */
@Data
@Schema(description = "目录创建")
public class CatalogueChangeRequest {

    /**
     * id
     */
    @NotNull(groups = Update.class, message = "id不能为空")
    @Null(groups = Create.class, message = "id必须为空")
    @Schema(description = "id(创建为空更新传递)")
    private Long id;

    /**
     * 目录名称
     */
    @NotBlank(groups = {Update.class, Create.class}, message = "目录名称不能为空")
    @Size(groups = {Update.class, Create.class}, min = 1, max = 50, message = "目录名称长度{min}-{max}")
    @Schema(description = "目录名称", example = "目录名称")
    private String name;

    /**
     * 上级目录ID，没有父级则为0
     */
    @NotNull(groups = {Update.class, Create.class}, message = "上级目录不能为空")
    @Schema(description = "上级目录ID，没有父级则为0", example = "0")
    private Long parentId;

    public CategoryCatalogueEntity toEntity() {
        CategoryCatalogueEntity entity = new CategoryCatalogueEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setParentId(parentId);
        return entity;
    }

}
