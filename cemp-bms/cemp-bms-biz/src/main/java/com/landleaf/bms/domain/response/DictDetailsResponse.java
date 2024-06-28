package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.DictDataEntity;
import com.landleaf.bms.domain.enums.DictDefaultStatusEnum;
import com.landleaf.bms.domain.enums.DictStatusEnum;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典详情返回参数
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@Schema(name = "字典详情返回参数", description = "字典详情返回参数")
public class DictDetailsResponse {
    /**
     * 字典id
     */
    @Schema(description = "字典id", example = "1")
    private Long dictId;

    /**
     * 租户id
     */
    @Schema(description = "租户id", example = "1")
    private Long tenantId;

    /**
     * 字典类型 （1 系统字典 2 租户字典）{@link DictTypeEnum}
     */
    @Schema(description = "字典类型 （1 系统字典 2 租户字典）", example = "1")
    private Integer type;

    /**
     * 字典编码
     */
    @Schema(description = "字典编码", example = "USER_STATUS")
    private String code;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "项目业态")
    private String name;

    /**
     * 字典描述
     */
    @Schema(description = "字典描述", example = "项目业态xxxx")
    private String description;

    /**
     * 字典数据（码值）列表
     */
    @Schema(description = "字典数据（码值）列表", example = "[1,2,3]")
    private List<DictData> dictDataList;

    @Data
    @Schema(name = "字典数据返回参数", description = "字典数据返回参数")
    public static class DictData {
        /**
         * 字典数据id（码值id）
         */
        @Schema(description = "字典数据id（码值id）", example = "1")
        private Long id;

        /**
         * 字典类型id
         */
        @Schema(description = "字典类型id", example = "1")
        private Long dictId;

        /**
         * 字典编码
         */
        @Schema(description = "字典编码", example = "1")
        private String dictCode;

        /**
         * 字典数据码值
         */
        @Schema(description = "字典数据码值", example = "1")
        private String value;

        /**
         * 字典数据中文描述
         */
        @Schema(description = "字典数据中文描述", example = "酒店")
        private String label;

        /**
         * 字典数据状态 （0 正常 1 失效）{@link DictStatusEnum}
         */
        @Schema(description = "字典数据状态 （0 正常 1 失效）", example = "1")
        private Integer status;

        /**
         * 字典数据顺序
         */
        @Schema(description = "字典数据顺序", example = "1")
        private Integer sort;

        /**
         * 字典数据默认状态（0 默认 1 非默认）{@link DictDefaultStatusEnum}
         */
        @Schema(description = "字典数据默认状态（0 默认 1 非默认）", example = "1")
        private Integer isDefault;

        public static List<DictData> convertFrom(List<DictDataEntity> dictDataEntityList) {
            List<DictData> dictDataList = new ArrayList<>();
            for (DictDataEntity dictDataEntity : dictDataEntityList) {
                DictData dictData = new DictData();
                dictData.setId(dictDataEntity.getId());
                dictData.setDictId(dictDataEntity.getDictId());
                dictData.setSort(dictDataEntity.getSort());
                dictData.setLabel(dictDataEntity.getLabel());
                dictData.setStatus(dictDataEntity.getStatus());
                dictData.setValue(dictDataEntity.getValue());
                dictData.setIsDefault(dictDataEntity.getIsDefault());
                dictDataList.add(dictData);
            }
            return dictDataList;
        }
    }
}
