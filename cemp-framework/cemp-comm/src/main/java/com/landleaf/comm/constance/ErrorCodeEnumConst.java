package com.landleaf.comm.constance;

/**
 * 错误码定义
 *
 * @author wenyilu
 */
public enum ErrorCodeEnumConst {

    /*
     * 查验参数错误
     */
    CHECK_PARAM_ERROR(-1, "查验参数错误"),

    /*
     * 数据库插入异常
     */
    DATA_INSERT_ERROR(2, "数据库插入异常"),

    /*
     * 修改的数据不存在
     */
    NULL_VALUE_ERROR(3, "修改的数据不存在"),

    /*
     * 日期格式错误
     */
    DATE_FORMAT_ERROR(4, "日期格式错误"),
    ;

    /**
     * 编码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCodeEnumConst(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
