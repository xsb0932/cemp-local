package com.landleaf.bms.domain.response;

import lombok.Data;

/**
 * CategoryTreeResponse
 *
 * @author 张力方
 * @since 2023/7/6
 **/
@Data
public class CategoryTreeListResponse {
    /**
     * id
     */
    private String id;

    /**
     * parentId
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否是目录
     */
    private Boolean isCatalogue;

}
