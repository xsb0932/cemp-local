package com.landleaf.bms.domain.request;

import cn.hutool.core.lang.Assert;
import com.landleaf.bms.api.json.FunctionParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 事件、服务参数
 *
 * @author yue lin
 * @since 2023/6/25 13:09
 */
@Data
public class FunctionParameterParam {

    /**
     * 事件参数/服务参数
     */
    @Valid
    @NotEmpty(message = "[事件参数/服务参数]不能为空")
    @Schema(description = "事件参数/服务参数")
    List<FunctionParam> functionParameter;

    /**
     * 响应参数
     */
    @Valid
    @Schema(description = "响应参数")
    List<FunctionParam> responseParameter;

    @Data
    public static class FunctionParam extends ValueDescriptionParam {
        /**
         * 字段标识符
         */
        @NotEmpty(message = "字段标识符不能为空")
        @Schema(description = "字段标识符")
        private String identifier;

        /**
         * 字段名称
         */
        @NotEmpty(message = "字段名称不能为空")
        @Schema(description = "字段名称")
        private String name;

        public FunctionParameter toFunctionParameter() {
            FunctionParameter functionParameter = new FunctionParameter();
            functionParameter.setIdentifier(this.identifier);
            functionParameter.setName(this.name);
            functionParameter.setDataType(getDataType());
            functionParameter.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
            return functionParameter;
        }

    }

    /**
     * 校验值描述
     */
    public void validate() {
        // 校验标识符是否重复
        List<String> identifier1 = functionParameter.stream().map(FunctionParam::getIdentifier).distinct().toList();
        Assert.isTrue(identifier1.size() == functionParameter.size(), "[事件参数/服务参数]字段标识符重复");
        for (FunctionParam functionParam : this.functionParameter) {
            functionParam.validate();
        }
        // 校验标识符是否重复
        List<String> identifier2 = responseParameter.stream().map(FunctionParam::getIdentifier).distinct().toList();
        Assert.isTrue(identifier2.size() == responseParameter.size(), "[响应参数]字段标识符重复");
        for (FunctionParam functionParam : this.responseParameter) {
            functionParam.validate();
        }
    }

}
