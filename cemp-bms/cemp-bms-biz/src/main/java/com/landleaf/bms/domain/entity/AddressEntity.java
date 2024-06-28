package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 行政区域实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "行政区域")
@TableName("tb_address")
public class AddressEntity extends BaseEntity {

    /**
     * 自增id
     */
    @Schema(description = "自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 编码
     */
    @Schema(description = "编码")
    private String addressCode;

    /**
     * 地址名称
     */
    @Schema(description = "地址名称")
    private String addressName;

    /**
     * 地址类型:1->省;2->市;3->区;
     */
    @Schema(description = "地址类型:1->省;2->市;3->区;")
    private Integer addressType;

    /**
     * 父地址编码
     */
    @Schema(description = "父地址编码")
    private String parentCode;

    /**
     * 对应气象局中相应区域code
     */
    @Schema(description = "对应气象局中相应区域code")
    private String weatherCode;

    /**
     * 对应气象局中相应区域name
     */
    @Schema(description = "对应气象局中相应区域name")
    private String weatherName;
}