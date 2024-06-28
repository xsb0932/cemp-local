package com.landleaf.oauth.domain.response;

import com.landleaf.oauth.domain.entity.ModuleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模块列表
 *
 * @author yue lin
 * @since 2023/6/9 15:08
 */
@Data
@Schema(description = "模块列表")
public class ModuleResponse {

    /**
     * 模块ID
     */
    @Schema(description = "模块ID", example = "1")
    private Long moduleId;

    /**
     * 模块Code
     */
    @Schema(description = "模块Code", example = "code")
    private String moduleCode;

    /**
     * 模块名
     */
    @Schema(description = "模块名", example = "模块名")
    private String moduleName;

    public static ModuleResponse fromEntity(ModuleEntity entity) {
        ModuleResponse moduleResponse = new ModuleResponse();
        moduleResponse.setModuleId(entity.getId());
        moduleResponse.setModuleCode(entity.getCode());
        moduleResponse.setModuleName(entity.getName());
        return moduleResponse;
    }

}
