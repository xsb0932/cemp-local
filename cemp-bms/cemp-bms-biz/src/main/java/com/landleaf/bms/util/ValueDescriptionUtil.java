package com.landleaf.bms.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.landleaf.bms.constance.ValueConstance;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.redis.dict.DictUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.bms.constance.ValueConstance.*;
import static com.landleaf.redis.constance.DictConstance.UNIT;

/**
 * 值描述工具
 *
 * @author yue lin
 * @since 2023/6/26 11:35
 */
@Slf4j
public class ValueDescriptionUtil {

    private ValueDescriptionUtil() {
    }

    /**
     * 获取值描述单位文本
     * @param values    描述
     * @return  结果
     */
    public static String unitToString(List<ValueDescription> values) {
        return values.stream().filter(it -> CharSequenceUtil.equals(UNIT_KEY, it.getKey()))
                .findFirst()
                .map(ValueDescription::getValue)
                .map(it -> SpringUtil.getBean(DictUtils.class).selectDictLabel(UNIT, it))
                .orElse("");
    }

    /**
     * 将值描述转为文本形式
     * @param type 数据类型
     * @param values 描述
     * @return 结果
     */
    public static String convertToString(String type, List<ValueDescription> values) {
        return switch (type) {
            case STRING -> stringValueConvert(values);
            case INTEGER, DOUBLE -> numberValueConvert(values);
            case BOOLEAN, ENUMERATE -> enumerateValueConvert(values);
            default -> {
                log.error("数据类型【{}】不存在", type);
                yield "";
            }
        };
    }

    /**
     * 字符串转换
     *
     * @param values 值描述
     * @return 结果
     */
    private static String stringValueConvert(List<ValueDescription> values) {
        return values.stream()
                .filter(it -> CharSequenceUtil.equals(it.getKey(), ValueConstance.STRING_KEY))
                .findFirst()
                .map(ValueDescription::getValue)
                .orElse("");
    }

    /**
     * 整形与浮点转换
     *
     * @param values 值描述
     * @return 结果
     */
    private static String numberValueConvert(List<ValueDescription> values) {
        String open = values.stream()
                .filter(it -> CharSequenceUtil.equals(it.getKey(), ValueConstance.NUMERIC_OPEN_KEY))
                .map(ValueDescription::getValue)
                .findFirst()
                .orElse("");
        String close = values.stream()
                .filter(it -> CharSequenceUtil.equals(it.getKey(), ValueConstance.NUMERIC_CLOSE_KEY))
                .map(ValueDescription::getValue)
                .findFirst()
                .orElse("");

        return open + "-" + close;
    }

    /**
     * 布尔与枚举转换
     *
     * @param values 值描述
     * @return 结果
     */
    private static String enumerateValueConvert(List<ValueDescription> values) {
        return values.stream()
                .map(it -> it.getKey() + "-" + it.getValue())
                .collect(Collectors.joining(";"));
    }


}
