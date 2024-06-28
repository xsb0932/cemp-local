package com.landleaf.comm.exception;


import com.landleaf.comm.constance.ErrorCodeEnumConst;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author wyl
 * @version 1.0
 * @description
 * @date 2019年03月20日 17:29
 */
@SuppressWarnings("serial")
public class BusinessException extends RuntimeException implements Serializable {

    /**
     * 异常国际化编码
     */
    protected String errCode;

    /**
     * 异常参数
     */
    private Object[] arguments;

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
        this.errCode = msg;
    }

    public BusinessException(Integer errCode, String message) {
        super(message);
        this.errCode = Objects.toString(errCode);
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.errCode = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errCode = errorCode.getCode();
    }

    public BusinessException(ErrorCodeEnumConst ec) {
        super(ec.getMessage());
        this.errCode = String.valueOf(ec.getCode());
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = code;
    }

    public BusinessException(String code, Object... args) {
        this.errCode = code;
        this.arguments = args;
    }

    public BusinessException(String code, String msg, Object... args) {
        super(msg);
        this.errCode = code;
        this.arguments = args;
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
    }

    public void setErrorArguments(Object... args) {
        this.arguments = args;
    }

    public Object[] getErrorArguments() {
        return this.arguments;
    }

    public String getErrorCode() {
        return this.errCode;
    }
}
