package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品类管理-目录
 *
 * @author yue lin
 * @since 2023/7/6 9:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category_management_catalogue")
public class CategoryCatalogueEntity extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 目录名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 上级目录ID，没有父级则为0
     */
    @TableField(value = "parent_id")
    private Long parentId;

}
