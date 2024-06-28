package com.landleaf.bms.domain.response;

import cn.hutool.extra.spring.SpringUtil;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目-空间树状结构
 *
 * @author yue lin
 * @since 2023/7/12 14:40
 */
@Data
public class ProjectSpaceTreeResponse {

    /**
     * 区域ID
     */
    @Schema(description = "区域ID")
    private Long spaceId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long projectId;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;

    /**
     * 区域名称
     */
    @Schema(description = "区域名称")
    private String spaceName;

    /**
     * 区域父级ID
     */
    @Schema(description = "区域父级ID")
    private Long parentId;

    /**
     * 区域父级名称
     */
    @Schema(description = "区域父级名称")
    private String parentName;

    /**
     * 区域类型
     */
    @Schema(description = "区域类型")
    private String spaceType;

    /**
     * 区域类型名称
     */
    @Schema(description = "区域类型名称")
    private String spaceTypeName;

    /**
     * 区域面积
     */
    @Schema(description = "区域面积")
    private BigDecimal spaceProportion;

    /**
     * 区域备注
     */
    @Schema(description = "区域备注")
    private String spaceDescription;

    /**
     * 子集
     */
    @Schema(description = "子集")
    private List<ProjectSpaceTreeResponse> children;

    public static ProjectSpaceTreeResponse from(ProjectSpaceEntity entity, String parentName) {
        ProjectSpaceTreeResponse response = new ProjectSpaceTreeResponse();
        response.setSpaceId(entity.getId());
        response.setBizId(entity.getBizId());
        response.setProjectId(entity.getProjectId());
        response.setSpaceName(entity.getName());
        response.setParentId(entity.getParentId());
        response.setParentName(parentName);
        response.setSpaceType(entity.getType());
        response.setSpaceTypeName(SpringUtil.getBean(DictUtils.class).selectDictLabel(DictConstance.CNF_SUBAREA_TYPE, entity.getType()));
        response.setSpaceProportion(entity.getProportion());
        response.setSpaceDescription(entity.getDescription());
        return response;
    }

}
