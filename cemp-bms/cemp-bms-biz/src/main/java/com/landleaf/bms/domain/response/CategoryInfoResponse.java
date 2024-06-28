package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.CategoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 品类详情
 *
 * @author yue lin
 * @since 2023/7/7 15:46
 */
@Data
public class CategoryInfoResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;

    /**
     * 品类名称
     */
    @Schema(description = "品类名称")
    private String name;

    /**
     * 品类归属
     */
    @Schema(description = "品类归属")
    private String ascription;

    /**
     * 所属目录ID
     */
    @Schema(description = "所属目录ID")
    private Long parentId;

    /**
     * 品类编码
     */
    @Schema(description = "品类编码")
    private String code;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private String image;

    /**
     * 品类描述
     */
    @Schema(description = "品类描述")
    private String description;

    public  static CategoryInfoResponse parse(CategoryEntity entity, String ascription) {
        CategoryInfoResponse response = new CategoryInfoResponse();
        response.setId(entity.getId());
        response.setBizId(entity.getBizId());
        response.setName(entity.getName());
        response.setParentId(entity.getParentId());
        response.setAscription(ascription);
        response.setCode(entity.getCode());
        response.setImage(entity.getImage());
        response.setDescription(entity.getDescription());
        return response;
    }

}
