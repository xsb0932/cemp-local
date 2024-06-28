package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典使用记录表
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_dict_used_record")
public class DictUsedRecordEntity extends TenantBaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 码值id
     */
    private Long dictDataId;

    /**
     * 使用方唯一标识，比如 表名+id
     */
    private String uniqueCode;
}
