package com.landleaf.bms.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ProductDeviceAttr
 *
 * @author xushibai
 * @since 2023/11/2
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeviceAttrMapResponse {

//    @Schema(name = "枚举")
//    private Map<String,Map<String,String>> desc;
    /**
     * id
     */
    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 功能标识符
     */
    private String identifier;

    /**
     * 功能类别-设备属性
     */
    private String functionCategory;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
     */
    private String functionType;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 值描述
     */
    private List<ValueDescriptionResponse> valueDescription;

    @TableField(value = "rw")
    private String rw;

}
