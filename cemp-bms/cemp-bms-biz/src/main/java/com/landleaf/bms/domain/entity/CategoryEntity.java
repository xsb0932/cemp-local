package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品类管理-品类
 *
 * @author yue lin
 * @since 2023/7/6 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category_management_category")
public class CategoryEntity extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务ID
     */
    @TableField(value = "biz_id")
    private String bizId;

    /**
     * 品类名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 上级目录ID，没有父级则为0
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 品类编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 图片
     */
    @TableField(value = "image")
    private String image;

    /**
     * 品类描述
     */
    @TableField(value = "description")
    private String description;

}
