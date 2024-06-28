package com.landleaf.lgc.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个性配置数据
 *
 * @author 张力方
 * @since 2023/8/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_custom_function_conf")
public class CustomFunctionConfEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 数据项名称
     */
    private String name;
    /**
     * 数据项编码
     */
    private String code;
    /**
     * 项目业务id
     */
    private String bizProjectId;
    /**
     * 数据项值，大对象
     */
    private String value;
}
