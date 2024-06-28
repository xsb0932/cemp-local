package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 产品
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@TableName(value = "tb_product")
public class ProductEntity extends TenantBaseEntity {
    /**
     * 产品id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 所属品类id,业务id
     */
    private String categoryId;
    /**
     * 产品业务id
     */
    private String bizId;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 产品型号
     */
    private String model;
    /**
     * 产品厂商
     */
    private String factory;
    /**
     * 产品状态 - 数据字典 PRODUCT_COMMUNICATION_TYPE
     * <p>
     * 1、已经发布的产品不可编辑
     * 2、产品有关联设备时不可改为未发布
     */
    private Integer status;
    /**
     * 实体产品
     */
    private Boolean isReal;
    /**
     * 联网方式 - 数据字典 PRODUCT_STATUS
     */
    private String communicationType;
    /**
     * 长连设备
     */
    private Boolean isLongOnline;
    /**
     * 异常超时
     */
    private Double timeout;
    /**
     * 产品描述
     */
    private String description;
}
