package com.landleaf.bms.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.landleaf.bms.constance.ValueConstance;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FunctionParameter工具类
 *
 * @author yue lin
 * @since 2023/6/27 17:27
 */
public class FunctionParameterUtil {

    private FunctionParameterUtil() {
    }

    // {参数标识符}：{参数名称}：{参数数据类型}：{参数值描述}：{单位}
    public static String convertToString(List<FunctionParameter> values) {
        DictUtils dictUtils = SpringUtil.getBean(DictUtils.class);
        return values.stream()
                .map(it -> "{" + it.getIdentifier() + ":" +
                        it.getName() + ":" +
                        dictUtils.selectDictLabel(DictConstance.PARAM_DATA_TYPE, it.getDataType()) + ":" +
                        ValueDescriptionUtil.convertToString(it.getDataType(), it.getValueDescription()) +
                        it.getValueDescription().stream().filter(valueDescription -> CharSequenceUtil.equals(valueDescription.getKey(), ValueConstance.UNIT_KEY))
                                .map(valueDescription -> ":" + valueDescription.getValue()).findFirst().orElse("")
                        + "}"
                ).collect(Collectors.joining(","));
    }

}
