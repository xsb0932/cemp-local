package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.CategoryCatalogueEntity;
import com.landleaf.bms.domain.entity.CategoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 品类目录树
 *
 * @author yue lin
 * @since 2023/7/6 17:15
 */
@Data
public class CategoryTreeResponse {

    /**
     * ID
     */
    @Schema(description = "ID")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 是否是目录
     */
    private Boolean isCatalogue;

    /**
     * 父级目录ID
     */
    @Schema(description = "父级目录ID")
    private Long parentId;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private String image;

    /**
     * 子集
     */
    @Schema(description = "子集")
    private List<CategoryTreeResponse> children;

    public static CategoryTreeResponse parseCatalogue(CategoryCatalogueEntity entity) {
        CategoryTreeResponse categoryTreeResponse = new CategoryTreeResponse();
        categoryTreeResponse.setId(String.valueOf(entity.getId()));
        categoryTreeResponse.setName(entity.getName());
        categoryTreeResponse.setIsCatalogue(true);
        categoryTreeResponse.setParentId(entity.getParentId());
        categoryTreeResponse.setChildren(new ArrayList<>());
        return categoryTreeResponse;
    }

    public static CategoryTreeResponse parseCategory(CategoryEntity entity) {
        CategoryTreeResponse categoryTreeResponse = new CategoryTreeResponse();
        categoryTreeResponse.setId(entity.getBizId());
        categoryTreeResponse.setName(entity.getName());
        categoryTreeResponse.setIsCatalogue(false);
        categoryTreeResponse.setParentId(entity.getParentId());
        categoryTreeResponse.setImage(entity.getImage());
        categoryTreeResponse.setChildren(new ArrayList<>());
        return categoryTreeResponse;
    }

    public static CategoryTreeResponse createTopLevel() {
        CategoryTreeResponse categoryTreeResponse = new CategoryTreeResponse();
        categoryTreeResponse.setId("0");
        categoryTreeResponse.setName("全部");
        categoryTreeResponse.setIsCatalogue(true);
        categoryTreeResponse.setParentId(-1L);
        categoryTreeResponse.setChildren(new ArrayList<>());
        return categoryTreeResponse;
    }

}
