package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.DictTypeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DictTypeListResponse
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@Schema(name = "字典类型返回参数", description = "字典类型返回参数")
public class DictTypeListResponse {
    /**
     * 字典类型
     * 系统 + 各租户
     */
    @Schema(description = "字典类型", example = "系统 + 各租户")
    private String dictType;

    /**
     * 字典列表
     */
    @Schema(description = "字典列表", example = "[1,2,3]")
    private List<Dict> dictList;

    @Data
    @Schema(name = "字典返回参数", description = "字典返回参数")
    public static class Dict {
        /**
         * 字典id
         */
        @Schema(description = "字典id", example = "1")
        private Long dictId;
        /**
         * 字典名称
         */
        @Schema(description = "字典名称", example = "城市")
        private String dictName;
        /**
         * 字典编码
         */
        @Schema(description = "字典编码", example = "01")
        private String dictCode;

        public static List<Dict> covertFrom(List<DictTypeEntity> dictTypeEntityList) {
            List<DictTypeListResponse.Dict> dictList = new ArrayList<>();
            for (DictTypeEntity systemDictType : dictTypeEntityList) {
                DictTypeListResponse.Dict dict = new DictTypeListResponse.Dict();
                dict.setDictId(systemDictType.getId());
                dict.setDictCode(systemDictType.getCode());
                dict.setDictName(systemDictType.getName());
                dictList.add(dict);
            }
            return dictList;
        }
    }

}
