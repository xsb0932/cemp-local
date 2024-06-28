package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.CategoryEntity;
import com.landleaf.web.validation.Create;
import com.landleaf.web.validation.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品类变更请求参数
 *
 * @author yue lin
 * @since 2023/7/6 13:23
 */
@Data
@Schema(description = "品类创建")
public class CategoryChangeRequest {

    /**
     * id
     */
    @NotNull(groups = Update.class, message = "id不能为空")
    @Null(groups = Create.class, message = "id必须为空")
    @Schema(description = "id(创建为空更新传递)")
    private Long id;

    /**
     * 品类名称
     */
    @NotBlank(groups = {Update.class, Create.class}, message = "品类名称不能为空")
    @Size(groups = {Update.class, Create.class}, min = 1, max = 50, message = "品类名称长度{min}-{max}")
    @Schema(description = "品类名称", example = "品类名称")
    private String name;

    /**
     * 上级目录ID，没有父级则为0
     */
    @NotNull(groups = {Update.class, Create.class}, message = "上级目录不能为空")
    @Schema(description = "上级目录ID，没有父级则为0", example = "0")
    private Long parentId;

    /**
     * 品类编码
     */
    @NotBlank(groups = {Update.class, Create.class}, message = "品类编码不能为空")
    @Schema(description = "品类编码", example = "品类编码")
    private String code;

    /**
     * 图片
     */
    @Schema(description = "图片URL", example = "图片URL")
    private String image;

    /**
     * 品类描述
     */
    @NotBlank(groups = {Update.class, Create.class}, message = "品类描述不能为空")
    @Size(groups = {Update.class, Create.class}, min = 1, max = 255, message = "品类描述长度{min}-{max}")
    @Schema(description = "品类描述", example = "品类描述")
    private String description;

    public CategoryEntity toEntity() {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setParentId(parentId);
        entity.setCode(code);
        entity.setImage(image);
        entity.setDescription(description);
        return entity;
    }

}
