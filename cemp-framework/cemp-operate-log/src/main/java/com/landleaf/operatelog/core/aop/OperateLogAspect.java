package com.landleaf.operatelog.core.aop;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.IpUtils;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.comm.util.servlet.ServletUtils;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.context.OperateLogContext;
import com.landleaf.operatelog.core.dal.OperateLogEntity;
import com.landleaf.operatelog.core.dal.OprUserEntity;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import com.landleaf.operatelog.core.service.OperateLogFrameworkService;
import com.landleaf.operatelog.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;


/**
 * 拦截使用 @OperateLog 注解，如果满足条件，则生成操作日志。
 * <p>
 * 但是，如果声明 @OperateLog 注解时，将 enable 属性设置为 false 时，强制不记录。
 *
 * @author 粒方
 */
@Aspect
@Slf4j
public class OperateLogAspect {

    /**
     * 用于记录操作内容的上下文
     *
     * @see com.landleaf.operatelog.core.dal.OperateLogEntity#getContent()
     */

    private final UserService userService;
    private final OperateLogFrameworkService operateLogFrameworkService;


    public OperateLogAspect(OperateLogFrameworkService operateLogFrameworkService,UserService userService) {
        this.operateLogFrameworkService = operateLogFrameworkService;
        this.userService = userService;
    }

    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {
        return around0(joinPoint, operateLog);
    }

    private Object around0(ProceedingJoinPoint joinPoint,
                           OperateLog operateLog) throws Throwable {
        // 记录开始时间
        LocalDateTime startTime = LocalDateTime.now();
        try {
            // 执行原有方法
            Object result = joinPoint.proceed();
            // 记录正常执行时的操作日志
            this.log(joinPoint, operateLog, startTime, result, null);
            return result;
        } catch (Throwable exception) {
            this.log(joinPoint, operateLog, startTime, null, exception);
            throw exception;
        } finally {
            clearThreadLocal();
        }
    }

    private static void clearThreadLocal() {
        OperateLogContext.release();
    }

    private void log(ProceedingJoinPoint joinPoint,
                     OperateLog operateLog,
                     LocalDateTime startTime, Object result, Throwable exception) {
        try {
            // 判断不记录的情况
            if (!isLogEnable(operateLog)) {
                return;
            }
            // 真正记录操作日志
            this.log0(joinPoint, operateLog, startTime, result, exception);
        } catch (Throwable ex) {
            log.error("[log][记录操作日志时，发生异常，其中参数是 joinPoint({}) operateLog({}) result({}) exception({}) ]",
                    joinPoint, operateLog, result, exception, ex);
        }
    }

    private void log0(ProceedingJoinPoint joinPoint,
                      OperateLog operateLog,
                      LocalDateTime startTime, Object result, Throwable exception) {
        System.out.println(obtainMethodArgs(joinPoint));
        OperateLogEntity operateLogEntity = new OperateLogEntity();
        // 补全通用字段
        HttpServletRequest request = ServletUtils.getRequest();
        operateLogEntity.setRequestId(request == null ? null : request.getRequestId());
        operateLogEntity.setStartTime(startTime);
        fillUserFields(operateLogEntity);
        // 补全模块信息
        fillModuleFields(operateLogEntity, joinPoint, operateLog);
        // 补全请求信息
        fillRequestFields(operateLogEntity);
        // 补全方法信息
        fillMethodFields(operateLogEntity, joinPoint, operateLog, startTime, result, exception);
        // 补充用户信息
        if(StringUtils.equals("登录",operateLog.name()) && result instanceof Response<?> response){
            JSONObject obj = JSONObject.parseObject(JSON.toJSONString(response.getResult()));
            Long userId = obj.getJSONObject("authInfo").getLong("userId");
            Long tenantId = obj.getJSONObject("authInfo").getLong("tenantId");
            if(userId != null){
                operateLogEntity.setUserId(userId);
                TenantContext.setTenantId(tenantId);
            }
        }
        if(StringUtils.equals("找回密码",operateLog.name())){
            JSONObject obj = JSONObject.parseObject(JSON.toJSONString(joinPoint.getArgs()[0]));
            String account = obj.getString("account");
            //根据账号/手机 查询用户
            OprUserEntity user = userService.queryByAccount(account);
            operateLogEntity.setUserId(user.getId());
            TenantContext.setTenantId(user.getTenantId());
            System.out.println(account);
        }
        if(StringUtils.equals("产品库-编辑产品状态",operateLog.name())){
            JSONObject obj = JSONObject.parseObject(JSON.toJSONString(joinPoint.getArgs()[0]));
            if(obj.containsKey("status")){
                if (obj.getInteger("status")== 1){
                    operateLogEntity.setName("产品启用");
                }else{
                    operateLogEntity.setName("产品停用");
                }
            }
        }

        TenantContext.setIgnore(false);
        // 异步记录日志
        operateLogFrameworkService.createOperateLog(operateLogEntity);
    }

    private static void fillUserFields(OperateLogEntity operateLogEntity) {
        operateLogEntity.setUserId(LoginUserUtil.getLoginUserId());
    }

    private static void fillModuleFields(OperateLogEntity operateLogEntity,
                                         ProceedingJoinPoint joinPoint,
                                         OperateLog operateLog) {
        // module 属性
        if (operateLog != null) {
            operateLogEntity.setModule(operateLog.module()[0].getName());
        }
        // name 属性
        if (operateLog != null) {
            operateLogEntity.setName(operateLog.name());
        }
        Operation operation = getMethodAnnotation(joinPoint, Operation.class);
        if (CharSequenceUtil.isEmpty(operateLogEntity.getName()) && operation != null) {
            operateLogEntity.setName(operation.summary());
        }
        // type 属性
        if (operateLog != null && ArrayUtil.isNotEmpty(operateLog.type())) {
            operateLogEntity.setType(operateLog.type()[0].getType());
        }
        if (operateLogEntity.getType() == null) {
            RequestMethod requestMethod = obtainFirstMatchRequestMethod(obtainRequestMethod(joinPoint));
            OperateTypeEnum operateLogType = convertOperateLogType(requestMethod);
            operateLogEntity.setType(operateLogType != null ? operateLogType.getType() : null);
        }
        // content
        operateLogEntity.setContent(OperateLogContext.getContent());
    }

