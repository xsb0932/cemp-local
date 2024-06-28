package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.CategoryCatalogueEntity;
import com.landleaf.bms.domain.entity.CategoryEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.enums.BmsConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "物联平台-设备管理产品节点树")
public class DeviceManagerProductTreeResponse {
    @Schema(description = "节点id")
    private String nodeId;
    @Schema(description = "父节点id")
    private String parentNodeId;
    @Schema(description = "节点名称")
    private String name;
    @Schema(description = "节点类型: 0-品类目录 1-品类 2-产品")
    private Integer type;
    @Schema(description = "节点图标")
    private String image;
    @Schema(description = "子节点")
    private List<DeviceManagerProductTreeResponse> children;

    @Schema(description = "产品id(添加设备时使用)")
    private Long productId;

    public static DeviceManagerProductTreeResponse newRootNode() {
        return new DeviceManagerProductTreeResponse()
                .setNodeId(BmsConstants.ROOT_CATEGORY_CATALOGUE_ID)
                .setParentNodeId("00")
                .setName(BmsConstants.ROOT_CATEGORY_CATALOGUE_NAME)
                .setType(0);
    }

    public static DeviceManagerProductTreeResponse convertToCatalogue(CategoryCatalogueEntity dto) {
        return new DeviceManagerProductTreeResponse()
                .setNodeId(dto.getId().toString())
                .setParentNodeId(dto.getParentId().toString())
                .setName(dto.getName())
                .setType(0);
    }

    public static DeviceManagerProductTreeResponse convertToCategory(CategoryEntity dto) {
        return new DeviceManagerProductTreeResponse()
                .setNodeId(dto.getBizId())
                .setParentNodeId(dto.getParentId().toString())
                .setName(dto.getName())
                .setImage(dto.getImage())
                .setType(1);
    }

    public static DeviceManagerProductTreeResponse convertToProduct(ProductEntity dto) {
        return new DeviceManagerProductTreeResponse()
                .setNodeId(dto.getBizId())
                .setParentNodeId(dto.getCategoryId())
                .setName(dto.getName())
                .setProductId(dto.getId())
                .setType(2);
    }
}
