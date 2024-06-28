package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品返回参数
 *
 * @author xushibai
 * @since 2023/7/27
 **/
@Data
@Schema(name = "产品返回参数", description = "产品返回参数")
public class ProductDetailResponse {
    /**
     * 产品id
     */
    @Schema(description = "产品id", example = "1")
    private Long id;
    /**
     * 产品业务id
     */
    @Schema(description = "产品业务id", example = "1")
    private String bizId;
    /**
     * 所属品类id
     */
    @Schema(description = "所属品类id", example = "1")
    private String categoryId;
    /**
     * 所属品类名称
     */
    @Schema(description = "所属品类名称", example = "1")
    private String categoryName;
    /**
     * 产品名称
     */
    @Schema(description = "产品名称", example = "XXX")
    private String name;
    /**
     * 产品型号
     */
    @Schema(description = "产品型号", example = "XXX")
    private String model;
    /**
     * 产品厂商
     */
    @Schema(description = "产品厂商", example = "XXX")
    private String factory;
    /**
     * 产品状态 - 数据字典 PRODUCT_STATUS
     */
    @Schema(description = "产品状态", example = "XXX")
    private Integer status;
    /**
     * 产品状态描述
     */
    @Schema(description = "产品状态描述", example = "XXX")
    private String statusLabel;
    /**
     * 是否实体产品
     */
    @Schema(description = "是否实体产品", example = "true")
    private Boolean isReal;
    /**
     * 联网方式 - 数据字典 PRODUCT_COMMUNICATION_TYPE
     */
    @Schema(description = "联网方式", example = "XXX")
    private Integer communicationType;
    /**
     * 联网方式描述
     */
    @Schema(description = "联网方式描述", example = "XXX")
    private String communicationTypeLabel;
    /**
     * 是否长连设备
     */
    @Schema(description = "是否长连设备", example = "true")
    private Boolean isLongOnline;
    /**
     * 异常超时
     */
    @Schema(description = "异常超时", example = "100")
    private Double timeout;
    /**
     * 产品描述
     */
    @Schema(description = "产品描述", example = "XXX")
    private String description;
    /**
     * 是否有关联设备
     * <p>
     * 如果有关联设备则产品不能被修改为未发布状态
     */
    @Schema(description = "是否有关联设备", example = "XXX")
    private Boolean refDevices;
    /**
     * 是否产品库产品
     * <p>
     * 如果是则不能修改删除
     */
    @Schema(description = "是否产品库产品", example = "XXX")
    private Boolean isRepo;
    /**
     * 品类图片
     */
    @Schema(description = "品类图片", example = "XXX")
    private String imageUrl;
}
