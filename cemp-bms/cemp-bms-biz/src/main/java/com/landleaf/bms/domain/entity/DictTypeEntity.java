package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_dict_type")
public class DictTypeEntity extends TenantBaseEntity {
    /**
     * 字典类型id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典编码（校验租户内唯一）
     */
    private String code;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典类型 （1 系统字典 2 用户字典）{@link DictTypeEnum}
     */
    private Integer type;

    /**
     * 字典描述
     */
    private String description;

}
