package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模块
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_module")
public class ModuleEntity extends BaseEntity {
    /**
     * 模块id
     */
    @TableField(value = "id")
    private Long id;

    /**
     * 模块名称
     */
    @TableField(value = "\"name\"")
    private String name;

    /**
     * 模块code
     */
    @TableField(value = "code")
    private String code;

    /**
     * 显示顺序
     */
    @TableField(value = "sort")
    private Integer sort;
}