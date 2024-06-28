package com.landleaf.mail.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "tb_mail")
public class MailEntity extends BaseEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板类型(1创建用户2重置密码3创建租户)
     */
    @TableField(value = "type")
    private Short type;

    /**
     * 邮件模板
     */
    @TableField(value = "format")
    private String format;

}

