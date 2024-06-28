package com.landleaf.comm.base.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.landleaf.comm.exception.ErrorCode;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.util.servlet.ServletUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 统一返回参数
 * <p>
 * 基础封装泛型类中,不能使用@Schema注解来约束该泛型类的类名称，因为泛型类一旦用该注解进行约束后,在OpenAPI的结构中,类名称就只有一个，会导致字段属性找不到的情况
 * 针对泛型T的属性，不应该在使用@Schema注解,交由swagger自己处理
 * </p>
 *
 * @param <T>
 * @author wenyilu
 */
public class Response<T> implements Serializable {

    /**
     * 请求id，系统异常时需要将此参数传递到前台去
     */
    @Schema(name = "requestId", example = "1", description = "请求id，系统异常时需要将此参数传递到前台去")
    private String requestId;

    /**
     * 请求是否处理成功
     */
    @Schema(name = "success", example = "true", description = "请求是否处理成功")
    private boolean success;

    /**
     * 业务异常错误代码
     */
    @Schema(name = "errorCode", example = "1003000000", description = "异常错误代码")
    private String errorCode;

    /**
     * 业务异常错误信息
     */
    @Schema(name = "errorMsg", example = "1003000000", description = "异常错误信息")
    private String errorMsg;

    /**
     * 提示消息，需要进行国际化
     */
    @Schema(name = "message", example = "提示消息", description = "提示消息")
    private String message;

    /**
     * 正常返回参数
     */
    private T result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }


    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * <p>
     * 因为 A 方法返回的 Response 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 Response 对象
     */
    public static <T> Response<T> error(Response<?> result) {
        return error(result.getErrorCode(), result.getMessage());
    }

    public static <T> Response<T> error(String code, String message) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        Response<T> result = new Response<>();
        result.errorCode = code;
        result.errorMsg = message;
        result.success = false;
        HttpServletRequest request = ServletUtils.getRequest();
        result.requestId = request == null ? null : request.getRequestId();
        return result;
    }

    public static <T> Response<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> Response<T> success(T data) {
        Response<T> result = new Response<>();
        result.errorCode = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.result = data;
        result.message = "";
        result.success = true;
        HttpServletRequest request = ServletUtils.getRequest();
        result.requestId = request == null ? null : request.getRequestId();
        return result;
    }

    public static <T> Response<T> success() {
        Response<T> result = new Response<>();
        result.errorCode = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.result = null;
        result.message = "";
        result.success = true;
        HttpServletRequest request = ServletUtils.getRequest();
        result.requestId = request == null ? null : request.getRequestId();
        return result;
    }

    public static <T> Response<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }

    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     */
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }
        // 业务异常
        throw new ServiceException(errorCode, errorMsg);
    }

    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     * 如果没有，则返回 {@link #result} 数据
     */
    @JsonIgnore // 避免 jackson 序列化
    public T getCheckedData() {
        checkError();
        return result;
    }

}
