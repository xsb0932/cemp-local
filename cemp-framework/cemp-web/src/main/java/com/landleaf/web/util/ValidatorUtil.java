package com.landleaf.web.util;

import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ValidatorUtil {
    // 也可以使用spring注入的方式
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 使用指定分组
     *
     * @param object 被校验的bean
     * @param groups 分组
     * @return
     */
    public static <T> Map<String, StringBuilder> validate(T object, Class<?>... groups) throws BusinessException {
        Map<String, StringBuilder> errorMap = new HashMap<>(16);
        if (groups == null) {
            groups = new Class[]{Default.class};
        }
        Set<ConstraintViolation<T>> set = validator.validate(object, groups);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        String property;
        for (ConstraintViolation<T> c : set) {
            // 这里循环获取错误信息，可以自定义格式
            log.error("校验参数失败：", c.getMessage());
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR.getCode(), ErrorCodeEnumConst.CHECK_PARAM_ERROR.getMessage());
        }
        return errorMap;
    }
}
