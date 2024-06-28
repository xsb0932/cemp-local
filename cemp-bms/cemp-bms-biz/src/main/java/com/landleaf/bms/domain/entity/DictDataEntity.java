package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.bms.domain.enums.DictDefaultStatusEnum;
import com.landleaf.bms.domain.enums.DictStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据表（码值）
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_dict_data")
public class DictDataEntity extends TenantBaseEntity {
    /**
     * 字典数据id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型id
     */
    private Long dictId;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典数据码值
     */
    private String value;

    /**
     * 字典数据中文描述
     */
    private String label;

    /**
     * 字典数据状态 （0 正常 1 失效）{@link DictStatusEnum}
     */
    private Integer status;

    /**
     * 字典数据顺序
     */
    private Integer sort;

    /**
     * 字典数据默认状态（0 默认 1 非默认）{@link DictDefaultStatusEnum}
     */
    private Integer isDefault;
}