    private static void fillRequestFields(OperateLogEntity operateLogEntity) {
        // 获得 Request 对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // 补全请求信息
        operateLogEntity.setRequestMethod(request.getMethod());
        operateLogEntity.setRequestUrl(request.getRequestURI());
        String ipAddr = IpUtils.getIpAddr();
        operateLogEntity.setUserIp(ipAddr);
        try {
            String macAddress = NetUtil.getMacAddress(InetAddress.getByName(ipAddr));
            operateLogEntity.setUserMac(macAddress);
        } catch (UnknownHostException e) {
            log.error("MAC 地址获取失败", e);
        }
        operateLogEntity.setUserAgent(ServletUtils.getUserAgent(request));
    }

    private static void fillMethodFields(OperateLogEntity operateLogEntity,
                                         ProceedingJoinPoint joinPoint,
                                         OperateLog operateLog,
                                         LocalDateTime startTime, Object result, Throwable exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        operateLogEntity.setJavaMethod(methodSignature.toString());
        if (operateLog == null || operateLog.logArgs()) {
            operateLogEntity.setJavaMethodArgs(obtainMethodArgs(joinPoint));
        }
        if (operateLog == null || operateLog.logResultData()) {
            operateLogEntity.setResultData(obtainResultData(result));
        }
        operateLogEntity.setDuration((int) (LocalDateTimeUtil.between(startTime, LocalDateTime.now()).toMillis()));
        // （正常）处理 resultCode 和 resultMsg 字段
        if (result instanceof Response<?> response) {
            operateLogEntity.setResultCode(response.getErrorCode());
            operateLogEntity.setResultMsg(response.getErrorMsg());
        } else {
            operateLogEntity.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());
        }
        // （异常）处理 resultCode 和 resultMsg 字段
        if (exception != null) {
            operateLogEntity.setResultCode(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode());
            operateLogEntity.setResultMsg(ExceptionUtil.getRootCauseMessage(exception));
        }
    }

    private static boolean isLogEnable(OperateLog operateLog) {
        // 有 @OperateLog 注解的情况下
        if (operateLog != null) {
            return operateLog.enable();
        }
        return false;
    }

    private static RequestMethod obtainFirstLogRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtil.isEmpty(requestMethods)) {
            return null;
        }
        return Arrays.stream(requestMethods).filter(requestMethod ->
                        requestMethod == RequestMethod.POST
                                || requestMethod == RequestMethod.PUT
                                || requestMethod == RequestMethod.DELETE)
                .findFirst().orElse(null);
    }

    private static RequestMethod obtainFirstMatchRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtil.isEmpty(requestMethods)) {
            return null;
        }
        // 优先，匹配最优的 POST、PUT、DELETE
        RequestMethod result = obtainFirstLogRequestMethod(requestMethods);
        if (result != null) {
            return result;
        }
        // 然后，匹配次优的 GET
        result = Arrays.stream(requestMethods).filter(requestMethod -> requestMethod == RequestMethod.GET)
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // 兜底，获得第一个
        return requestMethods[0];
    }

    private static OperateTypeEnum convertOperateLogType(RequestMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        return switch (requestMethod) {
            case GET -> OperateTypeEnum.GET;
            case POST -> OperateTypeEnum.CREATE;
            case PUT -> OperateTypeEnum.UPDATE;
            case DELETE -> OperateTypeEnum.DELETE;
            default -> OperateTypeEnum.OTHER;
        };
    }

    private static RequestMethod[] obtainRequestMethod(ProceedingJoinPoint joinPoint) {
        RequestMapping requestMapping = AnnotationUtils.getAnnotation( // 使用 Spring 的工具类，可以处理 @RequestMapping 别名注解
                ((MethodSignature) joinPoint.getSignature()).getMethod(), RequestMapping.class);
        return requestMapping != null ? requestMapping.method() : new RequestMethod[]{};
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(annotationClass);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getClassAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getAnnotation(annotationClass);
    }

    private static String obtainMethodArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        // 拼接参数
        Map<String, Object> args = Maps.newHashMapWithExpectedSize(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            // 被忽略时，标记为 ignore 字符串，避免和 null 混在一起
            args.put(argName, !isIgnoreArgs(argValue) ? argValue : "[ignore]");
        }
        return JSON.toJSONString(args);
    }

    private static String obtainResultData(Object result) {
        if (result instanceof Response<?>) {
            result = ((Response<?>) result).getResult();
        }
        return JSON.toJSONString(result);
    }

    private static boolean isIgnoreArgs(Object object) {
        Class<?> clazz = object.getClass();
        // 处理数组的情况
        if (clazz.isArray()) {
            return IntStream.range(0, Array.getLength(object))
                    .anyMatch(index -> isIgnoreArgs(Array.get(object, index)));
        }
        // 递归，处理数组、Collection、Map 的情况
        if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>) object).stream()
                    .anyMatch((Predicate<Object>) OperateLogAspect::isIgnoreArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return isIgnoreArgs(((Map<?, ?>) object).values());
        }
        // obj
        return object instanceof MultipartFile
                || object instanceof HttpServletRequest
                || object instanceof HttpServletResponse
                || object instanceof BindingResult;
    }

}
