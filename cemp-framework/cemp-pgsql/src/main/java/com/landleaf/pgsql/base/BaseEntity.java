package com.landleaf.pgsql.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库实体通用字段
 *
 * @author yue lin
 * @since 2023/6/1 9:19
 */
@Data
public class BaseEntity {

    /**
     * 创建者
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private Long creator;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者")
    private Long updater;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0否 1是）
     */
    @TableLogic
    @TableField(value = "deleted")
    private Short deleted = 0;

}
