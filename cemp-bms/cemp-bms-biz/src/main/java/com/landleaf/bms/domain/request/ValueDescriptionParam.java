package com.landleaf.bms.domain.request;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.landleaf.bms.api.json.ValueDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.regex.Pattern;

import static com.landleaf.bms.constance.ValueConstance.*;

/**
 * 值描述请求参数
 *
 * @author yue lin
 * @since 2023/6/27 10:50
 */
@Data
public class ValueDescriptionParam {

    /**
     * 判断是否为整形或浮点，正则表达式
     */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");

    /**
     * 值描述
     */
    @Valid
    @NotEmpty(message = "值描述不能为空")
    @Schema(description = "值描述")
    private List<ValueAccount> valueDescription;

    /**
     * 数据类型（字典编码-PARAM_DATA_TYPE）
     */
    @NotBlank(message = "数据类型不能为空")
    @Schema(description = "数据类型（字典编码-PARAM_DATA_TYPE）【01,02,03,04,05】", allowableValues = {"01", "02", "03", "04", "05"})
    private String dataType;

    @Data
    public static class ValueAccount {
        /**
         * key值
         * 01【LENGTH】， 02-03【MIN， MAX， UNIT】， 04【TRUE， FALSE】， 05【自定义】,
         */
        @NotBlank(message = "值描述-key不能为空")
        @Schema(description = "值描述-key值，01【LENGTH】， 02-03【MIN， MAX， UNIT】， 04【TRUE， FALSE】， 05【自定义】")
        private String key;

        /**
         * value值
         */
        @NotBlank(message = "值描述-value不能为空")
        @Schema(description = "值描述-value值")
        private String value;

        public ValueDescription toValueDescription() {
            ValueDescription description = new ValueDescription();
            description.setKey(key);
            description.setValue(value);
            return description;
        }

    }

    /**
     * 校验值描述
     */
    public void validate() {
        boolean match = switch (dataType) {
            case STRING -> valueDescription.size() == 1
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), STRING_KEY));
            case INTEGER, DOUBLE -> valueDescription.size() == 3
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), NUMERIC_OPEN_KEY))
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), NUMERIC_CLOSE_KEY))
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), UNIT_KEY))
                    && valueDescription.stream().filter(it -> CharSequenceUtil.equalsAny(it.getKey(), NUMERIC_OPEN_KEY, NUMERIC_CLOSE_KEY))
                    .map(ValueAccount::getValue).allMatch(it -> NUMERIC_PATTERN.matcher(it).matches());
            case BOOLEAN -> valueDescription.size() == 2
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), BOOLEAN_TRUE_KEY))
                    && valueDescription.stream().anyMatch(it -> CharSequenceUtil.equals(it.getKey(), BOOLEAN_FALSE_KEY));
            case ENUMERATE -> CollUtil.isNotEmpty(this.valueDescription);
            default -> throw new IllegalStateException("数据类型【" + dataType + "】参数错误");
        };
        Assert.isTrue(match, "值描述参数异常");
    }

}
