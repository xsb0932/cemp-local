package com.landleaf.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 视图实体类
 *
 * @author hebin
 * @since 2023-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ViewEntity对象", description = "视图")
@TableName("tb_view")
public class ViewEntity extends TenantBaseEntity {

    /**
     * 视图id
     */
    @Schema(description = "视图id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 视图名称
     */
    @Schema(description = "视图名称")
    private String name;

    /**
     * 视图类型（字典配置）
     */
    @Schema(description = "视图类型（字典配置）")
    private Integer viewType;

    /**
     * 发布状态（0未发布 1已发布）
     */
    @Schema(description = "发布状态（0未发布 1已发布）")
    private Integer status;

    /**
     * avue项目地址
     */
    @Schema(description = "avue项目编辑地址")
    private String url;

    /**
     * 项目展示地址
     */
    @Schema(description = "项目展示地址")
    private String viewUrl;

    /**
     * 类型（0avue 1项目定制）
     */
    @Schema(description = "类型（0avue 1项目定制）")
    private Integer customType;

    /**
     * 视图排序序号
     */
    @Schema(description = "视图排序序号")
    private Integer sort;

}